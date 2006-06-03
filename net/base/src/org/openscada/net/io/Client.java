package org.openscada.net.io;

import java.io.IOException;
import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.Message;

public class Client implements ConnectionStateListener
{
	
	private static Logger _log = Logger.getLogger ( Client.class );
    
    private int RECONNECT_TIMEOUT = Integer.getInteger ( "openscada.net.reconnect_period" , 10000 );
	
	private IOProcessor _processor = null;
	private MessageListener _listener;
	private ClientConnection _connection = null;
	private ConnectionStateListener _stateListener = null;
    
    private boolean _connected = false;
    private boolean _autoReconnect = false;
    
    private SocketAddress _lastRemote = null;

	public Client ( IOProcessor processor, MessageListener listener, ConnectionStateListener stateListener, boolean autoReconnect )
	{
		_processor = processor;
		_listener = listener;
		_stateListener = stateListener;
        _autoReconnect = autoReconnect;
	}
	
	public void sendMessage ( Message message )
	{
		if ( _connection != null )
		{
		    _connection.sendMessage ( message );
		}
	}
	
	public void connect ( SocketAddress remote )
	{
        _lastRemote = remote;
        performConnect ( remote );
	}
    
    public void connect ( SocketAddress remote, boolean wait )
    {
        _lastRemote = remote;
        scheduleConnectJob ( remote, RECONNECT_TIMEOUT );
    }
    
    private void performConnect ( SocketAddress remote )
    {
        _log.debug ( "connecting..." );
        
        closeCurrent ();
        
        try
        {
            SocketConnection channel = new SocketConnection ( _processor );
            _connection = new ClientConnection ( _listener, this, channel );
            channel.connect ( remote );
        }
        catch ( IOException e )
        {
            e.printStackTrace ();
        }
    }

    private void closeCurrent ()
    {
        if ( _connection != null )
        {
            _connected = false;
            _connection.close ();
            _connection = null;
        }
    }
    
	public void closed ( Exception error )
	{
		_log.debug ( "Connection closed" );
		
		if ( _stateListener != null )
		    _stateListener.closed ( error );

        _connected = false;
        
        if ( _autoReconnect && ( _lastRemote != null ) )
            connect ( _lastRemote, true );
	}

	public void opened ()
	{
        _connected = true;
        
		_log.debug ( "Connection open" );
		
		if ( _stateListener != null )
			_stateListener.opened ();
	}

	public ClientConnection getConnection ()
    {
		return _connection;
	}

    public boolean isConnected ()
    {
        return _connected;
    }
    
    private void scheduleConnectJob ( final SocketAddress remote, int timeout )
    {
        _log.debug ( "adding connect job" );
        
        _processor.getScheduler().scheduleJob ( new Runnable(){
            
            public void run() {
                performConnect ( remote );
            }}, timeout );
    }
}
