/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

public class ManualOverrideChainItem extends BaseChainItemCommon
{
    public static final String ORIGINAL_VALUE = "org.openscada.da.manual.original.value";
    public static final String MANUAL_ACTIVE = "org.openscada.da.manual.active";
    public static final String MANUAL_VALUE = "org.openscada.da.manual.value";
    public static final String MANUAL_TIMESTAMP = "org.openscada.da.manual.timestamp";
    public static final String MANUAL_USER = "org.openscada.da.manual.user";
    public static final String MANUAL_REASON = "org.openscada.da.manual.reason";

    private VariantBinder manualValue = new VariantBinder ( new Variant () );
    private VariantBinder manualReason = new VariantBinder ( new Variant () );
    private VariantBinder manualTimestamp = new VariantBinder ( new Variant () );
    private VariantBinder manualUser = new VariantBinder ( new Variant () );

    public ManualOverrideChainItem ( HiveServiceRegistry serviceRegistry )
    {
        super ( serviceRegistry );

        addBinder ( MANUAL_VALUE, manualValue );
        addBinder ( MANUAL_REASON, manualReason );
        addBinder ( MANUAL_TIMESTAMP, manualTimestamp );
        addBinder ( MANUAL_USER, manualUser );
        setReservedAttributes ( ORIGINAL_VALUE, MANUAL_ACTIVE );
    }
    
    public void process ( Variant value, Map<String, Variant> attributes )
    {
        attributes.put ( MANUAL_ACTIVE, null );
        attributes.put ( ORIGINAL_VALUE, null );
        
        if ( !manualValue.getValue ().isNull () )
        {
            attributes.put ( ORIGINAL_VALUE, new Variant ( value ) );
            value.setValue ( new Variant ( this.manualValue.getValue () ) );
            attributes.put ( MANUAL_ACTIVE, new Variant ( true ) );
        }
        addAttributes ( attributes );
    }
}
