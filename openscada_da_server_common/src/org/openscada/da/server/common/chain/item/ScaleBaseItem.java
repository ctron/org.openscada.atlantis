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

public abstract class ScaleBaseItem extends BaseChainItemCommon
{
    public static final String SCALE_FACTOR = ".factor";

    public static final String SCALE_RAW = ".raw";

    public static final String SCALE_ERROR = ".error";

    private final VariantBinder _scaleFactor = new VariantBinder ( new Variant () );

    public ScaleBaseItem ( final HiveServiceRegistry serviceRegistry )
    {
        super ( serviceRegistry );

        addBinder ( getFactorName (), this._scaleFactor );
        setReservedAttributes ( getRawName (), getErrorName () );
    }

    public Variant process ( final Variant value, final Map<String, Variant> attributes )
    {
        Variant newValue = null;

        attributes.put ( getRawName (), null );
        attributes.put ( getErrorName (), null );
        try
        {
            final Variant scaleFactor = this._scaleFactor.getValue ();
            // only process if we have a scale factor
            if ( !scaleFactor.isNull () )
            {
                attributes.put ( getRawName (), new Variant ( value ) );
                newValue = new Variant ( value.asDouble () * scaleFactor.asDouble () );
            }
        }
        catch ( final Exception e )
        {
            attributes.put ( getErrorName (), new Variant ( e.getMessage () ) );
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
