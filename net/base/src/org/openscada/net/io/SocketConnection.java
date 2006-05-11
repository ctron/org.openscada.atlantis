package org.openscada.net.io;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class SocketConnection extends IOChannel implements IOChannelListener {
	
	private static Logger _log = Logger.getLogger(SocketConnection.class);
	
	protected IOProcessor _processor; 
	private SocketChannel _channel = null;
	private ConnectionListener _listener = null;
	
	private boolean _reading = false;
	
	private ByteBuffer _inputBuffer = ByteBuffer.allocate(4096);
	private List<ByteBuffer> _outputBuffers = new ArrayList<ByteBuffer> ();
	
	public SocketConnection ( IOProcessor processor ) throws IOException
	{
        if ( processor == null )
            throw new NullPointerException();
        
		_processor = processor;
		
		_channel = SocketChannel.open();
		_channel.configureBlocking(false);
	}
	
	public SocketConnection ( IOProcessor processor, SocketChannel channel ) throws IOException
	{
        if ( processor == null )
            throw new NullPointerException();
        
		_processor = processor;
		
		_channel = channel;
		_channel.configureBlocking(false);
		
		// check for registration 
		updateOps();
	}
	
	@Override
	protected void finalize() throws Throwable {
		
		_log.debug("Finalizing socket connection");
		
		close ();
		
		super.finalize();
	}
	
	public SelectableChannel getSelectableChannel ()
	{
		return _channel;
	}
	
	public void connect (final SocketAddress remote)
	{
        try
        {
            _processor.getScheduler().executeJob(new Runnable(){

                public void run ()
                {
                    try {
                        _log.debug("Initiating contact");
                        register(_processor,SelectionKey.OP_CONNECT);
                        _channel.connect ( remote );
                        _log.debug("Contact request on its way");
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }});
        }
        catch ( InterruptedException e )
        {
        }
	}
	
	public void scheduleWrite ( final ByteBuffer buffer )
	{
        _processor.getScheduler().executeJobAsync(new Runnable(){
            public void run ()
            {
                appendWriteData ( buffer );
            }
        });
	}
    
    private void appendWriteData ( ByteBuffer buffer )
    {
        if ( _channel.isOpen() )
        {
            buffer.rewind();
            synchronized ( _outputBuffers )
            {
                _outputBuffers.add ( buffer );
            }
            updateOps();
        }
    }
	    
	private void updateOps ()
	{
		if ( !_channel.isOpen() )
		{
			_log.debug ( "Unregistering socket" );
            unregister(_processor);
            _log.debug ( "Socket unregistered" );
			return;
		}
		
		int ops = 0;
		
		if ( _channel.isConnectionPending() )
		{
			ops = SelectionKey.OP_CONNECT;
		}
		else if ( _channel.isConnected() )
		{
			if ( _reading )
				ops += SelectionKey.OP_READ;
			if ( _outputBuffers.size() > 0 )
				ops += SelectionKey.OP_WRITE;
		}
        
        if ( ops > 0 )
        {
            _log.debug ( "Setting ops = " + ops );
            register ( _processor, ops );
        }
        else
        {
            _log.debug ( "Unregistering socket" );
            unregister ( _processor );
        }
	}
	
	public void triggerRead ()
	{
		_reading = true;
		updateOps ();
	}
	public void stopRead ()
	{
		_reading = false;
		updateOps ();
	}

	public void handleConnect()
	{
		_log.debug("Connection request complete");
		
		if ( _channel.isConnectionPending() )
		{
			try {
				if ( _channel.finishConnect() )
				{
					_log.debug("Connection established");
					if ( _listener != null )
						_listener.connected();
				}
				else
				{
					_log.info("Unable to connect");
					_listener.connectionFailed (null);
					close();
				}
			}
			catch ( IOException e )
			{
				_log.info("Connection request failed: ", e );
				if ( _listener != null )
					_listener.connectionFailed(e);
				close ();
			}
		}
        else
            _log.debug("Handle connect called although socket is not connecting");
        
		updateOps();
	}

	public void close ()
	{
		
        synchronized(_outputBuffers)
        {
            _outputBuffers.clear();
        }
		
		try
        {
			if ( _channel.isOpen() )
			{
				_log.debug ( "Closing connection" );
				_channel.close();
				
				if ( _listener != null )
					_listener.closed();
			}
            updateOps();
		}
        catch (IOException e)
        {
			e.printStackTrace();
		}
		
	}
	
	public void handleRead()
	{
		_inputBuffer.clear();
		
		try {
			int rc;
			rc = _channel.read(_inputBuffer);
			
			_log.debug ( "Performed read: rc = " + rc );
			
			if ( rc < 0 )
            {
				close ();
                return;
            }
			
			_inputBuffer.flip();			
			
			if ( _listener != null )
				_listener.read(_inputBuffer);
			
		} catch (IOException e) {
		    _log.warn("Failed to read", e);
			close ();
		}
	}

	public void handleWrite()
	{
		_log.debug("Write event");
		
		synchronized ( _outputBuffers )
		{
			Iterator<ByteBuffer> i = _outputBuffers.iterator();
			
			_log.debug(_outputBuffers.size() + " buffer(s) in outbound queue (before)");
			
			while ( i.hasNext() )
			{
				ByteBuffer buffer = i.next();
				
				while ( buffer.hasRemaining() )
				{
					try {
						int rc = _channel.write(buffer);
						
						_log.debug ( "write: rc = " + rc );
						
						if ( !buffer.hasRemaining() )
						{
							i.remove();
							continue;
						}
						
						if ( rc < 0 )
						{
							close ();
							return;
						}
						else if ( rc == 0 )
						{
							// output buffer is full .. so stop here
							return;
						}
					} catch (IOException e) {
						close ();
						return;
					}
				}
			}
		} // end-sync
		
		_log.debug(_outputBuffers.size() + " buffer(s) in outbound queue (after)");
		
		updateOps();
	}

	public void handleAccept()
	{
		
	}

	public ConnectionListener getListener() {
		return _listener;
	}

	public void setListener(ConnectionListener listener) {
		_listener = listener;
	}


    public IOChannelListener getIOChannelListener ()
    {
        return this;
    }
}
