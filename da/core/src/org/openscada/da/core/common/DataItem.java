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

package org.openscada.da.core.common;

import java.util.Map;

import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.WriteAttributesOperationListener;
import org.openscada.da.core.common.impl.WriteAttributesOperation;
import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;

public interface DataItem {
	
	public DataItemInformation getInformation ();
	
	public Variant getValue () throws InvalidOperationException;
	public void setValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException;
	
	public Map<String, Variant> getAttributes ();
	public Map<String, WriteAttributesOperationListener.Result> setAttributes ( Map<String,Variant> attributes );
	
	/** Sets the listener for this item
	 * @param listener The listener to use or null to disable notification
	 * 
	 * Set by the controller to which this item is registered. The item has to use the listener
	 * provided.
	 * 
	 */
	public void setListener ( ItemListener listener );
}
