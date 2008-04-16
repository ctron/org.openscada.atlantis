package org.openscada.io.lcl;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.openscada.io.lcl.data.Request;
import org.openscada.io.lcl.data.Response;
import org.openscada.net.io.IOProcessor;
import org.openscada.net.line.LineBasedClient;
import org.openscada.net.line.LineBasedConnection;
import org.openscada.net.line.LineHandler;

public class Client implements LineHandler
{
    private static Logger _log = Logger.getLogger ( Client.class );
    
    public interface RequestStateListener
    {
        public enum State
        {
            SENT,
            ERROR,
            RESPONSE,
        }
        void stateChanged ( State state, Response response );
        boolean isResponse ( Response response );
    }
    
    private class Job
    {
        private Request _request = null;
        private RequestStateListener _listener = null;
        
        public Job ( Request request, RequestStateListener listener )
        {
            super ();
            _request = request;
            _listener = listener;
        }
        
        public RequestStateListener getListener ()
        {
            return _listener;
        }

        public Request getRequest ()
        {
            return _request;
        }
        
        public void setState ( RequestStateListener.State state, Response response )
        {
            if ( _listener != null )
            {
                _listener.stateChanged ( state, response );
                switch ( state )
                {
                case RESPONSE:
                case ERROR:
                    synchronized ( _listener )
                    {
                        _listener.notifyAll ();
                    }
                    break;
                default:
                    break;
                }
            }
        }
    }

    private Queue<Job> _requestList = new LinkedList<Job> ();
    private LineBasedClient _client = null;
    private LineBasedConnection _connection = null;
    private ClientHandler _handler = null;
    
    private Job _currentJob = null;
    
    private Integer _timeout = null;
    
    public Client ( IOProcessor processor, ClientHandler handler ) throws IOException
    {
        _handler = handler;
        _client = new LineBasedClient ( processor, this );
        
        _handler.setClient ( this );
    }
    
    synchronized public void sendRequest ( Request request, RequestStateListener listener )
    {
        if ( _client == null )
        {
            return;
        }
        
        Job job = new Job ( request, listener );
        
        if ( _currentJob == null )
            processSendJob ( job );
        else
            _requestList.add ( job );
    }
    
    private void processSendJob ( Job job )
    {
        if ( job == null )
            return;
        
        String str = String.format ( "%s %s", job.getRequest ().getCommand (), job.getRequest ().getData () );
        synchronized ( this )
        {
            if ( _connection != null )
                _connection.sendLine ( str );
            
            job.setState ( RequestStateListener.State.SENT, null );
            _currentJob = job;
        }
    }
    
    synchronized private void processResponse ( Response response )
    {
        if ( _currentJob == null )
            processEvent ( response );
        else if ( _currentJob.getListener () == null )
            processEvent ( response );
        else if ( !_currentJob.getListener ().isResponse ( response ) )
            processEvent ( response );
        else
            completeJob ( response );
    }
    
    private void processEvent ( Response response )
    {
        if ( _handler != null )
            _handler.handleEvent ( response );
    }

    synchronized private void completeJob ( Response response )
    {
        _currentJob.setState ( RequestStateListener.State.RESPONSE, response );
        _currentJob = null;
        
        Job nextJob = _requestList.poll ();
        if ( nextJob != null )
        {
            processSendJob ( nextJob );
        }
    }
    
    public void handleLine ( String line )
    {
        StringTokenizer tok = new StringTokenizer ( line, " \n\r\t" );
        
        try
        {
            Response response = new Response ();
            response.setCode ( Integer.valueOf ( tok.nextToken () ) );
            response.setData ( tok.nextToken ( "" ).trim () );
            
            processResponse ( response );
        }
        catch ( Exception e )
        {
            _log.warn ( "Failed to handle line", e );
        }
    }

    public void setConnection ( LineBasedConnection connection )
    {
        synchronized ( this )
        {
            _connection = connection;
        }
    }

    public void closed ()
    {
        _handler.closed ();
        
        synchronized ( this )
        {
            if ( _currentJob != null )
            {
                _currentJob.setState ( RequestStateListener.State.ERROR, null );
                _currentJob = null;
            }
            for ( Job job : _requestList )
            {
                job.setState ( RequestStateListener.State.ERROR, null );
            }
            _requestList.clear ();
        }
    }

    public void connected ()
    {
        _handler.connected ();
        
        applyTimeout ();
    }
    
    protected void applyTimeout ()
    {
        LineBasedConnection connection = _connection;
        if ( connection == null )
        {
            return;
        }
        
        Integer timeout = _timeout;
        if ( timeout == null )
        {
            timeout = 0;
        }
        connection.setTimeout ( timeout );
    }

    public void connectionFailed ( Throwable throwable )
    {
        _handler.connectionFailed ( throwable );
    }

    public void connect ( SocketAddress address )
    {
        _client.connect ( address );
    }

    public Integer getTimeout ()
    {
        return _timeout;
    }

    public void setTimeout ( Integer timeout )
    {
        _timeout = timeout;
        applyTimeout ();
    }
}
