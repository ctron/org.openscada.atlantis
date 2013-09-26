/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.eclipse.scada.da.server.browser.common.query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.eclipse.scada.da.core.browser.Entry;
import org.eclipse.scada.da.core.server.browser.NoSuchFolderException;
import org.eclipse.scada.da.server.browser.common.FolderListener;

public class GroupFolder implements StorageBasedFolder
{
    private final Map<ItemDescriptor, GroupSubFolder> itemList = new HashMap<ItemDescriptor, GroupSubFolder> ();

    private final GroupProvider groupProvider;

    private final NameProvider nameProvider;

    private final GroupSubFolder folder;

    public GroupFolder ( final GroupProvider groupProvider, final NameProvider nameProvider )
    {
        this.groupProvider = groupProvider;
        this.nameProvider = nameProvider;

        this.folder = new GroupSubFolder ( this.nameProvider );
    }

    synchronized public Entry[] list ( final Stack<String> path ) throws NoSuchFolderException
    {
        return this.folder.list ( path );
    }

    synchronized public void subscribe ( final Stack<String> path, final FolderListener listener, final Object tag ) throws NoSuchFolderException
    {
        this.folder.subscribe ( path, listener, tag );
    }

    synchronized public void unsubscribe ( final Stack<String> path, final Object tag ) throws NoSuchFolderException
    {
        this.folder.unsubscribe ( path, tag );
    }

    private static String[] reverse ( final String[] b )
    {
        for ( int left = 0, right = b.length - 1; left < right; left++, right-- )
        {
            // exchange the first and last
            final String temp = b[left];
            b[left] = b[right];
            b[right] = temp;
        }
        return b;
    }

    synchronized public void added ( final ItemDescriptor descriptor )
    {
        if ( this.itemList.containsKey ( descriptor ) )
        {
            return;
        }

        final String[] groupingArray = this.groupProvider.getGrouping ( descriptor );
        if ( groupingArray == null )
        {
            return;
        }

        final Stack<String> pathStack = new Stack<String> ();
        pathStack.addAll ( Arrays.asList ( reverse ( groupingArray ) ) );

        final GroupSubFolder subFolder = this.folder.add ( pathStack, descriptor );

        if ( subFolder != null )
        {
            this.itemList.put ( descriptor, subFolder );
        }
    }

    synchronized public void removed ( final ItemDescriptor descriptor )
    {
        final GroupSubFolder folder = this.itemList.get ( descriptor );

        if ( folder == null )
        {
            return;
        }

        folder.remove ( descriptor );
        this.itemList.remove ( descriptor );
    }

    public void added ()
    {
        this.folder.added ();
    }

    public void removed ()
    {
        this.folder.removed ();
    }

}
