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

package org.openscada.da.server.common.chain.item;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.chain.BaseChainItemCommon;
import org.openscada.da.server.common.chain.VariantBinder;

public abstract class NegateBaseItem extends BaseChainItemCommon
{
    public static final String NEGATE_ACTIVE = ".active";

    public static final String NEGATE_ERROR = ".error";

    private final VariantBinder negateActive = new VariantBinder ( new Variant () );

    public NegateBaseItem ( final HiveServiceRegistry serviceRegistry )
    {
        super ( serviceRegistry );

        addBinder ( getActiveName (), this.negateActive );
        setReservedAttributes ( getErrorName () );
    }

    public Variant process ( final Variant value, final Map<String, Variant> attributes )
    {
        Variant newValue = null;

        attributes.put ( getErrorName (), null );
        try
        {
            final Variant activeFlag = this.negateActive.getValue ();
            // only process if we are active
            if ( !activeFlag.isNull () )
            {
                newValue = Variant.valueOf ( !value.asBoolean () );
            }
        }
        catch ( final Exception e )
        {
            attributes.put ( getErrorName (), new Variant ( e.getMessage () ) );
        }

        addAttributes ( attributes );

        return newValue;
    }

    private String getActiveName ()
    {
        return getBase () + NEGATE_ACTIVE;
    }

    private String getErrorName ()
    {
        return getBase () + NEGATE_ERROR;
    }

    protected abstract String getBase ();
}
