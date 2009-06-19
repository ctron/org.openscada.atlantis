/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.chain;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.server.common.DataItem;

/**
 * A chain element
 * <p>
 * An instance of chain element may only be added to one data item 
 * @author Jens Reimann
 *
 */
public interface ChainItem
{
    /**
     * Request to set attributes
     * @param attributes the attributes update set
     * @return result for processed attributes
     */
    public abstract WriteAttributeResults setAttributes ( Map<String, Variant> attributes );

    /**
     * Process the chain item
     * @return the new result or <code>null</code> if the chain item does not change the input value
     * @param value the value to process or <code>null</code> if a output item changed only the attributes 
     * @param attributes The current primary attributes
     */
    public abstract Variant process ( Variant value, Map<String, Variant> attributes );

    /**
     * Called when the chain item is set to a data item
     * @param item The data item itself or <code>null</code> if the chain element is removed from the data item
     */
    public abstract void dataItemChanged ( DataItem item );

    /**
     * If this method returns true its binders are persisted when added to the {@link BaseChainItemCommon}
     * @return
     */
    public abstract boolean isPersistent ();
}
