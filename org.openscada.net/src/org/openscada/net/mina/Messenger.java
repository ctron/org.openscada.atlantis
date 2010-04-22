/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.net.mina;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.utils.MessageCreator;

public class Messenger implements MessageListener
{
    private static Logger logger = Logger.getLogger ( Messenger.class );

    private TimerTask timeoutJob = null;

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

        @SuppressWarnings ( "unused" )
        public long getTimestamp ()
        {
            return this.timestamp;
        }

        public void setTimestamp ( final long timestamp )
        {
            this.timestamp = timestamp;
        }

        @SuppressWarnings ( "unused" )
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

        public boolean isCanceled ()
        {
            return this.canceled;
        }
    }

    private final Map<Long, MessageTag> tagList = new HashMap<Long, MessageTag> ();

    private MessageSender connection;

    private Timer timer;

    private final long sessionTimeout;

    private final long timeoutJobPeriod;

    public Messenger ( final long timeout )
    {
        this.sessionTimeout = timeout;
        this.timeoutJobPeriod = 1000;
    }

    public long getSessionTimeout ()
    {
        return this.sessionTimeout;
    }

    @Override
    protected void finalize () throws Throwable
    {
        logger.info ( "Finalized" );
        if ( this.timer != null )
        {
            this.timer.cancel ();
        }
        super.finalize ();
    }

    public void connected ( final MessageSender connection )
    {
        disconnected ();

        Collection<MessageTag> tags = null;

        synchronized ( this )
        {
            if ( connection != null )
            {
                logger.info ( "Messenger connected" );

                this.connection = connection;
                tags = cleanTagList ();

                this.timer = new Timer ( "MessengerTimer/" + connection, true );
                this.timeoutJob = new TimerTask () {

                    @Override
                    public void run ()
                    {
                        Messenger.this.processTimeOuts ();
                    }

                    @Override
                    protected void finalize () throws Throwable
                    {
                        logger.info ( "Finalized timeout job" );
                        super.finalize ();
                    }
                };
                this.timer.scheduleAtFixedRate ( this.timeoutJob, this.sessionTimeout, this.timeoutJobPeriod );
            }
        }

        // now fire events from cleanup but outside the sync lock
        if ( tags != null )
        {
            for ( final MessageTag tag : tags )
            {
                tag.getListener ().messageTimedOut ();
            }
        }
    }

    public void disconnected ()
    {
        Collection<MessageTag> tags = null;

        synchronized ( this )
        {
            if ( this.connection != null )
            {
                this.connection = null;
                logger.info ( "Disconnected" );
                tags = cleanTagList ();
                if ( this.timeoutJob != null )
                {
                    this.timeoutJob.cancel ();
                    this.timeoutJob = null;
                }
                if ( this.timer != null )
                {
                    this.timer.cancel ();
                    this.timer = null;
                }
            }
        }

        // now fire events from cleanup but outside the sync lock
        if ( tags != null )
        {
            for ( final MessageTag tag : tags )
            {
                tag.getListener ().messageTimedOut ();
            }
        }
    }

    private final Map<Integer, MessageListener> listeners = new HashMap<Integer, MessageListener> ();

    private long lastMessge;

    public void setHandler ( final int commandCode, final MessageListener handler )
    {
        this.listeners.put ( Integer.valueOf ( commandCode ), handler );
    }

    public void unsetHandler ( final int commandCode )
    {
        this.listeners.remove ( Integer.valueOf ( commandCode ) );
    }

    private Collection<MessageTag> cleanTagList ()
    {
        final Collection<MessageTag> tags = new LinkedList<MessageTag> ();

        synchronized ( this.tagList )
        {
            for ( final Map.Entry<Long, MessageTag> tag : this.tagList.entrySet () )
            {
                try
                {
                    if ( !tag.getValue ().isCanceled () )
                    {
                        tag.getValue ().cancel ();
                        tags.add ( tag.getValue () );
                    }
                }
                catch ( final Throwable e )
                {
                    logger.warn ( "Failed to handle message timeout", e );
                }
            }
            this.tagList.clear ();
        }
        return tags;
    }

    public void messageReceived ( final Message message )
    {
        this.lastMessge = System.currentTimeMillis ();

        if ( logger.isDebugEnabled () )
        {
            if ( message.getReplySequence () == 0 )
            {
                logger.debug ( String.format ( "Received message: 0x%1$08X Seq: %2$d", message.getCommandCode (), message.getSequence () ) );
            }
            else
            {
                logger.debug ( String.format ( "Received message: 0x%1$08X Seq: %2$d in reply to: %3$d", message.getCommandCode (), message.getSequence (), message.getReplySequence () ) );
            }
        }

        if ( handleTagMessage ( message ) )
        {
            return;
        }
        else if ( handleDefaultMessage ( message ) )
        {
            return;
        }
        else if ( handleHandlerMessage ( message ) )
        {
            return;
        }

        handleUnknownMessage ( message );
    }

    protected void handleUnknownMessage ( final Message message )
    {
        sendMessage ( MessageCreator.createUnknownMessage ( message ) );
    }

    private boolean handleTagMessage ( final Message message )
    {
        final Long seq = Long.valueOf ( message.getReplySequence () );

        MessageTag tag = null;
        synchronized ( this.tagList )
        {
            if ( this.tagList.containsKey ( seq ) )
            {
                tag = this.tagList.get ( seq );
                logger.info ( String.format ( "Found tag for message %s but it is timed out", seq ) );
                // if the tag is timed out then we don't process it here and let processTimeOuts () do the job
                if ( !tag.isTimedOut () )
                {
                    this.tagList.remove ( seq );
                }
                else
                {
                    tag = null;
                    return true;
                }
            }
        }

        try
        {
            if ( tag != null )
            {
                logger.debug ( String.format ( "Processing message listener for message %s", seq ) );
                tag.getListener ().messageReply ( message );
            }
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Custom message failed", e );
        }
        return tag != null;
    }

    private void processTimeOuts ()
    {
        final List<MessageTag> removeBag = new ArrayList<MessageTag> ();

        // check for session timeout
        checkSessionTimeout ();

        // check for message timeouts
        synchronized ( this.tagList )
        {
            for ( final Iterator<Map.Entry<Long, MessageTag>> i = this.tagList.entrySet ().iterator (); i.hasNext (); )
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

    private void checkSessionTimeout ()
    {
        final long now = System.currentTimeMillis ();
        final long timeDiff = now - this.lastMessge;

        if ( this.connection == null )
        {
            logger.warn ( "Called without a connection" );
        }

        if ( timeDiff > this.sessionTimeout )
        {
            synchronized ( this )
            {
                logger.warn ( String.format ( "Closing connection due to receive timeout: %s (timeout: %s)", timeDiff, this.sessionTimeout ) );
                this.connection.close ();
                disconnected ();
            }
        }
    }

    public void sendMessage ( final Message message )
    {
        sendMessage ( message, null );
    }

    public void sendMessage ( final Message message, final MessageStateListener messageListener )
    {
        sendMessage ( message, messageListener, 0L );
    }

    protected void registerMessageTag ( final long sequence, final MessageTag messageTag )
    {
        if ( messageTag.getListener () == null )
        {
            return;
        }

        synchronized ( Messenger.this.tagList )
        {
            Messenger.this.tagList.put ( sequence, messageTag );
        }
    }

    /**
     * Send out a message including optional message tracking
     * @param message the message to send
     * @param listener the optional listener
     * @param timeout the timeout
     */
    public void sendMessage ( final Message message, final MessageStateListener listener, final long timeout )
    {
        boolean isSent = false;

        final MessageSender connection = this.connection;
        if ( connection != null )
        {
            final MessageTag tag = new MessageTag ();

            tag.setListener ( listener );
            tag.setTimestamp ( System.currentTimeMillis () );
            tag.setTimeout ( timeout < 0 ? 0 : timeout );

            isSent = connection.sendMessage ( message, new PrepareSendHandler () {

                public void prepareSend ( final Message message )
                {
                    registerMessageTag ( message.getSequence (), tag );
                }
            } );
        }

        // If the message was not sent, notify that
        if ( !isSent )
        {
            if ( listener != null )
            {
                listener.messageTimedOut ();
            }
        }

    }

    protected boolean handleDefaultMessage ( final Message message )
    {
        switch ( message.getCommandCode () )
        {
        case Message.CC_FAILED:
            String errorInfo = "";
            if ( message.getValues ().containsKey ( Message.FIELD_ERROR_INFO ) )
            {
                errorInfo = message.getValues ().get ( Message.FIELD_ERROR_INFO ).toString ();
            }

            logger.warn ( "Failed message: " + message.getSequence () + "/" + message.getReplySequence () + " Message: " + errorInfo );
            return true;

        case Message.CC_UNKNOWN_COMMAND_CODE:
            logger.warn ( "Reply to unknown message command code from peer: " + message.getSequence () + "/" + message.getReplySequence () );
            return true;

        case Message.CC_ACK:
            // no op
            return true;

        default:
            return false;
        }
    }

    protected boolean handleHandlerMessage ( final Message message )
    {
        final MessageListener listener = this.listeners.get ( message.getCommandCode () );
        if ( listener != null )
        {
            try
            {
                logger.debug ( String.format ( "Let handler %s serve message 0x%08x", listener, message.getCommandCode () ) );
                listener.messageReceived ( message );
            }
            catch ( final Throwable e )
            {
                // reply to other peer if message processing failed
                logger.warn ( "Message processing failed: ", e );
                this.connection.sendMessage ( MessageCreator.createFailedMessage ( message, e ), null );
            }

            return true;
        }
        else
        {
            logger.warn ( String.format ( "Received message which cannot be processed by handler! cc = %x", message.getCommandCode () ) );
            return false;
        }

    }

}
