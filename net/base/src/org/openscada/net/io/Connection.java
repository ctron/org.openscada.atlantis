package org.openscada.net.io;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.codec.InvalidValueTypeException;
import org.openscada.net.codec.Protocol;
import org.openscada.net.codec.ProtocolGMPP;
import org.openscada.utils.timing.Scheduler;

public class Connection implements ConnectionListener, MessageListener
{
    private static Logger _log = Logger.getLogger ( Connection.class );

    private static final long MAX_SEQUENCE = 0x7FFFFFFF;
    private static final long INIT_SEQUENCE = 1;

    private int _timeoutLimit = Integer.getInteger ( "org.openscada.net.message_timeout", 10*1000 );
    private static Scheduler _scheduler = new Scheduler ();

    private Protocol _protocolGMPP = null;
    protected SocketConnection _connection = null;

    private ConnectionStateListener _connectionStateListener = null;

    private long _sequence = INIT_SEQUENCE;

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

    private Map<Long,MessageTag> _tagList = new HashMap<Long,MessageTag> (); 

    public Connection ( MessageListener listener, ConnectionStateListener connectionStateListener, SocketConnection connection )
    {
        _connectionStateListener = connectionStateListener;
        _listener = listener;
        _connection = connection;

        _protocolGMPP = new ProtocolGMPP ( this, this );

        addTimeOutJob ();
    }

    public Connection ( MessageListener listener, SocketConnection connection )
    {
        _listener = listener;
        _protocolGMPP = new ProtocolGMPP ( this, this);
        _connection = connection;

        addTimeOutJob ();
    }

    private void addTimeOutJob ()
    {
        if ( _timeoutJob != null )
            return;

        final WeakReference<Connection> _this = new WeakReference<Connection> ( this );

        _timeoutJob = _scheduler.addJob ( new Runnable(){

            public void run ()
            {
                Connection c = _this.get ();
                if ( c == null )
                    _scheduler.removeJob ( this );
                else
                    c.processTimeOuts ();
            }}, 1000);
    }

    private void removeTimeOutJob ()
    {
        if ( _timeoutJob != null )
        {
            _scheduler.removeJob ( _timeoutJob );
        }
    }

    private ByteBuffer encode ( Message message )
    {
        try
        {
            return _protocolGMPP.code ( message );
        }
        catch ( InvalidValueTypeException e )
        {
            _log.warn ( "Message contained unsupported value type", e );
            return null;
        }
    }

    public void sendMessage ( Message message )
    {
        synchronized ( _connection )
        {
            message.setSequence ( nextSequence () );

            ByteBuffer buffer = encode ( message );

            if ( buffer != null )
                _connection.scheduleWrite ( buffer );
        }
    }

    public void sendMessage ( Message message, MessageStateListener listener )
    {
        MessageTag tag = new MessageTag ();

        tag.setListener ( listener );
        tag.setTimestamp ( System.currentTimeMillis () );
        
        synchronized ( _connection )
        {
            message.setSequence ( nextSequence () );

            ByteBuffer buffer = encode ( message );
            if ( buffer == null )
                return;
            
            synchronized ( _tagList )
            {
                _tagList.put ( message.getSequence (), tag );
            }

            _connection.scheduleWrite ( buffer );
        }
    }

    public void read ( ByteBuffer buffer )
    {
        _protocolGMPP.decode ( buffer );
    }

    public void written ()
    {
        // no op
    }

    public void connected ()
    {
        cleanTagList ();

        _connection.triggerRead();

        if ( _connectionStateListener != null )
            _connectionStateListener.opened ();
    }

    public void connectionFailed ( IOException e )
    {
        if ( _connectionStateListener != null )
            _connectionStateListener.closed ( e );
    }

    public void closed ()
    {
        removeTimeOutJob();

        cleanTagList ();

        if ( _connectionStateListener != null )
            _connectionStateListener.closed ( null );
    }

    public void close ()
    {
        _connection.close ();
    }

    private void cleanTagList ()
    {
        synchronized ( _tagList )
        {
            for ( Map.Entry<Long,MessageTag> tag : _tagList.entrySet() )
            {
                tag.getValue ().getListener ().messageTimedOut ();
            }
            _tagList.clear ();
        }
    }

    public void messageReceived ( Connection connection, Message message )
    {
        _log.info ( "Message received: Seq: " + message.getSequence () + " Reply: " + message.getReplySequence () );

        Long seq = Long.valueOf ( message.getReplySequence () );

        MessageTag tag = null;
        synchronized ( _tagList )
        {
            if ( _tagList.containsKey ( seq ) )
            {
                tag = _tagList.get ( seq );
                _tagList.remove ( seq );
            }
        }
        
        try
        {
            if ( tag != null )
            {
                tag.getListener ().messageReply ( message );
            }
            else
            {
                _listener.messageReceived ( connection, message );
            }
        }
        catch ( Exception e )
        {}
    }

    private void processTimeOuts ()
    {
        List<MessageTag> removeBag = new ArrayList<MessageTag> ();
        
        synchronized ( _tagList )
        {
            for ( Iterator < Map.Entry < Long, MessageTag > > i = _tagList.entrySet ().iterator (); i.hasNext () ;  )
            {
                MessageTag tag = i.next().getValue();

                if ( ( System.currentTimeMillis () - tag.getTimestamp() ) >= _timeoutLimit )
                {
                    removeBag.add ( tag );
                    i.remove ();
                }
            }
        }
        
        // now send out time outs
        for ( MessageTag tag : removeBag )
        {
            try
            {
                tag.getListener ().messageTimedOut ();
            }
            catch ( Exception e )
            {
            }
        }
    }

    private long nextSequence ()
    {
        long seq = _sequence++;
        if ( _sequence >= MAX_SEQUENCE )
            _sequence = INIT_SEQUENCE;
        return seq;
    }
}
