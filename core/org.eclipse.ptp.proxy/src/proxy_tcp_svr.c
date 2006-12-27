/******************************************************************************
 * Copyright (c) 2005 The Regents of the University of California. 
 * This material was produced under U.S. Government contract W-7405-ENG-36 
 * for Los Alamos National Laboratory, which is operated by the University 
 * of California for the U.S. Department of Energy. The U.S. Government has 
 * rights to use, reproduce, and distribute this software. NEITHER THE 
 * GOVERNMENT NOR THE UNIVERSITY MAKES ANY WARRANTY, EXPRESS OR IMPLIED, OR 
 * ASSUMES ANY LIABILITY FOR THE USE OF THIS SOFTWARE. If software is modified 
 * to produce derivative works, such modified software should be clearly 
 * marked, so as not to confuse it with the version available from LANL.
 * 
 * Additionally, this program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * LA-CC 04-115
 ******************************************************************************/

/*
 * The proxy handles communication between the client debug library API and the
 * client debugger, since they may be running on different hosts, and will
 * certainly be running in different processes.
 */

#include <config.h>

#include <sys/socket.h>
#include <arpa/inet.h>
#include <netdb.h>

#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>

#include "compat.h"
#include "args.h"
#include "proxy.h"
#include "proxy_event.h"
#include "proxy_tcp.h"
#include "handler.h"

static int	proxy_tcp_svr_init(proxy_svr *, void **);
static int	proxy_tcp_svr_create(proxy_svr *, int);
static int	proxy_tcp_svr_connect(proxy_svr *, char *, int);
static int  proxy_tcp_svr_handle_events(proxy_svr *, List *, struct timeval);
static int	proxy_tcp_svr_progress(proxy_svr *);
static void	proxy_tcp_svr_finish(proxy_svr *);

static int	proxy_tcp_svr_recv_msgs(int, void *);
static int	proxy_tcp_svr_accept(int, void *);
static int	proxy_tcp_svr_dispatch(proxy_tcp_conn *, char *);

proxy_svr_funcs proxy_tcp_svr_funcs =
{
	proxy_tcp_svr_init,
	proxy_tcp_svr_create,
	proxy_tcp_svr_connect,
	proxy_tcp_svr_progress,
	proxy_tcp_svr_handle_events,
	proxy_tcp_svr_finish,
};

/*
 * Called when an event is received in response to a client debug command.
 * Sends the event to the proxy peer.
 */
static void
proxy_tcp_svr_event_callback(void *ev_data, void *data)
{
	proxy_event *	ev = (proxy_event *)ev_data;
	proxy_tcp_conn *	conn = (proxy_tcp_conn *)data;
	char *			str;
	
	if (proxy_event_to_str(ev, &str) < 0) {
		/*
		 * TODO should send an error back to proxy peer
		 */
		fprintf(stderr, "proxy_tcp_svr_event_callback: event conversion failed\n");
		return;
	}

	DEBUG_PRINT("SVR reply <%s>\n", str);
	
	(void)proxy_tcp_send_msg(conn, str, strlen(str));
	free(str);
}

static int
proxy_tcp_svr_init(proxy_svr *svr, void **data)
{
	proxy_tcp_conn		*conn;

	proxy_tcp_create_conn(&conn);
	conn->svr = svr;
	*data = (void *)conn;
	
	return PROXY_RES_OK;
}

/**
 * Create server socket and bind address to it. 
 * 
 * @return conn structure containing server socket and port
 */
static int 
proxy_tcp_svr_create(proxy_svr *svr, int port)
{
	socklen_t				slen;
	SOCKET					sd;
	struct sockaddr_in		sname;
	proxy_tcp_conn *			conn = (proxy_tcp_conn *)svr->svr_data;
	
	if ( (sd = socket(PF_INET, SOCK_STREAM, 0)) == INVALID_SOCKET )
	{
		proxy_set_error(PROXY_ERR_SYSTEM, strerror(errno));
		return PROXY_RES_ERR;
	}
	
	memset (&sname, 0, sizeof(sname));
	sname.sin_family = PF_INET;
	sname.sin_port = htons(port);
	sname.sin_addr.s_addr = htonl(INADDR_ANY);
	
	if (bind(sd,(struct sockaddr *) &sname, sizeof(sname)) == SOCKET_ERROR )
	{
		proxy_set_error(PROXY_ERR_SYSTEM, strerror(errno));
		CLOSE_SOCKET(sd);
		return PROXY_RES_ERR;
	}
	
	slen = sizeof(sname);
	
	if ( getsockname(sd, (struct sockaddr *)&sname, &slen) == SOCKET_ERROR )
	{
		proxy_set_error(PROXY_ERR_SYSTEM, strerror(errno));
		CLOSE_SOCKET(sd);
		return PROXY_RES_ERR;
	}
	
	if ( listen(sd, 5) == SOCKET_ERROR )
	{
		proxy_set_error(PROXY_ERR_SYSTEM, strerror(errno));
		CLOSE_SOCKET(sd);
		return PROXY_RES_ERR;
	}
	
	conn->svr_sock = sd;
	conn->port = (int) ntohs(sname.sin_port);
	
	if (svr->proxy->handler_funcs->regfile != NULL)
		svr->proxy->handler_funcs->regfile(sd, READ_FILE_HANDLER, proxy_tcp_svr_accept, (void *)conn);
	if (svr->proxy->handler_funcs->regeventhandler != NULL)
		svr->proxy->handler_funcs->regeventhandler(PROXY_EVENT_HANDLER, proxy_tcp_svr_event_callback, (void *)conn);
	
	return PROXY_RES_OK;
}

