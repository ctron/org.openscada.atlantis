package org.openscada.core.server.net;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.multiton.SingleSessionIoHandler;
import org.openscada.core.ConnectionInformation;
import org.openscada.net.base.PingService;
import org.openscada.net.base.data.Message;
import org.openscada.net.mina.IoSessionSender;
import org.openscada.net.mina.Messenger;

public abstract class AbstractServerConnectionHandler implements SingleSessionIoHandler
{

    private static final int DEFAULT_TIMEOUT = 10000;

    private static Logger logger = Logger.getLogger ( AbstractServerConnectionHandler.class );

    protected IoSession ioSession;

    protected final Messenger messenger;

    protected final PingService pingService;

    protected final ConnectionInformation connectionInformation;

    public AbstractServerConnectionHandler ( final IoSession ioSession, final ConnectionInformation connectionInformation )
    {
        super ();
        this.ioSession = ioSession;
        this.connectionInformation = connectionInformation;

        this.messenger = new Messenger ( getMessageTimeout () );

        this.pingService = new PingService ( this.messenger );

        setupSession ();

        this.messenger.connected ( new IoSessionSender ( this.ioSession ) );
    }

    private void setupSession ()
    {
        this.ioSession.getConfig ().setBothIdleTime ( getPingPeriod () / 1000 );
    }

    public void exceptionCaught ( final Throwable cause ) throws Exception
    {
        logger.warn ( "Something failed", cause );
    }

    public void messageReceived ( final Object message ) throws Exception
    {
        if ( message instanceof Message )
        {
            this.messenger.messageReceived ( (Message)message );
        }
    }

    public void messageSent ( final Object message ) throws Exception
    {
    }

    public void sessionClosed () throws Exception
    {
        cleanUp ();
    }

    protected void cleanUp ()
    {
        if ( this.ioSession != null )
        {
            this.ioSession.close ( true );
            this.ioSession = null;
        }
    }

    public void sessionCreated () throws Exception
    {
    }

    public void sessionIdle ( final IdleStatus status ) throws Exception
    {
        this.pingService.sendPing ();
    }

    public void sessionOpened () throws Exception
    {
    }

    public int getPingPeriod ()
    {
        return getIntProperty ( "pingPeriod", getIntProperty ( "timeout", DEFAULT_TIMEOUT ) / getIntProperty ( "pingFrequency", 3 ) );
    }

    public int getMessageTimeout ()
    {
        return getIntProperty ( "messageTimeout", getIntProperty ( "timeout", DEFAULT_TIMEOUT ) );
    }

    protected int getIntProperty ( final String propertyName, final int defaultValue )
    {
        try
        {
            final String timeout = this.connectionInformation.getProperties ().get ( propertyName );
            final int i = Integer.parseInt ( timeout );
            if ( i <= 0 )
            {
                return defaultValue;
            }
            return i;
        }
        catch ( final Throwable e )
        {
            return defaultValue;
        }
    }
}