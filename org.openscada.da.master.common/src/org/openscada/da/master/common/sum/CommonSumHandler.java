/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.master.common.sum;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.master.AbstractMasterHandlerImpl;
import org.openscada.da.master.MasterItem;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.openscada.utils.str.StringHelper;

public class CommonSumHandler extends AbstractMasterHandlerImpl
{
    private Pattern pattern;

    private String tag;

    private String prefix = "osgi.source";

    private boolean debug = false;

    private String suffix;

    private String attributeName;

    public CommonSumHandler ( final ObjectPoolTracker<MasterItem> poolTracker )
    {
        super ( poolTracker );
    }

    @Override
    public synchronized void update ( final UserInformation userInformation, final Map<String, String> parameters ) throws Exception
    {
        super.update ( userInformation, parameters );
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        this.debug = cfg.getBoolean ( "debug", false );
        this.prefix = cfg.getString ( "prefix", "osgi.source" );

        this.tag = cfg.getString ( "tag" );

        this.suffix = cfg.getString ( "suffix", "." + this.tag );
        this.pattern = makePattern ( cfg.getString ( "pattern", null ) );

        this.attributeName = String.format ( "%s.%s", this.prefix, this.tag );

        reprocess ();
    }

    private Pattern makePattern ( final String string )
    {
        if ( string == null )
        {
            return null;
        }
        return Pattern.compile ( string );
    }

    @Override
    public DataItemValue dataUpdate ( final Map<String, Object> context, final DataItemValue value )
    {
        if ( this.tag == null )
        {
            return value;
        }

        final Builder builder = new DataItemValue.Builder ( value );

        // convert source errors
        convertSource ( builder );

        final Set<Object> contextSet = getContextSet ( context, this.tag );

        if ( this.debug )
        {
            builder.setAttribute ( this.prefix + ".before", Variant.valueOf ( StringHelper.join ( contextSet, "," ) ) );
        }

        // sum up
        int matches = 0;
        for ( final Map.Entry<String, Variant> entry : builder.getAttributes ().entrySet () )
        {
            final Variant pValue = entry.getValue ();
            final String name = entry.getKey ();
            if ( matches ( name, pValue ) )
            {
                if ( !contextSet.contains ( name ) )
                {
                    matches++;
                    contextSet.add ( name );
                }
            }
        }

        if ( this.debug )
        {
            builder.setAttribute ( this.prefix + ".after", Variant.valueOf ( StringHelper.join ( contextSet, "," ) ) );
        }

        builder.setAttribute ( this.tag, Variant.valueOf ( matches != 0 ) );
        if ( this.debug )
        {
            builder.setAttribute ( this.tag + ".count", Variant.valueOf ( matches ) );
        }

        return builder.build ();
    }

    private boolean matches ( final String name, final Variant value )
    {
        // if the value is null is will never match
        if ( value == null )
        {
            return false;
        }

        if ( this.pattern != null )
        {
            // if a pattern is defined ...
            if ( !this.pattern.matcher ( name ).matches () )
            {
                // ... it has to match
                return false;
            }
        }
        else
        {
            // otherwise the suffix ...
            if ( !name.endsWith ( this.suffix ) )
            {
                // ... must match
                return false;
            }
        }

        /* finally check the value itself, this may trigger
         * parsing the value as boolean (e.g. from a string) so we do this last
         */
        return value.asBoolean ();
    }

    @SuppressWarnings ( "unchecked" )
    private static Set<Object> getContextSet ( final Map<String, Object> context, final String tag )
    {
        final Object o = context.get ( tag + ".set" );
        if ( o instanceof Set<?> )
        {
            return (Set<Object>)o;
        }
        else
        {
            final Set<Object> set = new HashSet<Object> ();
            context.put ( tag + ".set", set );
            return set;
        }
    }

    private void convertSource ( final Builder builder )
    {
        Variant sourceValue;

        sourceValue = builder.getAttributes ().remove ( this.tag );
        if ( sourceValue != null )
        {
            builder.setAttribute ( this.attributeName, sourceValue );
        }

        sourceValue = builder.getAttributes ().remove ( this.tag + ".count" );
        if ( sourceValue != null && this.debug )
        {
            builder.setAttribute ( String.format ( "%s.%s.count", this.prefix, this.tag ), sourceValue );
        }

        sourceValue = builder.getAttributes ().remove ( this.tag + ".items" );
        if ( sourceValue != null && this.debug )
        {
            builder.setAttribute ( String.format ( "%s.%s.items", this.prefix, this.tag ), sourceValue );
        }
    }

}