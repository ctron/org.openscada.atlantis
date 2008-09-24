package org.openscada.da.server.opc2.configuration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.opc.configuration.InitialItemType;
import org.openscada.da.opc.configuration.InitialItemsType;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.DataItemCommand.Listener;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.item.factory.FolderItemFactory;

public abstract class AbstractXMLItemSource extends AbstractItemSource
{
    private static Logger logger = Logger.getLogger ( AbstractXMLItemSource.class );

    private boolean active = false;

    private FolderItemFactory parentItemFactory;

    protected FolderItemFactory itemFactory;

    private String baseId;

    private DataItemCommand reloadCommandItem;

    private DataItemInputChained stateItem;

    private Listener reloadListener;

    public AbstractXMLItemSource ( FolderItemFactory parentItemFactory, String baseId )
    {
        super ();
        this.parentItemFactory = parentItemFactory;
        this.baseId = baseId;
    }

    @Override
    public void activate ()
    {
        itemFactory = parentItemFactory.createSubFolderFactory ( baseId );

        this.reloadCommandItem = itemFactory.createCommand ( "reload" );
        this.stateItem = itemFactory.createInput ( "state" );

        this.reloadCommandItem.addListener ( this.reloadListener = new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                AbstractXMLItemSource.this.reload ();
            }
        } );

        setSuccessState ( "IDLE" );

        active = true;
        reload ();
    }

    @Override
    public void deactivate ()
    {
        super.deactivate ();

        active = false;

        this.itemFactory.dispose ();
        this.itemFactory = null;

        this.reloadCommandItem.removeListener ( this.reloadListener );
        this.reloadCommandItem = null;
        this.stateItem = null;
    }

    protected void reload ()
    {
        if ( !active )
        {
            return;
        }

        try
        {
            setSuccessState ( "READ" );
            InitialItemsType initialItems = parse ();
            setSuccessState ( "NOTIFY" );
            handleItems ( initialItems );
            setSuccessState ( "IDLE" );
        }
        catch ( Throwable e )
        {
            handleError ( e );
        }
    }

    protected abstract InitialItemsType parse () throws Exception;

    private void handleItems ( InitialItemsType initialItems )
    {
        Set<ItemDescription> items = new HashSet<ItemDescription> ();

        logger.debug ( "Number of items: " + initialItems.getItemList ().size () );

        for ( InitialItemType item : initialItems.getItemList () )
        {
            logger.debug ( "Found new item: " + item.getId () );

            ItemDescription newItem = new ItemDescription ();
            newItem.setId ( item.getId () );
            newItem.setDescription ( item.getDescription () );
            newItem.setAccessPath ( item.getAccessPath () );

            if ( newItem.getId () != null )
            {
                items.add ( newItem );
            }
        }

        fireAvailableItemsChanged ( items );
    }

    private void handleError ( Throwable e )
    {
        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "error", new Variant ( true ) );
        attributes.put ( "error.message", new Variant ( e.getMessage () ) );

        this.stateItem.updateData ( new Variant ( "ERROR" ), attributes, AttributeMode.UPDATE );
    }

    private void setSuccessState ( String state )
    {
        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "error", null );
        attributes.put ( "error.message", null );

        this.stateItem.updateData ( new Variant ( state ), attributes, AttributeMode.UPDATE );
    }

}
