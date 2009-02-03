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

package org.openscada.ae.submitter.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscada.ae.core.Event;
import org.openscada.core.Variant;

public class Application
{
    public static Event fillEventAndProperties ( final String[] args, final Properties properties )
    {
        String id = "openscada_ae_submitter." + System.currentTimeMillis ();
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        final Pattern p1 = Pattern.compile ( "-A([0-9a-zA-Z\\.-_:]+)=(.*)" );
        final Pattern p2 = Pattern.compile ( "-P([0-9a-zA-Z\\.-_:]+)=(.*)" );
        final Pattern p3 = Pattern.compile ( "--id=(.*)" );

        for ( final String arg : args )
        {
            Matcher m;

            // check for attribute
            m = p1.matcher ( arg );
            if ( m.matches () )
            {
                final String key = m.group ( 1 );
                final String value = m.group ( 2 );
                attributes.put ( key, new Variant ( value ) );
            }

            // check for property
            m = p2.matcher ( arg );
            if ( m.matches () )
            {
                final String key = m.group ( 1 );
                final String value = m.group ( 2 );
                properties.put ( key, value );
            }

            m = p3.matcher ( arg );
            if ( m.matches () )
            {
                id = m.group ( 1 );
            }
        }

        final Event event = new Event ( id );
        event.setAttributes ( attributes );
        return event;
    }

    public static void main ( final String[] args ) throws Throwable
    {
        final Properties properties = new Properties ();
        final Event event = fillEventAndProperties ( args, properties );

        new Submitter ().submitEvent ( properties, event );
    }
}
