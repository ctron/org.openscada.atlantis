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

package org.openscada.ae.server.http.monitor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ae.Event;
import org.openscada.ae.Event.Fields;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.filter.EventMatcher;
import org.openscada.ae.filter.internal.EventMatcherImpl;
import org.openscada.ae.monitor.common.AbstractStateMachineMonitorService;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.utils.lang.Pair;
import org.osgi.framework.BundleContext;

public class EventMonitorImpl extends AbstractStateMachineMonitorService implements EventMonitor
{
    private EventMatcher matcher = null;

    private String monitorType = "EVENT";

    public EventMonitorImpl ( final BundleContext context, final Executor executor, final EventProcessor eventProcessor, final String id )
    {
        super ( context, executor, eventProcessor, id );
    }

    @Override
    public void update ( final Map<String, String> properties )
    {
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( properties );

        setEventInformationAttributes ( convertAttributes ( cfg ) );
        setActive ( cfg.getBoolean ( "active", true ) );
        setRequireAkn ( cfg.getBoolean ( "requireAck", true ) );
        setEventMatcher ( cfg.getString ( "filter", "" ) );
        setMonitorType ( cfg.getString ( "monitorType", "EVENT" ) );
    }

    private void setEventMatcher ( final String filter )
    {
        this.matcher = new EventMatcherImpl ( filter );
    }

    private void setMonitorType ( final String monitorType )
    {
        this.monitorType = monitorType;
    }

    private static Map<String, Variant> convertAttributes ( final ConfigurationDataHelper cfg )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        for ( final Map.Entry<String, String> entry : cfg.getPrefixed ( "info." ).entrySet () )
        {
            attributes.put ( entry.getKey (), new Variant ( entry.getValue () ) );
        }

        return attributes;
    }

    @Override
    public synchronized Pair<Boolean, Event> evaluate ( final Event event )
    {
        if ( this.matcher != null )
        {
            if ( this.matcher.matches ( event ) )
            {
                // FIXME: just for now, the real implementation should set AKN directly
                final SequenceEventDecorator eventDecorator = new SequenceEventDecorator ();
                eventDecorator.setSequence ( 1 );
                setFailure ( Variant.NULL, event.getSourceTimestamp (), eventDecorator );
                eventDecorator.setSequence ( 2 );
                setOk ( Variant.NULL, event.getSourceTimestamp (), eventDecorator );
                final Event resultEvent = Event.create () //
                .event ( event ) //
                .attribute ( Fields.COMMENT, annotateCommentWithSource ( event ) ) //
                .attribute ( Fields.SOURCE, getId () ) //
                .attribute ( Fields.MONITOR_TYPE, this.monitorType )//
                .attribute ( "sequence", 0 )//
                .build ();
                return new Pair<Boolean, Event> ( true, resultEvent );
            }
        }
        return new Pair<Boolean, Event> ( false, event );
    }

    private Variant annotateCommentWithSource ( final Event event )
    {
        final StringBuilder sb = new StringBuilder ();
        final Variant originalComment = event.getField ( Fields.COMMENT );
        final Variant originalSource = event.getField ( Fields.SOURCE );
        boolean commentThere = false;
        if ( originalComment != null && originalComment.isString () && originalComment.asString ( "" ).length () > 0 )
        {
            commentThere = true;
            sb.append ( originalComment.asString ( "" ) );
        }
        if ( originalSource != null && originalSource.isString () && originalSource.asString ( "" ).length () > 0 )
        {
            if ( commentThere )
            {
                sb.append ( "; " );
            }
            sb.append ( "original source: " );
            sb.append ( originalSource.asString ( "" ) );
        }

        return new Variant ( sb.toString () );
    }

    @Override
    public void init ()
    {
        super.init ();
        setOk ( Variant.NULL, new Date () );
    }
}
