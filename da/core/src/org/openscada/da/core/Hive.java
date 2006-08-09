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

package org.openscada.da.core;

import java.util.Collection;
import java.util.Properties;

import org.openscada.da.core.browser.HiveBrowser;
import org.openscada.da.core.data.Variant;

public interface Hive
{
	public Session createSession ( Properties props ) throws UnableToCreateSessionException;
	public void closeSession ( Session session ) throws InvalidSessionException;
	
	public void registerForItem ( Session session, String item, boolean initial ) throws InvalidSessionException, InvalidItemException;
	public void unregisterForItem ( Session session, String item ) throws InvalidSessionException, InvalidItemException;
	
	public void registerForAll ( Session session ) throws InvalidSessionException;
	public void unregisterForAll ( Session session ) throws InvalidSessionException;
	
    public void registerItemList ( Session session ) throws InvalidSessionException;
    public void unregisterItemList ( Session session ) throws InvalidSessionException;
    
	// enumerate
	public Collection<DataItemInformation> listItems ( Session session ) throws InvalidSessionException;
    
    // async DA operations
    public long startWrite ( Session session, String itemName, Variant value, WriteOperationListener listener ) throws InvalidSessionException, InvalidItemException;
    //public void startRead ( Session session, String item, Variant value, ReadOperationListener listener );
    
    public void thawOperation ( long id );
    public void cancelOperation ( long id ) throws CancellationNotSupportedException;
    
    public HiveBrowser getBrowser ();
}
