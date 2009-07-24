package org.openscada.da.server.simulation.scriptomatic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.script.ScriptEngineManager;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.browser.common.query.GroupFolder;
import org.openscada.da.server.browser.common.query.IDNameProvider;
import org.openscada.da.server.browser.common.query.InvisibleStorage;
import org.openscada.da.server.browser.common.query.ItemDescriptor;
import org.openscada.da.server.browser.common.query.SplitGroupProvider;
import org.openscada.da.server.common.chain.item.ChainCreator;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.simulation.scriptomatic.configuration.ConfigurationException;
import org.openscada.da.server.simulation.scriptomatic.configuration.Configurator;
import org.openscada.da.server.simulation.scriptomatic.configuration.PropertyConfigurator;
import org.openscada.utils.collection.MapBuilder;

public class Hive extends HiveCommon
{
    private final static Logger logger = Logger.getLogger ( Hive.class );

    private final Map<String, ScriptomaticItem> items = new HashMap<String, ScriptomaticItem> ();

    private final Map<String, ScriptomaticContext> contexts = new HashMap<String, ScriptomaticContext> ();

    private boolean started;

    private final ScriptEngineManager scriptEngineManager;

    private ScheduledExecutorService scheduler;

    private final InvisibleStorage storage = new InvisibleStorage ();

    private final FolderCommon rootFolder;

    private GroupFolder groupFolder;

    public Hive () throws ConfigurationException
    {
        this ( new PropertyConfigurator () );
    }

    public Hive ( final Configurator configurator ) throws ConfigurationException
    {
        this.scriptEngineManager = new ScriptEngineManager ();
        this.rootFolder = new FolderCommon ();
        setRootFolder ( this.rootFolder );
        configurator.configure ( this );

        this.rootFolder.add ( "items", this.groupFolder = new GroupFolder ( new SplitGroupProvider ( new IDNameProvider (), "\\.", 0, 0 ), new IDNameProvider () ), new HashMap<String, Variant> () );
        this.storage.addChild ( this.groupFolder );
    }

    @Override
    public void start () throws Exception
    {
        super.start ();

        this.scheduler = Executors.newScheduledThreadPool ( 1 );

        synchronized ( this )
        {
            this.started = true;
            startAllItems ();
        }
    }

    private void startAllItems ()
    {
        for ( final ScriptomaticItem item : this.items.values () )
        {
            try
            {
                item.start ( this.scheduler );
            }
            catch ( final InterruptedException e )
            {
                logger.warn ( "Failed to start item", e );
                Thread.currentThread ().interrupt ();
            }
            catch ( final Throwable e )
            {
                logger.warn ( "Failed to start item", e );
            }
        }
    }

    private void stopAllItems ()
    {
        for ( final ScriptomaticItem item : this.items.values () )
        {
            try
            {
                item.stop ();
            }
            catch ( final InterruptedException e )
            {
                logger.warn ( "Failed to stop item", e );
                Thread.currentThread ().interrupt ();
            }
            catch ( final Throwable e )
            {
                logger.warn ( "Failed to stop item", e );
            }
        }
    }

    @Override
    public void stop () throws Exception
    {
        synchronized ( this )
        {
            this.started = false;
            stopAllItems ();
        }

        this.scheduler.shutdown ();

        super.stop ();
    }

    public void addItemDefinition ( final ItemDefinition itemDefintion )
    {
        final String itemId = itemDefintion.getId ();

        final ScriptomaticItem item = createItem ( itemDefintion );

        synchronized ( this )
        {
            if ( this.items.containsKey ( itemId ) )
            {
                return;
            }
            this.items.put ( itemId, item );
            registerItem ( item );
            this.storage.added ( new ItemDescriptor ( item, new MapBuilder<String, Variant> ().getMap () ) );

            if ( this.started )
            {
                try
                {
                    item.start ( this.scheduler );
                }
                catch ( final InterruptedException e )
                {
                    logger.warn ( "Failed to start item", e );
                    Thread.currentThread ().interrupt ();
                }
                catch ( final Throwable e )
                {
                    logger.warn ( "Failed to start item", e );
                }
            }
        }
    }

    /**
     * Create the item but do not register or start it
     * @param itemDefintion the item definition of the item to create
     * @return the new item
     */
    private ScriptomaticItem createItem ( final ItemDefinition itemDefintion )
    {
        final String contextId = itemDefintion.getContextId ();
        final ScriptomaticContext context = getContext ( contextId );
        ScriptomaticItem item;
        try
        {
            item = new ScriptomaticItem ( itemDefintion, context, getOperationService () );
            if ( itemDefintion.isDefaultChain () )
            {
                ChainCreator.applyDefaultInputChain ( item, this );
            }
            return item;

        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to create item " + itemDefintion.getId (), e );
        }
        return null;
    }

    private ScriptomaticContext getContext ( final String contextId )
    {
        ScriptomaticContext context = this.contexts.get ( contextId );
        if ( context == null )
        {
            context = createContext ( contextId );
            this.contexts.put ( contextId, context );
        }
        return context;
    }

    private ScriptomaticContext createContext ( final String contextId )
    {
        return new ScriptomaticContext ( this, this.scriptEngineManager.getEngineByName ( contextId ) );
    }

    public synchronized void removeItem ( final String itemId )
    {
        final ScriptomaticItem item = this.items.remove ( itemId );
        if ( item != null )
        {
            unregisterItem ( item );
            this.storage.removed ( new ItemDescriptor ( item, null ) );
        }

        if ( this.started )
        {
            try
            {
                item.stop ();
            }
            catch ( final InterruptedException e )
            {
                logger.warn ( "Failed to stop item", e );
                Thread.currentThread ().interrupt ();
            }
            catch ( final Throwable e )
            {
                logger.warn ( "Failed to stop item", e );
            }
        }
    }

    public ScheduledExecutorService getScheduler ()
    {
        return this.scheduler;
    }

    public synchronized ScriptomaticItem getItem ( final String itemId )
    {
        return this.items.get ( itemId );
    }
}
