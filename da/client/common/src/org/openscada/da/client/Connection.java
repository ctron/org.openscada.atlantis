/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client;

import java.util.Map;

import org.openscada.core.InvalidSessionException;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.da.core.Location;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.browser.Entry;
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningOperation;

public interface Connection extends org.openscada.core.client.Connection
{
    public abstract Entry[] browse ( String [] path ) throws InvalidSessionException, OperationException;
    public abstract Entry[] browse ( String [] path, LongRunningListener listener ) throws InvalidSessionException, OperationException;
    public abstract LongRunningOperation startBrowse ( String [] path, LongRunningListener listener ) throws Exception;
    public abstract LongRunningOperation startBrowse ( String [] path );
    public abstract Entry[] completeBrowse ( LongRunningOperation op ) throws InvalidSessionException, OperationException;
    
    public abstract void write ( String itemName, Variant value ) throws InterruptedException, OperationException;
    public abstract void write ( String itemName, Variant value, LongRunningListener listener ) throws InterruptedException, OperationException;
    public abstract LongRunningOperation startWrite ( String itemName, Variant value );
    public abstract LongRunningOperation startWrite ( String itemName, Variant value, LongRunningListener listener );
    public abstract void completeWrite ( LongRunningOperation op ) throws OperationException;
    
    public abstract void writeAttributes ( String itemId, Map<String,Variant> attributes ) throws InterruptedException, OperationException;
    public abstract void writeAttributes ( String itemId, Map<String,Variant> attributes, LongRunningListener listener ) throws InterruptedException, OperationException;
    public abstract LongRunningOperation startWriteAttributes ( String itemId, Map<String,Variant> attributes, LongRunningListener listener );
    public abstract WriteAttributeResults completeWriteAttributes ( LongRunningOperation operation ) throws OperationException;
    
    public abstract void subscribeFolder ( Location location ) throws InvalidSessionException, OperationException;
    public abstract void unsubscribeFolder ( Location location ) throws InvalidSessionException, OperationException;
    public abstract FolderListener setFolderListener ( Location location, FolderListener listener );
    
    public abstract void subscribeItem ( String itemId, boolean initial ) throws InvalidSessionException, OperationException;
    public abstract void unsubscribeItem ( String itemId ) throws InvalidSessionException, OperationException;
    public abstract ItemUpdateListener setItemUpdateListener ( String itemId, ItemUpdateListener listener );
}
