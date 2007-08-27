/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.net.io.net;

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
import org.openscada.net.io.ConnectionListener;
import org.openscada.net.io.ConnectionStateListener;
import org.openscada.net.io.SocketConnection;
import org.openscada.utils.timing.Scheduler;

public class Connection implements ConnectionListener, MessageListener
{
    private static Logger _log = Logger.getLogger ( Connection.class );

    private static final long MAX_SEQUENCE = 0x7FFFFFFF;
    private static final long INIT_SEQUENCE = 1;

    private int _timeoutLimit = Integer.getInteger ( "org.openscada.net.message_timeout", 10 * 1000 );
    private static Scheduler _scheduler = new Scheduler ();

    private Protocol _protocolGMPP = null;
    protected SocketConnection _connection = null;

    private ConnectionStateListener _connectionStateListener = null;

    private long _sequence = INIT_SEQUENCE;

    private MessageListener _listener = null;

    private Scheduler.Job _timeoutJob = null;

    private boolean _connected = false;

    private class MessageTag
    {
        private MessageStateListener _listener;
        private long _timestamp = 0;
        private long _timeout = 0;
        private boolean _canceled = false;

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

        public long getTimeout ()
        {
            return _timeout;
        }

        public void setTimeout ( long timeout )
        {
            _timeout = timeout;
        }

        synchronized public boolean isTimedOut ()
        {
            if ( _timeout <= 0 )
                return _canceled;

            if ( _canceled )
                return true;

            return ( System.currentTimeMillis () - _timestamp ) >= _timeout;
        }

        synchronized public void cancel ()
        {
            if ( _canceled )
                return;

            _canceled = true;
        }
    }

    private Map<Long, MessageTag> _tagList = new HashMap<Long, MessageTag> ();

    public Connection ( MessageListener listener, ConnectionStateListener connectionStateListener, SocketConnection connection )
    {
        _connectionStateListener = connectionStateListener;
        _listener = listener;
        _connection = connection;

        _protocolGMPP = new ProtocolGMPP ( this, this );

        _connected = true;

        addTimeOutJob ();
    }

    public Connection ( MessageListener listener, SocketConnection connection )
    {
        _listener = listener;
        _protocolGMPP = new ProtocolGMPP ( this, this );
        _connection = connection;

        _connected = true;

        addTimeOutJob ();
    }

    private void addTimeOutJob ()
    {
        if ( _timeoutJob != null )
            return;

        final WeakReference<Connection> _this = new WeakReference<Connection> ( this );

        _timeoutJob = _scheduler.addJob ( new Runnable () {

            public void run ()
            {
                Connection c = _this.get ();
                if ( c == null )
                    _scheduler.removeJob ( this );
                else
                    c.processTimeOuts ();
            }
        }, 1000 );
    }

    private void removeTimeOutJob ()
    {
        if ( _timeoutJob != null )
        {
            _scheduler.removeJob ( _timeoutJob );
            _timeoutJob = null;
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
        if ( !_connected )
            return;

        synchronized ( _connection )
        {
            message.setSequence ( nextSequence () );

            ByteBuffer buffer = encode ( message );

            if ( buffer != null )
                _connection.scheduleWrite ( buffer );
        }
    }

    public MessageTag sendMessage ( Message message, MessageStateListener listener, long timeout )
    {
        MessageTag tag = new MessageTag ();

        tag.setListener ( listener );
        tag.setTimestamp ( System.currentTimeMillis () );
        tag.setTimeout ( timeout );

        if ( !_connected )
        {
            listener.messageTimedOut ();
            return tag;
        }

        synchronized ( _connection )
        {
            message.setSequence ( nextSequence () );

            ByteBuffer buffer = encode ( message );
            if ( buffer == null )
                return tag;

            synchronized ( _tagList )
            {
                _tagList.put ( message.getSequence (), tag );
            }

            _connection.scheduleWrite ( buffer );
        }
        return tag;
    }

    public MessageTag sendMessage ( Message message, MessageStateListener listener )
    {
        return sendMessage ( message, listener, 0 );
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
        _connected = true;

        cleanTagList ();

        _connection.triggerRead ();

        if ( _connectionStateListener != null )
            _connectionStateListener.opened ();
    }

    public void connectionFailed ( IOException e )
    {
        _connected = false;

        if ( _connectionStateListener != null )
            _connectionStateListener.closed ( e );
    }

    public void closed ()
    {
        _connected = false;

        removeTimeOutJob ();

        cleanTagList ();

        if ( _connectionStateListener != null )
            _connectionStateListener.closed ( null );
    }

    public void close ()
    {
        _connected = false;
        _connection.close ();
    }

    private void cleanTagList ()
    {
        synchronized ( _tagList )
        {
            for ( Map.Entry<Long, MessageTag> tag : _tagList.entrySet () )
            {
                tag.getValue ().getListener ().messageTimedOut ();
            }
            _tagList.clear ();
        }
    }

    public void messageReceived ( Connection connection, Message message )
    {
        _log.debug ( "Message received: Seq: " + message.getSequence () + " Reply: " + message.getReplySequence () );

        Long seq = Long.valueOf ( message.getReplySequence () );

        MessageTag tag = null;
        synchronized ( _tagList )
        {
            if ( _tagList.containsKey ( seq ) )
            {
                tag = _tagList.get ( seq );
                if ( !tag.isTimedOut () )
                    _tagList.remove ( seq );
                else
                    tag = null;
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
        {
            _log.warn ( "Custom message failed", e );
        }
    }

    private void processTimeOuts ()
    {
        List<MessageTag> removeBag = new ArrayList<MessageTag> ();

        synchronized ( _tagList )
        {
            for ( Iterator<Map.Entry<Long, MessageTag>> i = _tagList.entrySet ().iterator (); i.hasNext (); )
            {
                MessageTag tag = i.next ().getValue ();

                if ( tag.isTimedOut () )
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
                _log.info ( "Failed to handle messageTimedOut", e );
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
