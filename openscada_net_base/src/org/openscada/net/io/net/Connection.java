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
    private static Logger logger = Logger.getLogger ( Connection.class );

    private static final long MAX_SEQUENCE = 0x7FFFFFFF;

    private static final long INIT_SEQUENCE = 1;

    private static final int DEFAULT_CONNECTION_TIMEOUT = Integer.getInteger ( "openscada.net.connection.timeout", 10 * 1000 );

    private static Scheduler _scheduler = new Scheduler ( "GlobalConnectionScheduler" );

    private Protocol protocolGMPP = null;

    protected SocketConnection _connection = null;

    private ConnectionStateListener connectionStateListener = null;

    private long sequence = INIT_SEQUENCE;

    private MessageListener messageListener = null;

    private Scheduler.Job timeoutJob = null;

    private boolean connected = false;

    private static class MessageTag
    {
        private MessageStateListener listener;

        private long timestamp = 0;

        private long timeout = 0;

        private boolean canceled = false;

        public MessageStateListener getListener ()
        {
            return this.listener;
        }

        public void setListener ( final MessageStateListener listener )
        {
            this.listener = listener;
        }

        public long getTimestamp ()
        {
            return this.timestamp;
        }

        public void setTimestamp ( final long timestamp )
        {
            this.timestamp = timestamp;
        }

        public long getTimeout ()
        {
            return this.timeout;
        }

        public void setTimeout ( final long timeout )
        {
            this.timeout = timeout;
        }

        public synchronized boolean isTimedOut ()
        {
            if ( this.timeout <= 0 )
            {
                return this.canceled;
            }

            if ( this.canceled )
            {
                return true;
            }

            return System.currentTimeMillis () - this.timestamp >= this.timeout;
        }

        public synchronized void cancel ()
        {
            this.canceled = true;
        }
    }

    private final Map<Long, MessageTag> _tagList = new HashMap<Long, MessageTag> ();

    public Connection ( final MessageListener listener, final ConnectionStateListener connectionStateListener, final SocketConnection connection )
    {
        this.connectionStateListener = connectionStateListener;
        this.messageListener = listener;
        this._connection = connection;

        this.protocolGMPP = new ProtocolGMPP ( this, this );

        this.connected = true;

        addTimeOutJob ();

        setTimeout ( DEFAULT_CONNECTION_TIMEOUT );
    }

    public Connection ( final MessageListener listener, final SocketConnection connection )
    {
        this.messageListener = listener;
        this.protocolGMPP = new ProtocolGMPP ( this, this );
        this._connection = connection;

        this.connected = true;

        addTimeOutJob ();

        setTimeout ( DEFAULT_CONNECTION_TIMEOUT );
    }

    private void addTimeOutJob ()
    {
        if ( this.timeoutJob != null )
        {
            return;
        }

        final WeakReference<Connection> _this = new WeakReference<Connection> ( this );

        this.timeoutJob = _scheduler.addJob ( new Runnable () {

            public void run ()
            {
                final Connection c = _this.get ();
                if ( c == null )
                {
                    _scheduler.removeJob ( this );
                }
                else
                {
                    c.processTimeOuts ();
                }
            }
        }, 1000 );
    }

    private void removeTimeOutJob ()
    {
        if ( this.timeoutJob != null )
        {
            _scheduler.removeJob ( this.timeoutJob );
            this.timeoutJob = null;
        }
    }

    private ByteBuffer encode ( final Message message )
    {
        try
        {
            return this.protocolGMPP.code ( message );
        }
        catch ( final InvalidValueTypeException e )
        {
            logger.warn ( "Message contained unsupported value type", e );
            return null;
        }
    }

    public void sendMessage ( final Message message )
    {
        if ( !this.connected )
        {
            return;
        }

        synchronized ( this._connection )
        {
            message.setSequence ( nextSequence () );

            final ByteBuffer buffer = encode ( message );

            if ( buffer != null )
            {
                this._connection.scheduleWrite ( buffer );
            }
        }
    }

    public MessageTag sendMessage ( final Message message, final MessageStateListener listener, final long timeout )
    {
        final MessageTag tag = new MessageTag ();

        tag.setListener ( listener );
        tag.setTimestamp ( System.currentTimeMillis () );
        tag.setTimeout ( timeout );

        if ( !this.connected )
        {
            listener.messageTimedOut ();
            return tag;
        }

        synchronized ( this._connection )
        {
            message.setSequence ( nextSequence () );

            final ByteBuffer buffer = encode ( message );
            if ( buffer == null )
            {
                return tag;
            }

            synchronized ( this._tagList )
            {
                this._tagList.put ( message.getSequence (), tag );
            }

            this._connection.scheduleWrite ( buffer );
        }
        return tag;
    }

    public MessageTag sendMessage ( final Message message, final MessageStateListener listener )
    {
        return sendMessage ( message, listener, 0 );
    }

    public void read ( final ByteBuffer buffer )
    {
        this.protocolGMPP.decode ( buffer );
    }

    public void written ()
    {
        // no op
    }

    public void connected ()
    {
        this.connected = true;

        cleanTagList ();

        this._connection.triggerRead ();

        if ( this.connectionStateListener != null )
        {
            this.connectionStateListener.opened ();
        }
    }

    public void connectionFailed ( final IOException e )
    {
        logger.debug ( "connection failed" );

        this.connected = false;

        removeTimeOutJob ();

        if ( this.connectionStateListener != null )
        {
            this.connectionStateListener.closed ( e );
        }
    }

    public void closed ()
    {
        logger.debug ( "connection closed" );

        this.connected = false;

        removeTimeOutJob ();

        cleanTagList ();

        if ( this.connectionStateListener != null )
        {
            this.connectionStateListener.closed ( null );
        }
    }

    public void close ()
    {
        this.connected = false;
        this._connection.close ();
    }

    private void cleanTagList ()
    {
        synchronized ( this._tagList )
        {
            for ( final Map.Entry<Long, MessageTag> tag : this._tagList.entrySet () )
            {
                try
                {
                    tag.getValue ().getListener ().messageTimedOut ();
                }
                catch ( final Throwable e )
                {
                    logger.warn ( "Failed to handle message timeout", e );
                }
            }
            this._tagList.clear ();
        }
    }

    public void messageReceived ( final Connection connection, final Message message )
    {
        logger.debug ( "Message received: Seq: " + message.getSequence () + " Reply: " + message.getReplySequence () );

        final Long seq = Long.valueOf ( message.getReplySequence () );

        MessageTag tag = null;
        synchronized ( this._tagList )
        {
            if ( this._tagList.containsKey ( seq ) )
            {
                tag = this._tagList.get ( seq );
                // if the tag is timed out then we don't process it here and let processTimeOuts () do the job
                if ( !tag.isTimedOut () )
                {
                    this._tagList.remove ( seq );
                }
                else
                {
                    tag = null;
                }
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
                this.messageListener.messageReceived ( connection, message );
            }
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Custom message failed", e );
        }
    }

    private void processTimeOuts ()
    {
        final List<MessageTag> removeBag = new ArrayList<MessageTag> ();

        synchronized ( this._tagList )
        {
            for ( final Iterator<Map.Entry<Long, MessageTag>> i = this._tagList.entrySet ().iterator (); i.hasNext (); )
            {
                final MessageTag tag = i.next ().getValue ();

                if ( tag.isTimedOut () )
                {
                    removeBag.add ( tag );
                    i.remove ();
                }
            }
        }

        // now send out time outs
        for ( final MessageTag tag : removeBag )
        {
            try
            {
                tag.getListener ().messageTimedOut ();
            }
            catch ( final Throwable e )
            {
                logger.info ( "Failed to handle messageTimedOut", e );
            }
        }
    }

    private long nextSequence ()
    {
        final long seq = this.sequence++;
        if ( this.sequence >= MAX_SEQUENCE )
        {
            this.sequence = INIT_SEQUENCE;
        }
        return seq;
    }

    /**
     * Set the connection timeout
     * @param timeout
     */
    protected void setTimeout ( final long timeout )
    {
        logger.debug ( String.format ( "Setting timeout to %s", timeout ) );

        this._connection.setTimeout ( timeout );
    }

    public long getTimeout ()
    {
        return this._connection.getTimeout ();
    }
}
