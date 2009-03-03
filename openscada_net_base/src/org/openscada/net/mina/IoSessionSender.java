package org.openscada.net.mina;

import org.apache.mina.core.session.IoSession;
import org.openscada.net.base.data.Message;

public class IoSessionSender implements MessageSender
{
    private static final long MAX_SEQUENCE = 0x7FFFFFFF;

    private static final long INIT_SEQUENCE = 1;

    private long sequence = INIT_SEQUENCE;

    private final IoSession session;

    public IoSessionSender ( final IoSession session )
    {
        this.session = session;
    }

    public boolean sendMessage ( final Message message, final PrepareSendHandler handler )
    {
        synchronized ( this )
        {
            message.setSequence ( nextSequence () );

            // if we have a prepare send handler .. notify
            if ( handler != null )
            {
                handler.prepareSend ( message );
            }

            this.session.write ( message );
        }

        return true;
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

    public void close ()
    {
        this.session.close ( true );
    }

    @Override
    public String toString ()
    {
        return this.session.toString ();
    }

}
