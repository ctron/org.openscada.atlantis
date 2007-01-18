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

package org.openscada.da.server.ice.impl;

import org.apache.log4j.Logger;

import Ice.LocalException;
import OpenSCADA.DA.AMI_DataCallback_attributesChange;

public class AsyncAttributesChange extends AMI_DataCallback_attributesChange
{
    private static Logger _log = Logger.getLogger ( AsyncAttributesChange.class );
    
    private SessionImpl _session = null;
    
    public AsyncAttributesChange ( SessionImpl session )
    {
        super ();
        _session = session;
    }
    
    @Override
    public void ice_exception ( LocalException ex )
    {
        _log.debug ( "Failed to notify", ex );
        _session.handleListenerError ();
    }

    @Override
    public void ice_response ()
    {
    }

}
