/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.da.core.common.chained;

import java.util.Map;

import org.openscada.da.core.data.Variant;

public class ScaleInputItem extends InputChainItemCommon
{
    public static final String SCALE_FACTOR = "org.openscada.da.input.scale.factor";
    public static final String SCALE_RAW = "org.openscada.da.input.scale.raw";
    public static final String SCALE_ERROR = "org.openscada.da.input.scale.error";
    
    private VariantBinder _scaleFactor = new VariantBinder ( new Variant () );
    
    public ScaleInputItem ()
    {
        super ();
     
        addBinder ( SCALE_FACTOR, _scaleFactor );
        setReservedAttributes ( SCALE_RAW, SCALE_ERROR );
    }
    
    public void process ( Variant value, Map<String, Variant> attributes )
    {
        attributes.put ( SCALE_RAW, null );
        attributes.put ( SCALE_ERROR, null );
        try
        {
            Variant scaleFactor = _scaleFactor.getValue ();
            // only process if we have a scale factor
            if ( !scaleFactor.isNull () )
            {
                attributes.put ( SCALE_RAW, new Variant ( value ) );
                value.setValue ( value.asDouble () * scaleFactor.asDouble () );
            }
        }
        catch ( Exception e )
        {
            attributes.put ( SCALE_ERROR, new Variant ( e.getMessage () ) );
        }
        
        addAttributes ( attributes );
    }
    
    
}
