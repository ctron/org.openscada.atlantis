package org.openscada.ae.net;

import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.mina.Messenger;
import org.openscada.utils.exec.Result;

public abstract class ClientImpl implements Connection
{
    private final Messenger messenger;

    public ClientImpl ( final Messenger messenger )
    {
        this.messenger = messenger;
    }

    public static abstract class ListQueriesResult extends Result<ListQueryResult>
    {
    }

    protected static class ListQueriesResultImpl extends ListQueriesResult
    {
        protected synchronized void handleError ( final Throwable error )
        {
            this.signalError ( error );
        }

        protected synchronized void handleSuccess ( final ListQueryResult result )
        {
            this.signalResult ( result );
        }
    }

    public ListQueriesResult startListQueries ( final long timeout )
    {
        final ListQueriesResultImpl result = new ListQueriesResultImpl ();

        final Message message = null;

        this.messenger.sendMessage ( message, new MessageStateListener () {

            public void messageReply ( final Message message )
            {
                result.handleSuccess ( ListQueriesResponse.fromMessage ( message ).getValue () );
            }

            public void messageTimedOut ()
            {
                result.handleError ( null );
            }
        }, timeout );

        return result;
    }

    public ListQueryResult listQueries ()
    {
        return listQueries ( 0 );
    }

    public ListQueryResult listQueries ( final long timeout )
    {
        final ListQueriesResult result = startListQueries ( timeout );
        try
        {
            return result.waitForResult ( timeout );
        }
        catch ( final InterruptedException e )
        {
            return null;
        }
    }

    public static class SubscribeResult extends VoidResult
    {
    }

    protected static class SubscribeResultImpl extends SubscribeResult
    {
        protected synchronized void handleError ( final Throwable error )
        {
            this.signalError ( error );
        }

        protected synchronized void handleSuccess ()
        {
            this.signalResult ( null );
        }
    }

    public void subscribe ( final Subscribe request, final long timeout )
    {
        final Message message = Subscribe.toMessage ( request );

        final SubscribeResultImpl result = new SubscribeResultImpl ();

        this.messenger.sendMessage ( message, new MessageStateListener () {

            public void messageReply ( final Message message )
            {
                switch ( message.getCommandCode () )
                {
                case Message.CC_ACK:
                    result.handleSuccess ();
                    break;
                default:
                    result.handleError ( new Exception ( String.format ( "Invalid reply to message: ", message.getCommandCode () ) ).fillInStackTrace () );
                    break;
                }
            }

            public void messageTimedOut ()
            {
                result.handleError ( null );
            }
        }, timeout );
    }

}
