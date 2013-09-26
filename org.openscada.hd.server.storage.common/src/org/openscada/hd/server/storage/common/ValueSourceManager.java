/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassid.de)
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

package org.openscada.hd.server.storage.common;

import org.eclipse.scada.hd.data.QueryParameters;
import org.eclipse.scada.hds.ValueVisitor;

public interface ValueSourceManager
{

    public void queryClosed ( QueryImpl query );

    /**
     * Visit values
     * 
     * @param parameters
     *            the parameters to visit
     * @param visitor
     *            the visitor
     * @return <code>true</code> if the run was complete, <code>false</code> if
     *         is was aborted by the visitor
     */
    public boolean visit ( QueryParameters parameters, ValueVisitor visitor );

}
