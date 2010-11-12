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

package org.openscada.da.server.opc.browser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.openscada.da.server.opc.Hive;
import org.openscada.da.server.opc.connection.ConnectionSetup;
import org.openscada.da.server.opc.connection.OPCModel;
import org.openscada.da.server.opc.job.Worker;
import org.openscada.da.server.opc.job.impl.BrowseJob;
import org.openscada.utils.beans.AbstractPropertyChange;

public class OPCBrowserManager extends AbstractPropertyChange
{

    private static class Request
    {
        private final BrowseRequest request;

        private final BrowseRequestListener listener;

        public Request ( final BrowseRequest request, final BrowseRequestListener listener )
        {
            this.request = request;
            this.listener = listener;
        }
    }

    private static final String PROP_ACTIVE_REQUESTS = "activeRequests";

    private static final String PROP_QUEUED_REQUESTS = "queuedRequests";

    private final Worker worker;

    private final OPCModel model;

    private final List<Request> requests = new LinkedList<Request> ();

    private int activeRequests = 0;

    private int queuedRequests = 0;

    public OPCBrowserManager ( final Worker worker, final ConnectionSetup configuration, final OPCModel model, final Hive hive )
    {
        this.worker = worker;
        this.model = model;
    }

    public int getQueuedRequests ()
    {
        return this.queuedRequests;
    }

    protected void setQueuedRequests ( final int queuedRequests )
    {
        final int oldQueuedRequests = this.queuedRequests;
        this.queuedRequests = queuedRequests;
        firePropertyChange ( PROP_QUEUED_REQUESTS, oldQueuedRequests, queuedRequests );
    }

    public int getActiveRequests ()
    {
        return this.activeRequests;
    }

    protected void setActiveRequests ( final int activeRequests )
    {
        final int oldActiveRequests = this.activeRequests;
        this.activeRequests = activeRequests;
        firePropertyChange ( PROP_ACTIVE_REQUESTS, oldActiveRequests, activeRequests );
    }

    /**
     * Perform all browse requests
     * @throws Throwable 
     */
    public void performBrowse () throws Throwable
    {
        List<Request> currentRequests;
        synchronized ( this.requests )
        {
            currentRequests = new ArrayList<Request> ( this.requests );
            this.requests.clear ();
            setQueuedRequests ( 0 );
        }

        if ( !currentRequests.isEmpty () )
        {
            int len = currentRequests.size ();
            for ( final Request request : currentRequests )
            {
                setActiveRequests ( len-- );
                processRequest ( request );
            }
            setActiveRequests ( 0 );
        }
    }

    private void processRequest ( final Request request ) throws Throwable
    {
        final BrowseJob job = new BrowseJob ( this.model.getDefaultTimeout (), this.model, request.request );

        try
        {
            final BrowseResult result = this.worker.execute ( job, job );
            request.listener.browseComplete ( result );
        }
        catch ( final Throwable e )
        {
            request.listener.browseError ( e );
            throw e;
        }
    }

    public void addBrowseRequest ( final BrowseRequest request, final BrowseRequestListener listener )
    {
        if ( listener == null )
        {
            throw new NullPointerException ( "Listener must not be null" );
        }

        if ( !this.model.isConnected () )
        {
            listener.browseError ( new RuntimeException ( "OPC is not connected" ).fillInStackTrace () );
            return;
        }

        synchronized ( this.requests )
        {
            this.requests.add ( new Request ( request, listener ) );
            setQueuedRequests ( this.requests.size () );
        }
    }

}