/**
 * Connect to a remote proxy.
 */
static int
proxy_tcp_svr_connect(proxy_svr *svr, char *host, int port)
{
	SOCKET					sd;
	struct hostent *			hp;
	long int					haddr;
	struct sockaddr_in		scket;
	proxy_tcp_conn *			conn = (proxy_tcp_conn *)svr->svr_data;
		        
	if (host == NULL) {
		proxy_set_error(PROXY_ERR_SERVER, "no host specified");
		return PROXY_RES_ERR;
	}
	
	hp = gethostbyname(host);
	        
	if (hp == (struct hostent *)NULL) {
		proxy_set_error(PROXY_ERR_SERVER, "could not find host");
		return PROXY_RES_ERR;
	}
	
	haddr = ((hp->h_addr[0] & 0xff) << 24) |
			((hp->h_addr[1] & 0xff) << 16) |
			((hp->h_addr[2] & 0xff) <<  8) |
			((hp->h_addr[3] & 0xff) <<  0);
	
	if ( (sd = socket(PF_INET, SOCK_STREAM, 0)) == INVALID_SOCKET )
	{
		proxy_set_error(PROXY_ERR_SYSTEM, strerror(errno));
		return PROXY_RES_ERR;
	}
	
	memset (&scket,0,sizeof(scket));
	scket.sin_family = PF_INET;
	scket.sin_port = htons((u_short) port);
	scket.sin_addr.s_addr = htonl(haddr);
	
	if ( connect(sd, (struct sockaddr *) &scket, sizeof(scket)) == SOCKET_ERROR )
	{
		proxy_set_error(PROXY_ERR_SYSTEM, strerror(errno));
		CLOSE_SOCKET(sd);
		return PROXY_RES_ERR;
	}

	conn->sess_sock = sd;
	conn->host = strdup(host);
	conn->port = port;
	
	if (svr->proxy->handler_funcs->regeventhandler != NULL)
		svr->proxy->handler_funcs->regeventhandler(PROXY_EVENT_HANDLER, proxy_tcp_svr_event_callback, (void *)conn);
	if (svr->proxy->handler_funcs->regfile != NULL)
		svr->proxy->handler_funcs->regfile(sd, READ_FILE_HANDLER, proxy_tcp_svr_recv_msgs, (void *)conn);
	
	return PROXY_RES_OK;
}

/**
 * Accept a new proxy connection. Register dispatch routine.
 */
static int
proxy_tcp_svr_accept(int fd, void *data)
{
	socklen_t				fromlen;
	SOCKET					ns;
	struct sockaddr			addr;
	proxy_tcp_conn *			conn = (proxy_tcp_conn *)data;
	
	fromlen = sizeof(addr);
	ns = accept(fd, &addr, &fromlen);
	if (ns < 0) {
		proxy_set_error(PROXY_ERR_SYSTEM, strerror(errno));
		return PROXY_RES_ERR;
	}
	
	/*
	 * Only allow one connection at a time.
	 */
	if (conn->connected) {
		CLOSE_SOCKET(ns); // reject
		return PROXY_RES_OK;
	}
	
	if (conn->svr->svr_helper_funcs->newconn != NULL && conn->svr->svr_helper_funcs->newconn() < 0) {
		CLOSE_SOCKET(ns); // reject
		return PROXY_RES_OK;
	}
	
	conn->sess_sock = ns;
	conn->connected++;
	
	if (conn->svr->proxy->handler_funcs->regfile != NULL)
		conn->svr->proxy->handler_funcs->regfile(ns, READ_FILE_HANDLER, proxy_tcp_svr_recv_msgs, (void *)conn);
	
	return PROXY_RES_OK;
}

/**
 * Cleanup prior to server exit.
 */
static void 
proxy_tcp_svr_finish(proxy_svr *svr)
{
	proxy_tcp_conn *			conn = (proxy_tcp_conn *)svr->svr_data;
	
	if (conn->sess_sock != INVALID_SOCKET) {
		if (svr->proxy->handler_funcs->unregfile != NULL)
			svr->proxy->handler_funcs->unregfile(conn->sess_sock);
		CLOSE_SOCKET(conn->sess_sock);
		conn->sess_sock = INVALID_SOCKET;
	}
	
	if (conn->svr_sock != INVALID_SOCKET) {
		if (svr->proxy->handler_funcs->unregfile != NULL)
			svr->proxy->handler_funcs->unregfile(conn->svr_sock);
		CLOSE_SOCKET(conn->svr_sock);
		conn->svr_sock = INVALID_SOCKET;
	}
	
	proxy_tcp_destroy_conn(conn);
}

