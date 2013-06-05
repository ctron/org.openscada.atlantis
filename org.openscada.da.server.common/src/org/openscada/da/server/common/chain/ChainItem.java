/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.da.server.common.chain;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.WriteAttributeResults;

/**
 * A chain element
 * <p>
 * An instance of chain element may only be added to one data item
 * 
 * @author Jens Reimann
 */
public interface ChainItem
{
    /**
     * Request to set attributes
     * 
     * @param attributes
     *            the attributes update set
     * @return result for processed attributes
     */
    public abstract WriteAttributeResults setAttributes ( Map<String, Variant> attributes );

    /**
     * Process the chain item
     * 
     * @return the new result or <code>null</code> if the chain item does not
     *         change the input value
     * @param value
     *            the value to process or <code>null</code> if a output item
     *            changed only the attributes
     * @param attributes
     *            The current primary attributes
     */
    public abstract Variant process ( Variant value, Map<String, Variant> attributes );

}
