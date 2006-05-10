package org.openscada.net.io;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openscada.net.base.MessageListener;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.Message;
import org.openscada.utils.timing.Scheduler;

public class Connection implements ConnectionListener, MessageListener {
	
    private int _timeoutLimit = Integer.getInteger ( "org.openscada.net.message_timeout", 10*1000 );
    private static Scheduler _scheduler = new Scheduler();
    
	private Codec _codec = null;
	protected SocketConnection _connection = null;
	
	private ConnectionStateListener _connectionStateListener = null;
	
	private long _sequence = 0;
    
    private MessageListener _listener = null;
    
    private Scheduler.Job _timeoutJob = null;
    
    private class MessageTag
    {
        private MessageStateListener _listener;
        private long _timestamp;
        
        public MessageStateListener getListener ()
        {
            return _listener;
        }
        public void setListener ( MessageStateListener listener )
        {
            _listener = listener;
        }
        public long getTimestamp ()
        {
            return _timestamp;
        }
        public void setTimestamp ( long timestamp )
        {
            _timestamp = timestamp;
        }
    }
    
    private Map<Long,MessageTag> _tagList = new HashMap<Long,MessageTag>(); 
	
	public Connection ( MessageListener listener, ConnectionStateListener connectionStateListener, SocketConnection connection )
	{
		_connectionStateListener = connectionStateListener;
        _listener = listener;
        _connection = connection;
        
		_codec = new Codec ( this, this );
        
        addTimeOutJob();
	}
	
	public Connection(MessageListener listener, SocketConnection connection)
	{
        _listener = listener;
		_codec = new Codec ( this, this);
		_connection = connection;
        
        addTimeOutJob();
	}
    
    private void addTimeOutJob ()
    {
        if ( _timeoutJob != null )
            return;
        
        final WeakReference<Connection> _this = new WeakReference<Connection>(this);
        
        _timeoutJob = _scheduler.addJob ( new Runnable(){

            public void run ()
            {
                Connection c = _this.get();
                if ( c == null )
                    _scheduler.removeJob(this);
                else
                    c.processTimeOuts();
            }}, 1000);
    }
    
    private void removeTimeOutJob ()
    {
        if ( _timeoutJob != null )
        {
            _scheduler.removeJob(_timeoutJob);
        }
    }
    
	public void sendMessage ( Message message )
	{
		message.setSequence(_sequence++);
		_connection.scheduleWrite(_codec.code(message));
	}
    
    public void sendMessage ( Message message, MessageStateListener listener )
    {
        MessageTag tag = new MessageTag();
        
        tag.setListener(listener);
        tag.setTimestamp(System.currentTimeMillis());
        
        message.setSequence(_sequence++);
        
        _tagList.put ( message.getSequence(), tag );
        
        _connection.scheduleWrite ( _codec.code(message) );
    }
	
	public void read(ByteBuffer buffer)
	{
		_codec.decode(buffer);
	}

	public void written() {
		// no op
	}

	public void connected() {
		if ( _connectionStateListener != null )
			_connectionStateListener.opened ();
	}

	public void connectionFailed(IOException e) {
		if ( _connectionStateListener != null )
			_connectionStateListener.closed ();
	}

	public void closed() {
        
        removeTimeOutJob();
        
		if ( _connectionStateListener != null )
			_connectionStateListener.closed ();
	}
	
	public void close ()
	{
		_connection.close();
	}

    public void messageReceived ( Connection connection, Message message )
    {
        Long seq = Long.valueOf(message.getReplySequence());
        
        _listener.messageReceived ( connection, message );
        
        synchronized ( _tagList )
        {
            if ( _tagList.containsKey( seq ) )
            {
                try
                {
                    _tagList.get(seq).getListener().messageComplete ( message );
                }
                catch ( Exception e )
                {
                }
            }
        }
    }
    
    private void processTimeOuts ()
    {
        synchronized ( _tagList )
        {
            for ( Iterator<Map.Entry<Long,MessageTag>> i = _tagList.entrySet().iterator(); i.hasNext() ;  )
            {
                MessageTag tag = i.next().getValue();
                
                if ( (System.currentTimeMillis() - tag.getTimestamp()) >= _timeoutLimit )
                {
                    try
                    {
                        tag.getListener().messageTimedOut();
                    }
                    catch ( Exception e )
                    {
                    }
                    i.remove();
                }
            }
        }
    }
	
}
