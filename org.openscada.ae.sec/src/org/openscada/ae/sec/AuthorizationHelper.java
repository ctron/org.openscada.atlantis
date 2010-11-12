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

package org.openscada.ae.sec;

import java.util.Date;
import java.util.Map;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.Event.Fields;
import org.openscada.ae.event.EventProcessor;
import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.UserInformation;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

public class AuthorizationHelper extends org.openscada.sec.osgi.AuthorizationHelper
{

    private final boolean logAll;

    private final EventProcessor eventProcessor;

    public AuthorizationHelper ( final BundleContext context ) throws InvalidSyntaxException
    {
        this ( context, Boolean.getBoolean ( "org.openscada.ae.sec.logAll" ) );
    }

    public AuthorizationHelper ( final BundleContext context, final boolean logAll ) throws InvalidSyntaxException
    {
        super ( context );
        this.eventProcessor = new EventProcessor ( context );
        this.logAll = logAll;
    }

    @Override
    public void open ()
    {
        this.eventProcessor.open ();
        super.open ();
    }

    @Override
    public void close ()
    {
        super.close ();
        this.eventProcessor.close ();
    }

    @Override
    public AuthorizationResult authorize ( final String objectId, final String objectType, final String action, final UserInformation userInformation, final Map<String, Object> context )
    {
        return authorize ( objectId, objectType, action, userInformation, context, DEFAULT_RESULT );
    }

    @Override
    public AuthorizationResult authorize ( final String objectId, final String objectType, final String action, final UserInformation userInformation, final Map<String, Object> context, final AuthorizationResult defaultResult )
    {
        if ( this.logAll )
        {
            this.eventProcessor.publishEvent ( makeEvent ( objectId, objectType, action, userInformation, null ) );
        }

        final AuthorizationResult result = super.authorize ( objectId, objectType, action, userInformation, context, defaultResult );

        if ( result != null && !result.isGranted () )
        {
            this.eventProcessor.publishEvent ( makeEvent ( objectId, objectType, action, userInformation, result ) );
        }

        return result;
    }

    private Event makeEvent ( final String objectId, final String objectType, final String action, final UserInformation userInformation, final AuthorizationResult result )
    {
        final EventBuilder builder = Event.create ();

        builder.sourceTimestamp ( new Date () );
        if ( userInformation != null )
        {
            builder.attribute ( Fields.ACTOR_NAME, userInformation.getName () );
            builder.attribute ( Fields.ACTOR_TYPE, "USER" );
        }

        builder.attribute ( Fields.MONITOR_TYPE, "SEC" );
        builder.attribute ( Fields.ITEM, objectId );
        if ( result != null )
        {
            builder.attribute ( Fields.MESSAGE, String.format ( "%s: %s", result.getErrorCode (), result.getMessage () ) );
            builder.attribute ( "CODE", result.getErrorCode () );
            builder.attribute ( Fields.PRIORITY, 1000 );
            builder.attribute ( Fields.EVENT_TYPE, "DENY" );
        }
        else
        {
            builder.attribute ( Fields.EVENT_TYPE, "REQ" );
            builder.attribute ( Fields.MESSAGE, "Requesting authorization" );
            builder.attribute ( Fields.PRIORITY, 0 );
        }
        builder.attribute ( Fields.SOURCE, objectId );
        builder.attribute ( "SOURCE_TYPE", objectType );
        builder.attribute ( Fields.VALUE, action );

        return builder.build ();
    }
}
