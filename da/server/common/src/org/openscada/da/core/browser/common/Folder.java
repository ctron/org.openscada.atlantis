/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.core.browser.common;

import java.util.Stack;

import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.browser.NoSuchFolderException;


public interface Folder
{
    Entry [] list ( Stack<String> path ) throws NoSuchFolderException;
    
    void subscribe ( Stack<String> path, FolderListener listener, Object tag ) throws NoSuchFolderException;
    void unsubscribe ( Stack<String> path, Object tag ) throws NoSuchFolderException;
    
    /**
     * Called when the folder was added to the browser space
     */
    void added ();
    
    /**
     * Called when the folder was removed from the browser space
     *
     */
    void removed ();
}
