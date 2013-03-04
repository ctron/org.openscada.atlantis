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

package org.openscada.da.client;

import java.util.Collection;

import org.openscada.da.core.browser.Entry;

public interface FolderListener
{
    /**
     * Provide changes to the listener
     * <p>
     * If either no items were added or removed the corresponding list must
     * still be not null. The lists may not be altered by the listener
     * implementations and may by unmodifiable.
     * </p>
     * 
     * @param added
     *            the folder entries that were added
     * @param removed
     *            the folder entries that where removed
     * @param full
     *            <code>true</code> if this is not a change but a full
     *            transmission, in this case the <code>removed</code> parameter
     *            must be empty or <code>null</code>.
     */
    public void folderChanged ( Collection<Entry> added, Collection<String> removed, boolean full );
}