/**
 * Check for incoming messages or connection attempts.
 * 
 * @return	0	success
 */
static int
proxy_tcp_svr_progress(proxy_svr *svr)
{
	char *					msg;
	proxy_tcp_conn *			conn = (proxy_tcp_conn *)svr->svr_data;

	if (proxy_tcp_get_msg(conn, &msg) > 0) {
		proxy_tcp_svr_dispatch(conn, msg);
		free(msg);
	}

	return PROXY_RES_OK;
}

/**
 * Check file descriptors for messages and if present, call handlers
 */
static int
proxy_tcp_svr_handle_events(proxy_svr *svr, List * eventList, struct timeval timeout)
{
	/* file descriptors for handling resource manager commands */

	fd_set			rfds;
	fd_set			wfds;
	fd_set			efds;
	int				res;
	int				nfds = 0;
	char *			event;
	struct timeval	tv;
	handler *		h;

	for (SetList(eventList); (event = (char *)GetListElement(eventList)) != NULL; ) {
		proxy_svr_event_callback(svr, event);
		RemoveFromList(eventList, (void *)event);
		free(event);	
	}

	/***********************************
	 * First: Check for any file events
	 */
	 
	/* Set up fd sets */

	FD_ZERO(&rfds);
	FD_ZERO(&wfds);
	FD_ZERO(&efds);
	
	for (SetHandler(); (h = GetHandler()) != NULL; ) {
		if (h->htype == HANDLER_FILE) {
			if (h->file_type & READ_FILE_HANDLER)
				FD_SET(h->fd, &rfds);
			if (h->file_type & WRITE_FILE_HANDLER)
				FD_SET(h->fd, &wfds);
			if (h->file_type & EXCEPT_FILE_HANDLER)
				FD_SET(h->fd, &efds);
			if (h->fd > nfds)
				nfds = h->fd;
		}
	}
	
	tv = timeout;
	
	for ( ;; ) {
		res = select(nfds+1, &rfds, &wfds, &efds, &tv);
	
		switch (res) {
		case INVALID_SOCKET:
			if ( errno == EINTR )
				continue;
		
			perror("socket");
			return PROXY_RES_ERR;
		
		case 0:
			/* Timeout. */
			break;

		default:
			for (SetHandler(); (h = GetHandler()) != NULL; ) {
				if (h->htype == HANDLER_FILE
					&& ((h->file_type & READ_FILE_HANDLER && FD_ISSET(h->fd, &rfds))
						|| (h->file_type & WRITE_FILE_HANDLER && FD_ISSET(h->fd, &wfds))
						|| (h->file_type & EXCEPT_FILE_HANDLER && FD_ISSET(h->fd, &efds)))
					&& h->file_handler(h->fd, h->data) < 0)
					return PROXY_RES_ERR;
			}
			
		}
	
		break;
	}

	return 0;	
}

/*
 * Dispatch a command to the server
 *
 * proxy_tcp_svr_dispatch() should never fail. If we get a read error from the client then we just
 * assume the client has gone away. Errors from server commands are just reported back to the
 * client.
 */
static int
proxy_tcp_svr_dispatch(proxy_tcp_conn *conn, char *msg)
{
	int					i;
	int					argc;
	char *				p;
	char **				args;
	proxy_svr_commands * cmd;
	
	DEBUG_PRINT("SVR received <%s>\n", msg);

	/*
	 * Convert msg into an array of arguments
	 * Convert each argument from proxy str to a cstring (apart from first)
	 */
	for (argc = 1, p = msg; *p != '\0';)
		if (*p++ == ' ')
			argc++;
			
	args = (char **)malloc((argc + 1) * sizeof(char *));

	for (i = 0; i < argc; i++) {
		if ((p = strsep(&msg, " ")) == NULL)
			break;
		if (i == 0)
			args[i] = strdup(p);
		else
			proxy_str_to_cstring(p, &args[i]);
	}
		
	args[i] = NULL;
                               
	for (cmd = conn->svr->svr_commands; cmd->cmd_name != NULL; cmd++) {
		if (strcmp(args[0], cmd->cmd_name) == 0) {
			(void)cmd->cmd_func(args);
			break;
		}
	}
	
	for (i = 0; i < argc; i++)
		free(args[i]);
		
	free(args);
	
	return 0;
}

static int
proxy_tcp_svr_recv_msgs(int fd, void *data)
{
	proxy_tcp_conn *		conn = (proxy_tcp_conn *)data;
	
	return proxy_tcp_recv_msgs(conn);
}
