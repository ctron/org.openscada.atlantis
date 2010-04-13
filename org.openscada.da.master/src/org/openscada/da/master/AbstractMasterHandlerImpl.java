package org.openscada.da.master;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.utils.osgi.pool.ObjectPoolListener;
import org.openscada.utils.osgi.pool.ObjectPoolServiceTracker;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMasterHandlerImpl implements MasterItemHandler
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractMasterHandlerImpl.class );

    private final Set<MasterItem> items = new CopyOnWriteArraySet<MasterItem> ();

    private final int defaultPriority;

    private final ObjectPoolTracker poolTracker;

    private volatile int priority;

    private final Collection<ObjectPoolServiceTracker> trackers = new LinkedList<ObjectPoolServiceTracker> ();

    protected Map<String, Variant> eventAttributes;

    public AbstractMasterHandlerImpl ( final ObjectPoolTracker poolTracker, final int defaultPriority )
    {
        this.poolTracker = poolTracker;
        this.defaultPriority = defaultPriority;
        this.priority = defaultPriority;

        this.eventAttributes = Collections.emptyMap ();
    }

    public synchronized void dispose ()
    {
        closeTrackers ();
    }

    private void closeTrackers ()
    {
        for ( final ObjectPoolServiceTracker tracker : this.trackers )
        {
            tracker.close ();
        }
        this.trackers.clear ();
    }

    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        closeTrackers ();

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        this.priority = cfg.getInteger ( "handlerPriority", this.defaultPriority );
        final String masterId = cfg.getStringChecked ( MasterItem.MASTER_ID, String.format ( "'%s' must be set", MasterItem.MASTER_ID ) );
        final String splitPattern = cfg.getString ( "splitPattern", ", ?" );

        this.eventAttributes = convert ( cfg.getPrefixed ( "info." ) );

        createTrackers ( masterId.split ( splitPattern ) );

        for ( final ObjectPoolServiceTracker tracker : this.trackers )
        {
            tracker.open ();
        }
    }

    private Map<String, Variant> convert ( final Map<String, String> attributes )
    {
        final Map<String, Variant> result = new HashMap<String, Variant> ( attributes.size () );

        for ( final Map.Entry<String, String> entry : attributes.entrySet () )
        {
            result.put ( entry.getKey (), Variant.valueOf ( entry.getValue () ) );
        }

        return result;
    }

    protected void createTrackers ( final String[] masterIds )
    {
        for ( final String masterId : masterIds )
        {
            this.trackers.add ( new ObjectPoolServiceTracker ( this.poolTracker, masterId, new ObjectPoolListener () {

                public void serviceAdded ( final Object service, final Dictionary<?, ?> properties )
                {
                    addItem ( (MasterItem)service );
                }

                public void serviceModified ( final Object service, final Dictionary<?, ?> properties )
                {
                    // TODO Auto-generated method stub

                }

                public void serviceRemoved ( final Object service, final Dictionary<?, ?> properties )
                {
                    removeItem ( (MasterItem)service );
                }
            } ) );
        }
    }

    protected boolean removeItem ( final MasterItem item )
    {
        logger.debug ( "Removing master: {}", item );
        if ( this.items.remove ( item ) )
        {
            logger.debug ( "Removed master: {}", item );
            item.removeHandler ( this );
            return true;
        }
        else
        {
            return false;
        }
    }

    protected void addItem ( final MasterItem item )
    {
        logger.debug ( "Adding master: {}", item );
        if ( this.items.add ( item ) )
        {
            logger.debug ( "Added master: {} / {}", new Object[] { item, this.priority } );
            item.addHandler ( this, this.priority );
        }
    }

    protected Collection<MasterItem> getMasterItems ()
    {
        return new ArrayList<MasterItem> ( this.items );
    }

    protected void reprocess ()
    {
        for ( final MasterItem item : this.items )
        {
            item.reprocess ();
        }
    }

    public abstract DataItemValue dataUpdate ( Map<String, Object> context, final DataItemValue value );

    /**
     * Process the write request
     * <p>
     * This implementation does <em>nothing</em> and can be overridden by
     * derived implementations.
     * </p>  
     */
    public WriteRequestResult processWrite ( final WriteRequest request )
    {
        return null;
    }
}
