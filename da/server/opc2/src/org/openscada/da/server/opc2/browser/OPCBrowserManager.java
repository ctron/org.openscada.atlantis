package org.openscada.da.server.opc2.browser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.openscada.da.server.opc2.Hive;
import org.openscada.da.server.opc2.connection.ConnectionSetup;
import org.openscada.da.server.opc2.connection.OPCModel;
import org.openscada.da.server.opc2.job.Worker;
import org.openscada.da.server.opc2.job.impl.BrowseJob;

public class OPCBrowserManager
{

    private static class Request
    {
        private BrowseRequest request;

        private BrowseRequestListener listener;

        public Request ( BrowseRequest request, BrowseRequestListener listener )
        {
            this.request = request;
            this.listener = listener;
        }
    }

    private Worker worker;

    private OPCModel model;

    private List<Request> requests = new LinkedList<Request> ();

    public OPCBrowserManager ( Worker worker, ConnectionSetup configuration, OPCModel model, Hive hive )
    {
        this.worker = worker;
        this.model = model;
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
        }

        for ( Request request : currentRequests )
        {
            processRequest ( request );
        }
    }

    private void processRequest ( Request request ) throws Throwable
    {
        BrowseJob job = new BrowseJob ( this.model.getDefaultTimeout (), this.model, request.request );

        try
        {
            BrowseResult result = this.worker.execute ( job, job );
            request.listener.browseComplete ( result );
        }
        catch ( Throwable e )
        {
            request.listener.browseError ( e );
            throw e;
        }
    }

    public void addBrowseRequest ( BrowseRequest request, BrowseRequestListener listener )
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
        }
    }

}
