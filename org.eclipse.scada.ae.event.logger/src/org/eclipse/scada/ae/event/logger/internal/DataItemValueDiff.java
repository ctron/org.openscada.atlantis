/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.ae.event.logger.internal;

import org.eclipse.scada.core.AttributesHelper;
import org.eclipse.scada.da.client.DataItemValue;
import org.eclipse.scada.da.client.DataItemValue.Builder;

public class DataItemValueDiff
{
    private static final DataItemValue DEFAULT_VALUE = DataItemValue.DISCONNECTED;

    public static DataItemValue diff ( DataItemValue source, DataItemValue target )
    {
        final Builder builder = new Builder ();

        if ( source == null )
        {
            source = DEFAULT_VALUE;
        }
        if ( target == null )
        {
            target = DEFAULT_VALUE;
        }

        builder.setValue ( changed ( source.getValue (), target.getValue () ) );
        builder.setSubscriptionState ( changed ( source.getSubscriptionState (), target.getSubscriptionState () ) );
        builder.setAttributes ( AttributesHelper.diff ( source.getAttributes (), target.getAttributes () ) );

        return builder.build ();
    }

    /**
     * Check if data changed from source to target
     * @param <T> the type to check
     * @param source the original value
     * @param target the new value
     * @return the target value if it is different to the source value, <code>null</code> otherwise
     */
    private static <T> T changed ( final T source, final T target )
    {
        if ( source == target )
        {
            return null;
        }
        if ( source == null )
        {
            return target;
        }
        if ( target == null )
        {
            return null;
        }
        // now we are sure that neither source nor target are null
        if ( source.equals ( target ) )
        {
            return null;
        }
        return target;
    }
}
