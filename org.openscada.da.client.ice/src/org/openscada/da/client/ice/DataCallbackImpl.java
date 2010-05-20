/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

    public DataCallbackImpl ( final Connection connection )
    {
        super ();
        this._connection = connection;
    }

    @SuppressWarnings ( "unchecked" )
    public void dataChange ( final String item, final VariantBase value, final Map attributes, final boolean cache, final Current __current )
    {
        _log.debug ( String.format ( "Data change: '%s' (%s)", cache, item ) );
        this._connection.dataChange ( item, VariantHelper.fromIce ( value ), AttributesHelper.fromIce ( attributes ), cache );
    }

    public void subscriptionChange ( final String item, final SubscriptionState subscriptionState, final Current __current )
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

        this._connection.subscriptionChange ( item, ss );
    }

}
