package org.openscada.da.server.dave;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.multiton.SingleSessionIoHandler;
import org.apache.mina.handler.multiton.SingleSessionIoHandlerDelegate;
import org.apache.mina.handler.multiton.SingleSessionIoHandlerFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.openscada.core.Variant;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.protocols.dave.DaveConnectionEstablishedMessage;
import org.openscada.protocols.dave.DaveFilter;
import org.openscada.protocols.dave.DaveGenericMessage;
import org.openscada.protocols.dave.DaveMessage;
import org.openscada.protocols.dave.DaveWriteRequest;
import org.openscada.protocols.iso8073.COTPFilter;
import org.openscada.protocols.tkpt.TPKTFilter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaveDevice implements SingleSessionIoHandler
{

    private final static Logger logger = LoggerFactory.getLogger ( DaveDevice.class );

    private final ScheduledExecutorService executor;

    private NioSocketConnector connector;

    private IoSession session;

    private String host;

    private Short port;

    private Integer rack;

    private Byte slot;

    private final BundleContext context;

    private final String id;

    private final DataItemInputChained stateItem;

    private final ServiceRegistration stateItemHandle;

    private final DataItemInputChained configItem;

    private final ServiceRegistration configItemHandle;

    private final DaveBlockConfigurator configurator;

    private final DaveJobManager jobManager;

    public DaveDevice ( final BundleContext context, final String id, final Map<String, String> properties ) throws Exception
    {
        this.id = id;
        this.context = context;
        this.executor = Executors.newSingleThreadScheduledExecutor ();

        this.configurator = new DaveBlockConfigurator ( this, this.context );
        this.jobManager = new DaveJobManager ( this );

        this.stateItem = new DataItemInputChained ( getItemId ( "state" ), this.executor );
        this.stateItemHandle = context.registerService ( DataItem.class.getName (), this.stateItem, null );

        this.configItem = new DataItemInputChained ( getItemId ( "config" ), this.executor );
        this.configItemHandle = context.registerService ( DataItem.class.getName (), this.configItem, null );

        update ( properties );
    }

    public String getItemId ( final String localId )
    {
        if ( localId != null )
        {
            return "dave." + this.id + "." + localId;
        }
        else
        {
            return "dave." + this.id;
        }
    }

    public void dispose ()
    {
        this.configurator.dispose ();

        this.configItemHandle.unregister ();
        this.stateItemHandle.unregister ();

        this.jobManager.dispose ();

        disconnect ();
    }

    public void update ( final Map<String, String> properties ) throws Exception
    {
        this.host = properties.get ( "host" );
        this.port = Short.valueOf ( properties.get ( "port" ) );
        this.rack = Integer.valueOf ( properties.get ( "rack" ) );
        this.slot = Byte.valueOf ( properties.get ( "slot" ) );

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "host", new Variant ( this.host ) );
        attributes.put ( "port", new Variant ( this.port ) );
        attributes.put ( "rack", new Variant ( this.rack ) );
        attributes.put ( "slot", new Variant ( this.slot ) );
        this.configItem.updateData ( new Variant ( true ), attributes, AttributeMode.SET );

        disconnect ();
        connect ();
    }

    private void connect ()
    {
        if ( this.connector == null )
        {
            this.connector = new NioSocketConnector ();

            this.connector.setHandler ( new SingleSessionIoHandlerDelegate ( new SingleSessionIoHandlerFactory () {

                public SingleSessionIoHandler getHandler ( final IoSession session ) throws Exception
                {
                    return DaveDevice.this;
                }
            } ) );

            // this.connector.getFilterChain ().addLast ( "logger", new LoggingFilter ( this.getClass ().getName () ) );
            this.connector.getFilterChain ().addLast ( "tpkt", new TPKTFilter ( 3 ) );
            this.connector.getFilterChain ().addLast ( "cotp", new COTPFilter ( this.rack, this.slot ) );
            this.connector.getFilterChain ().addLast ( "dave", new DaveFilter () );
        }

        this.stateItem.updateData ( new Variant ( "CONNECTING" ), null, null );

        final ConnectFuture future = this.connector.connect ( new InetSocketAddress ( this.host, this.port ) );
        future.addListener ( new IoFutureListener<IoFuture> () {

            public void operationComplete ( final IoFuture future )
            {
                setSession ( future.getSession () );
            }
        } );
    }

    protected void setSession ( final IoSession session )
    {
        if ( session != null )
        {
            this.stateItem.updateData ( new Variant ( "CONNECTED" ), null, null );
        }
        else
        {
            this.stateItem.updateData ( new Variant ( "DISCONNECTED" ), null, null );
        }
        this.session = session;
    }

    private void disconnect ()
    {
        if ( this.session != null )
        {
            this.session.close ( true );
            this.session = null;
        }

        if ( this.connector != null )
        {
            this.connector.dispose ();
            this.connector = null;
        }
    }

    public void exceptionCaught ( final Throwable error ) throws Exception
    {
        logger.warn ( "Exception caught", error );
        this.session.close ( true );
    }

    public void messageReceived ( final Object message ) throws Exception
    {
        // logger.info ( "Message received: {}", message );
        if ( message instanceof DaveConnectionEstablishedMessage )
        {
            logger.info ( "DAVE Connection established " );
            //startTimer ();
            this.jobManager.setSession ( this.session );
        }
        else if ( message instanceof DaveMessage )
        {
            this.jobManager.messageReceived ( (DaveMessage)message );
        }
        else if ( message instanceof DaveGenericMessage )
        {
            logger.info ( "Message received: {}", message );
        }
    }

    public void messageSent ( final Object message ) throws Exception
    {
    }

    public void sessionClosed () throws Exception
    {
        logger.warn ( "Connection lost" );

        this.jobManager.setSession ( null );

        this.session = null;
        this.executor.schedule ( new Runnable () {

            public void run ()
            {
                connect ();
            }
        }, 1000, TimeUnit.MILLISECONDS );

    }

    public void sessionCreated () throws Exception
    {
    }

    public void sessionIdle ( final IdleStatus status ) throws Exception
    {
    }

    public void sessionOpened () throws Exception
    {
        logger.debug ( "Session opened" );
    }

    /*
    protected void checkRequestData ()
    {
        if ( this.currentRequest == null )
        {
            requestData ();
        }
    }

    private void requestData ()
    {
        final IoSession session = this.session;
        if ( session != null )
        {
            this.currentRequest = getNextBlock ();
            if ( this.currentRequest != null )
            {
                final DaveReadRequest request = new DaveReadRequest ();
                request.addRequest ( this.currentRequest.getRequest () );
                session.write ( request );
            }
        }
    }

    protected DaveRequestBlock getNextBlock ()
    {
        final List<DaveRequestBlock> blocks = new ArrayList<DaveRequestBlock> ( this.blocks.values () );
        Collections.sort ( blocks, new Comparator<DaveRequestBlock> () {

            public int compare ( final DaveRequestBlock o1, final DaveRequestBlock o2 )
            {
                final long l1 = o1.updatePriority ();
                final long l2 = o2.updatePriority ();
                return (int) ( l2 - l1 );
            }
        } );

        if ( !blocks.isEmpty () )
        {
            return blocks.get ( 0 );
        }
        else
        {
            return null;
        }
    }
    */

    public Executor getExecutor ()
    {
        return this.executor;
    }

    public String getId ()
    {
        return this.id;
    }

    public void writeBit ( final DaveRequestBlock block, final int index, final int subIndex, final boolean value )
    {
        logger.info ( "Bit write request - index: {}.{} -> {}", new Object[] { index, subIndex, value } );
        final DaveWriteRequest request = new DaveWriteRequest ();

        request.addRequest ( new DaveWriteRequest.BitRequest ( block.getRequest ().getArea (), block.getRequest ().getBlock (), (short) ( index * 8 + subIndex ), value ) );

        this.jobManager.addWriteRequest ( request );
    }

    public void writeFloat ( final DaveRequestBlock block, final int index, final float value )
    {
        logger.info ( "Float write request - index: {} -> {}", new Object[] { index, value } );
        final DaveWriteRequest request = new DaveWriteRequest ();

        final IoBuffer data = IoBuffer.allocate ( 4 );
        data.putFloat ( value );
        data.flip ();

        request.addRequest ( new DaveWriteRequest.ByteRequest ( block.getRequest ().getArea (), block.getRequest ().getBlock (), (short)index, data ) );

        this.jobManager.addWriteRequest ( request );

    }

    public void addBlock ( final String name, final DaveRequestBlock deviceBlock )
    {
        this.jobManager.addBlock ( name, deviceBlock );
    }

    public void removeBlock ( final String block )
    {
        this.jobManager.removeBlock ( block );
    }
}
