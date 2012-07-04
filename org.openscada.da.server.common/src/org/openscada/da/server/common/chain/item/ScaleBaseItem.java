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

package org.openscada.da.server.common.chain.item;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.chain.BaseChainItemCommon;
import org.openscada.da.server.common.chain.VariantBinder;

public abstract class ScaleBaseItem extends BaseChainItemCommon
{
    public static final String SCALE_FACTOR = ".factor";

    public static final String SCALE_RAW = ".value.original";

    public static final String SCALE_ERROR = ".error";

    private final VariantBinder scaleFactor = new VariantBinder ( Variant.NULL );

    public ScaleBaseItem ( final HiveServiceRegistry serviceRegistry )
    {
        super ( serviceRegistry );

        addBinder ( getFactorName (), this.scaleFactor );
        setReservedAttributes ( getRawName (), getErrorName () );
    }

    @Override
    public Variant process ( final Variant value, final Map<String, Variant> attributes )
    {
        Variant newValue = null;

        attributes.put ( getRawName (), null );
        attributes.put ( getErrorName (), null );
        try
        {
            final Variant scaleFactor = this.scaleFactor.getValue ();
            // only process if we have a scale factor
            if ( !scaleFactor.isNull () )
            {
                attributes.put ( getRawName (), Variant.valueOf ( value ) );
                newValue = Variant.valueOf ( value.asDouble () * scaleFactor.asDouble () );
            }
        }
        catch ( final Exception e )
        {
            attributes.put ( getErrorName (), Variant.valueOf ( e.getMessage () ) );
        }

        addAttributes ( attributes );

        return newValue;
    }

    private String getFactorName ()
    {
        return getBase () + SCALE_FACTOR;
    }

    private String getErrorName ()
    {
        return getBase () + SCALE_ERROR;
    }

    private String getRawName ()
    {
        return getBase () + SCALE_RAW;
    }

    protected abstract String getBase ();
}
