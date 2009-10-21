/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.client.base.realtime;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class ListEntryComparator extends ViewerComparator
{
    @Override
    public int compare ( final Viewer viewer, final Object e1, final Object e2 )
    {
        if ( e1 instanceof ListEntry && e2 instanceof ListEntry )
        {
            final ListEntry l1 = (ListEntry)e1;
            final ListEntry l2 = (ListEntry)e2;
            return l1.getDataItem ().getItem ().getId ().compareTo ( l2.getDataItem ().getItem ().getId () );
        }
        if ( e1 instanceof ListEntry.AttributePair && e2 instanceof ListEntry.AttributePair )
        {
            final ListEntry.AttributePair l1 = (ListEntry.AttributePair)e1;
            final ListEntry.AttributePair l2 = (ListEntry.AttributePair)e2;
            return l1.key.compareTo ( l2.key );
        }
        return super.compare ( viewer, e1, e2 );
    }
}