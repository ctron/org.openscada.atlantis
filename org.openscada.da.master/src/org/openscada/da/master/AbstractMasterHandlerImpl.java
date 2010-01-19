package org.openscada.da.master;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.openscada.da.client.DataItemValue;
import org.openscada.utils.osgi.pool.ObjectPoolListener;
import org.openscada.utils.osgi.pool.ObjectPoolServiceTracker;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMasterHandlerImpl implements MasterItemHandler
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractMasterHandlerImpl.class );

    private ObjectPoolServiceTracker tracker;

    private final Set<MasterItem> items = new CopyOnWriteArraySet<MasterItem> ();

    private final int priority;

    private String masterId;

    private final ObjectPoolTracker poolTracker;

    public AbstractMasterHandlerImpl ( final ObjectPoolTracker poolTracker, final int priority )
    {
        this.poolTracker = poolTracker;
        this.priority = priority;
    }

    public synchronized void dispose ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
            this.tracker = null;
        }
    }

    protected String getMasterId ()
    {
        return this.masterId;
    }

    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
            this.tracker = null;
        }

        this.masterId = parameters.get ( MasterItem.MASTER_ID );
        if ( this.masterId == null )
        {
            throw new IllegalArgumentException ( String.format ( "'%s' must be set", MasterItem.MASTER_ID ) );
        }

        this.tracker = new ObjectPoolServiceTracker ( this.poolTracker, this.masterId, new ObjectPoolListener () {

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
        } );
        this.tracker.open ();
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
        logger.debug ( "Addeding master: {}", item );
        if ( this.items.add ( item ) )
        {
            logger.debug ( "Added master: {}", item );
            item.addHandler ( this, this.priority );
        }
    }

    protected Collection<MasterItem> getMasterItems ()
    {
        return new ArrayList<MasterItem> ( this.items );
    }

    public abstract DataItemValue dataUpdate ( final DataItemValue value );

    public WriteRequestResult processWrite ( final WriteRequest request )
    {
        return null;
    }
}
