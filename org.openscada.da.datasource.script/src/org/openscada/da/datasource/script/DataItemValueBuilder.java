/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.datasource.script;

import org.eclipse.scada.core.data.SubscriptionState;
import org.openscada.da.client.DataItemValue;

/**
 * A class that renames the original {@link DataItemValue.Builder} since some
 * script languages might have problems accessing a nested class or enums.
 * @author Jens Reimann
 *
 */
public class DataItemValueBuilder extends DataItemValue.Builder
{
    public static SubscriptionState CONNECTED = SubscriptionState.CONNECTED;

    public static SubscriptionState GRANTED = SubscriptionState.GRANTED;

    public static SubscriptionState DISCONNECTED = SubscriptionState.DISCONNECTED;

    public void setConnected ( final boolean flag )
    {
        setSubscriptionState ( flag ? SubscriptionState.CONNECTED : SubscriptionState.DISCONNECTED );
    }
}
