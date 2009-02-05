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

package org.openscada.da.client.ice;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.ice.AttributesHelper;
import org.openscada.core.ice.VariantHelper;

import Ice.Current;
import OpenSCADA.Core.VariantBase;
import OpenSCADA.DA.SubscriptionState;
import OpenSCADA.DA._DataCallbackDisp;

public class DataCallbackImpl extends _DataCallbackDisp
{
    private static Logger _log = Logger.getLogger ( DataCallbackImpl.class );
    
    private Connection _connection = null;
    
    public DataCallbackImpl ( Connection connection )
    {
        super ();
        _connection = connection;
    } 
   
    @SuppressWarnings("unchecked")
    public void dataChange ( String item, VariantBase value, Map attributes, boolean cache, Current __current )
    {
        _log.debug ( String.format ( "Data change: '%s' (%s)", cache, item ) );
        _connection.dataChange ( item, VariantHelper.fromIce ( value ), AttributesHelper.fromIce ( attributes ), cache );
    }

    public void subscriptionChange ( String item, SubscriptionState subscriptionState, Current __current )
    {
        _log.debug ( String.format ( "Subscription change: '%s' - '%s'", item, subscriptionState.value () ) );
        
        org.openscada.core.subscription.SubscriptionState ss = org.openscada.core.subscription.SubscriptionState.DISCONNECTED;
        switch ( subscriptionState.value () )
        {
        case SubscriptionState._CONNECTED:
            ss = org.openscada.core.subscription.SubscriptionState.CONNECTED;
            break;
        case SubscriptionState._DISCONNECTED:
            ss = org.openscada.core.subscription.SubscriptionState.DISCONNECTED;
            break;
        case SubscriptionState._GRANTED:
            ss = org.openscada.core.subscription.SubscriptionState.GRANTED;
            break;
        }
        
        _connection.subscriptionChange ( item, ss );
    }

}
