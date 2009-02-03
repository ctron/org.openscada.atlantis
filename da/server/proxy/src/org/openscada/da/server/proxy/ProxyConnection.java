package org.openscada.da.server.proxy;

import java.util.HashMap;

import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;
import org.openscada.utils.collection.MapBuilder;

public class ProxyConnection
{
    private final Hive hive;

    private final ProxyGroup group;

    private final FolderCommon connectionsFolder;

    private final String separator;

    private WriteHandlerItem activeConnectionItem;

    private DataItemInputChained switchStarted;

    private DataItemInputChained switchEnded;

    private DataItemInputChained switchInProgress;

    private DataItemInputChained switchDuration;

    private FolderCommon connectionFolder;

    private DataItemCommand connectItem;

    private DataItemCommand disconnectItem;

    public ProxyConnection ( final Hive hive, final FolderCommon connectionsFolder, final ProxyGroup group )
    {
        this.hive = hive;
        this.connectionsFolder = connectionsFolder;
        this.group = group;
        this.separator = this.hive.getSeparator ();
    }

    protected DataItemInputChained createItem ( final String localId )
    {
        final DataItemInputChained item = new DataItemInputChained ( ProxyUtils.ITEM_PREFIX + this.separator + this.group.getPrefix ().getName () + this.separator + localId );

        this.hive.registerItem ( item );
        this.connectionFolder.add ( localId, item, new MapBuilder<String, Variant> ().getMap () );

        return item;
    }

    public void init ()
    {
        this.connectionFolder = new FolderCommon ();
        this.group.setConnectionFolder ( this.connectionFolder );
        this.connectionsFolder.add ( this.group.getPrefix ().getName (), this.connectionFolder, new HashMap<String, Variant> () );

        this.switchStarted = createItem ( "switch.started" );
        this.switchEnded = createItem ( "switch.ended" );
        this.switchInProgress = createItem ( "switch.inprogress" );
        this.switchDuration = createItem ( "switch.duration" );

        // active Connection
        this.activeConnectionItem = new WriteHandlerItem ( ProxyUtils.ITEM_PREFIX + this.separator + this.group.getPrefix () + this.separator + "active.connection", new WriteHandler () {
            @Override
            public void handleWrite ( final Variant value ) throws Exception
            {
                final String newId = value.asString ( null );
                final ProxySubConnection newSubConnection = ProxyConnection.this.group.getSubConnections ().get ( new ProxySubConnectionId ( newId ) );
                if ( newSubConnection != null )
                {
                    ProxyConnection.this.switchTo ( newSubConnection.getId () );
                }
            }
        } );
        this.hive.registerItem ( this.activeConnectionItem );

        // fill active connection information
        final HashMap<String, Variant> availableConnections = new HashMap<String, Variant> ();
        for ( final ProxySubConnection subConnection : this.group.getSubConnections ().values () )
        {
            availableConnections.put ( "available.connection." + subConnection.getId (), new Variant ( subConnection.getPrefix ().getName () ) );
        }
        this.connectionFolder.add ( this.activeConnectionItem.getInformation ().getName (), this.activeConnectionItem, availableConnections );

        this.activeConnectionItem.updateData ( new Variant ( this.group.getCurrentConnection ().toString () ), availableConnections, AttributeMode.SET );

        this.connectItem = new DataItemCommand ( "connect" );
        this.connectItem.addListener ( new DataItemCommand.Listener () {

            @Override
            public void command ( final Variant value ) throws Exception
            {
                ProxyConnection.this.group.connectCurrentConnection ();
            }
        } );
        this.hive.registerItem ( this.connectItem );
        this.connectionFolder.add ( "connect", this.connectItem, new MapBuilder<String, Variant> ().getMap () );

        this.disconnectItem = new DataItemCommand ( "disconnect" );
        this.disconnectItem.addListener ( new DataItemCommand.Listener () {

            @Override
            public void command ( final Variant value ) throws Exception
            {
                ProxyConnection.this.group.connectCurrentConnection ();
            }
        } );
        this.hive.registerItem ( this.disconnectItem );
        this.connectionFolder.add ( "disconnect", this.disconnectItem, new MapBuilder<String, Variant> ().getMap () );

        // actual items
        // this.group.getConnectionFolder ().add ( "items", new ProxyFolder ( this.group ), new HashMap<String, Variant> () );
        this.group.addConnectionStateListener ( new NotifyConnectionErrorListener ( this.group ) );

        this.group.start ();
    }

    protected void switchTo ( final ProxySubConnectionId id )
    {
        // mark start of switch
        final long start = System.currentTimeMillis ();
        this.switchStarted.updateData ( new Variant ( start ), null, AttributeMode.UPDATE );
        this.switchInProgress.updateData ( new Variant ( true ), null, AttributeMode.UPDATE );

        // perform switch
        this.group.switchTo ( id );
        this.activeConnectionItem.updateData ( new Variant ( id ), null, null );

        // mark end of switch
        this.switchInProgress.updateData ( new Variant ( false ), null, AttributeMode.UPDATE );
        final long end = System.currentTimeMillis ();
        this.switchEnded.updateData ( new Variant ( end ), null, AttributeMode.UPDATE );
        this.switchDuration.updateData ( new Variant ( end - start ), null, AttributeMode.UPDATE );
    }

    public void dispose ()
    {
        this.group.stop ();
    }

    public ProxyDataItem realizeItem ( final String id )
    {
        return this.group.realizeItem ( id );
    }

}
