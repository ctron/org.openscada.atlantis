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

package org.openscada.da.server.common.chain.item;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.chain.BaseChainItemCommon;
import org.openscada.da.server.common.chain.VariantBinder;

/**
 * This class 
 * @author jens
 *
 */
public class ManualOverrideChainItem extends BaseChainItemCommon
{
    public static final String MANUAL_BASE = "org.openscada.da.manual";

    public static final String ORIGINAL_VALUE = MANUAL_BASE + ".value.original";

    public static final String ORIGINAL_TIMESTAMP = MANUAL_BASE + ".timestamp.original";

    public static final String MANUAL_ACTIVE = MANUAL_BASE + ".active";

    public static final String MANUAL_VALUE = MANUAL_BASE + ".value";

    public static final String MANUAL_TIMESTAMP = MANUAL_BASE + ".timestamp";

    public static final String MANUAL_USER = MANUAL_BASE + ".user";

    public static final String MANUAL_REASON = MANUAL_BASE + ".reason";

    private final VariantBinder manualValue = new VariantBinder ( new Variant () );

    private final VariantBinder manualReason = new VariantBinder ( new Variant () );

    private final VariantBinder manualUser = new VariantBinder ( new Variant () );

    private Calendar manualTimestamp;

    public ManualOverrideChainItem ( final HiveServiceRegistry serviceRegistry )
    {
        super ( serviceRegistry );

        addBinder ( MANUAL_VALUE, this.manualValue );
        addBinder ( MANUAL_REASON, this.manualReason );
        addBinder ( MANUAL_USER, this.manualUser );
        setReservedAttributes ( ORIGINAL_VALUE, MANUAL_ACTIVE );
    }

    /**
     * loading initial properties from the storage service
     */
    @Override
    protected void loadInitialProperties ()
    {
        // load all other properties
        super.loadInitialProperties ();

        // load the manual timestamp
        this.manualTimestamp = null;
        final Map<String, Variant> properties = loadStoredValues ( new HashSet<String> ( Arrays.asList ( MANUAL_TIMESTAMP ) ) );
        if ( properties.containsKey ( MANUAL_TIMESTAMP ) )
        {
            final Variant value = properties.get ( MANUAL_TIMESTAMP );
            if ( !value.isNull () )
            {
                this.manualTimestamp = Calendar.getInstance ();
                try
                {
                    this.manualTimestamp.setTimeInMillis ( value.asLong () );
                }
                catch ( final Throwable e )
                {
                }
            }
        }
    }

    @Override
    public WriteAttributeResults setAttributes ( final Map<String, Variant> attributes )
    {
        final Variant value = attributes.get ( MANUAL_VALUE );
        if ( value != null )
        {
            if ( value.isNull () )
            {
                // if the value is set as Variant#NULL clear the timestamp
                this.manualTimestamp = null;
            }
            else
            {
                // we got a valid value
                this.manualTimestamp = Calendar.getInstance ();
            }
        }
        else if ( attributes.containsKey ( MANUAL_VALUE ) )
        {
            // if the value is set but as "null" then clear the timestamp
            this.manualTimestamp = null;
        }
        return super.setAttributes ( attributes );
    }

    @Override
    protected void performWriteBinders ( final Map<String, Variant> attributes )
    {
        // if we got a timestamp, store it
        if ( this.manualTimestamp != null )
        {
            attributes.put ( MANUAL_TIMESTAMP, new Variant ( this.manualTimestamp.getTimeInMillis () ) );
        }
        super.performWriteBinders ( attributes );
    }

    public Variant process ( final Variant value, final Map<String, Variant> attributes )
    {
        attributes.put ( MANUAL_ACTIVE, null );
        attributes.put ( ORIGINAL_VALUE, null );
        attributes.put ( ORIGINAL_TIMESTAMP, null );
        attributes.put ( MANUAL_TIMESTAMP, null );

        Variant newValue = null;

        if ( !this.manualValue.getValue ().isNull () )
        {
            attributes.put ( ORIGINAL_VALUE, new Variant ( value ) );
            newValue = this.manualValue.getValue ();
            attributes.put ( MANUAL_ACTIVE, Variant.TRUE );
            attributes.put ( MANUAL_TIMESTAMP, new Variant ( this.manualTimestamp.getTimeInMillis () ) );

            // if we have an original timestamp, replace it
            final Variant originalTimestamp = attributes.get ( "timestamp" );
            if ( originalTimestamp != null )
            {
                attributes.put ( ORIGINAL_TIMESTAMP, new Variant ( originalTimestamp ) );
            }
            attributes.put ( "timestamp", new Variant ( this.manualTimestamp.getTimeInMillis () ) );
        }
        addAttributes ( attributes );

        return newValue;
    }
}
