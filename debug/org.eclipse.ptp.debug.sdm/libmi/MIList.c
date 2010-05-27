/******************************************************************************
 * Copyright (c) 2005, 2010 The Regents of the University of California and others
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
 * NOTE: This list implementation is not thread safe.
 */

#include <stdio.h>
#include <stdlib.h>

#include "MIList.h"

/*
 * Create a new empty list
 */
MIList *
MIListNew(void)
{
	MIList *	l;

	l = (MIList *)malloc(sizeof(MIList));

	l->l_head = (ListElement *)NULL;
	l->l_tail = &l->l_head;
	l->l_nel = 0;
	l->l_scan = NULL;

	return l;
}

/*
 * Add new element to end of list.
 */
void		
MIListAdd(MIList *l, void *v)
{
	ListElement *	e;

	if ( l == (List *)NULL)
		return;

	e = malloc(sizeof(ListElement));

	e->l_value = v;
	e->l_next = (ListElement *)NULL;
	
	*(l->l_tail) = e;
	l->l_tail = &e->l_next;
	l->l_nel++;
}

/*
 * Add new element to beginning of list (stack emulation).
 */
void		
MIListAddFirst(MIList *l, void *v)
{
	ListElement *	e;
	ListElement *	ep;

	if (l == (List *)NULL)
		return;

	e = malloc(sizeof(ListElement));

	e->l_value = v;
	e->l_next = l->l_head;

	/*
	** Adjust tail if necessary.
	*/
	if
	(
		(ep = l->l_head) != (ListElement *)NULL 
		&&
		ep->l_next == (ListElement *)NULL 
	)
		l->l_tail = &ep->l_next;

	l->l_head = e;
	l->l_nel++;
}

/*
 * Insert new element before given element
 */
void
MIListInsertBefore(MIList *l, void *val, void *new_val)
{
	ListElement **	e;
	ListElement *	ne;

	if (l == (List *)NULL) {
		return;
	}
	
	/*
	 * Find the element corresponding to val
	 */
	for (e = &l->l_head ; *e != (ListElement *)NULL ; e = &(*e)->l_next) {
		if ((*e)->l_value == val) {
			break;
		}
	}
	
	if (*e == NULL) {
		return;
	}

	ne = malloc(sizeof(ListElement));

	ne->l_value = new_val;
	ne->l_next = *e;
	*e = ne;
	l->l_nel++;
}

/*
 * Append source list to destination list
 */
void
MIListAppend(MIList *dst, MIList *src)
{
	void *	e;

	SetList(src);

	while ( (e = GetListElement(src)) != (void *)NULL) {
		MIListAdd(dst, e);
	}
}

/*
 * Remove element from list
 */
void		
MIListRemove(MIList *l, void *v)
{
	ListElement *	e;
	ListElement *	ep;

	if ( l == (MIList *)NULL || l->l_nel == 0 ) {
		return;
	}

	for ( ep = e = l->l_head ; e != (ListElement *)NULL ; )
	{
		if ( e->l_value != v )
		{
			if ( ep != e )
				ep = ep->l_next;
			e = e->l_next;
			continue;
		}

		if ( l->l_scan == e )
			l->l_scan = ep;

		if ( ep == e )
		{
			l->l_head = e->l_next;

			if ( e->l_next == (ListElement *)NULL )
				l->l_tail = &l->l_head;
		}
		else
		{
			ep->l_next = e->l_next;

			if ( e->l_next == (ListElement *)NULL )
				l->l_tail = &ep->l_next;
		}

		free(e);

		break;
	}

	l->l_nel--;
}

/*
 * Remove first element of list (stack emulation)
 */
void *
MIListRemoveFirst(MIList *l)
{
	void *		v;
	ListElement *	e;
	ListElement *	ep;


	if ( l == (MIList *)NULL || l->l_nel == 0 ) {
		return (void *)NULL;
	}

	ep = e = l->l_head;

	if ( l->l_scan == e )
		l->l_scan = e->l_next;

	l->l_head = e->l_next;

	if ( e->l_next == (ListElement *)NULL )
		l->l_tail = &l->l_head;

	v = e->l_value;

	free(e);

	l->l_nel--;

	return v;
}

/*
 * Destroy list and its contents
 */
void		
MIListFree(MIList *l, void (*destroy)())
{
	ListElement *	ep;
	ListElement *	en;

	if ( l == (MIList *)NULL )
		return;

	ep = l->l_head;

	while ( ep != (ListElement *)NULL )
	{
		en = ep->l_next;

		if ( destroy != (void (*)())NULL )
			destroy(ep->l_value);

		free(ep);

		ep = en;
	}

	free(l);
}

/*
 * Initialize list iterator
 */
void
MIListSet(MIList *l)
{
	if ( l == (MIList *)NULL )
		return;

	l->l_scan = l->l_head;
}

/*
 * Get next element from list. Returns NULL when there are no more elements
 */
void *
MIListGet(MIList *l)
{
	void *			val;
	ListElement *	le;

	if ( l == (MIList *)NULL || l->l_scan == (ListElement *)NULL ) {
		return (void *)NULL;
	}

	le = l->l_scan;
	l->l_scan = le->l_next;
	
	val = le->l_value;
	
	return val;
}

/*
 * Get the first element in the list
 */
void *
MIListGetFirst(MIList *l)
{
	void *	val;
	
	if ( l == (MIList *)NULL || l->l_head == (ListElement *) NULL) {
		THREAD_UNLOCK(list);
		return (void *)NULL;
	}

	val = l->l_head->l_value;
	
	return val;
}

/*
 * Check if the list is empty. Returns true (1) if it is.
 */
int
MIListIsEmpty(MIList *l)
{
	return (int)(l == (MIList *)NULL || l->l_nel == 0);
}

/*
 * Check if element is in the list
 */
int
MIListTest(MIList *l, void *v)
{
	ListElement *	e;

	if ( l == (MIList *)NULL ) {
		return 0;
	}

	for ( e = l->l_head ; e != (ListElement *)NULL ; e = e->l_next ) {
		if ( e->l_value == v ) {
			return 1;
		}
	}

	return 0;
}

/*
 * Get the number of elements in the list
 */
int
MIListSize(MIList *l)
{
	if ( l == (MIList *)NULL )
		return 0;

	return l->l_nel;
}
