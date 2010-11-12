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

package org.openscada.ae.server.http.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openscada.ae.Event;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.server.http.monitor.EventMonitorEvaluator;

public class JsonServlet extends HttpServlet
{
    private static final long serialVersionUID = -2152989291571139312L;

    private final EventProcessor eventProcessor;

    private final EventMonitorEvaluator eventMonitorEvaluator;

    public JsonServlet ( final EventProcessor eventProcessor, final EventMonitorEvaluator evaluator )
    {
        if ( eventProcessor == null )
        {
            throw new IllegalArgumentException ( "eventProcessor must not be null" );
        }
        if ( evaluator == null )
        {
            throw new IllegalArgumentException ( "evaluator must not be null" );
        }
        this.eventProcessor = eventProcessor;
        this.eventMonitorEvaluator = evaluator;
    }

    @Override
    protected void doPost ( final HttpServletRequest request, final HttpServletResponse response ) throws ServletException, IOException
    {
        if ( request.getPathInfo () == null )
        {
            send404Error ( request, response );
            return;
        }
        if ( request.getPathInfo ().equals ( "/publish" ) || request.getPathInfo ().equals ( "/publish/" ) )
        {
            // read input
            final BufferedReader reader = request.getReader ();
            final char[] buffer = new char[4 * 1024];
            int len;
            final StringBuilder sb = new StringBuilder ();
            while ( ( len = reader.read ( buffer, 0, buffer.length ) ) != -1 )
            {
                sb.append ( buffer, 0, len );
            }
            final Event event = EventSerializer.deserializeEvent ( sb.toString () );
            // publish event
            final Event evalEvent = this.eventMonitorEvaluator.evaluate ( event );
            this.eventProcessor.publishEvent ( evalEvent );
            // return output
            response.setContentType ( "text/html" );
            final PrintWriter pw = new PrintWriter ( response.getOutputStream () );
            pw.write ( "OK" );
            pw.close ();
        }
        else
        {
            send404Error ( request, response );
        }
    }

    private void send404Error ( final HttpServletRequest request, final HttpServletResponse response ) throws IOException
    {
        response.sendError ( HttpServletResponse.SC_NOT_FOUND );
    }
}