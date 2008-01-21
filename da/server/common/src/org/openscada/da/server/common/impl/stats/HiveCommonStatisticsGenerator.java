package org.openscada.da.server.common.impl.stats;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.impl.HiveCommon;

public class HiveCommonStatisticsGenerator extends HiveStatisticsGenerator
{
    private HiveCommon _hive;
    private String _itemPrefix;
    private FolderCommon _folder;
    private DataItemCounterOutput _attributeWritesOutput;
    private DataItemCounterOutput _valuesWritesOutput;
    private DataItemCounterOutput _itemsOutput;
    private DataItemCounterOutput _sessionsOutput;
    private Timer _timer;
    private DataItemCounterOutput _attributeEventsOutput;
    private DataItemCounterOutput _valueEventsOutput;

    public HiveCommonStatisticsGenerator ( String itemPrefix )
    {
        _itemPrefix = itemPrefix;
        _attributeWritesCounter.setOutput ( _attributeWritesOutput = new DataItemCounterOutput (
                getId ( "attributeWrites" ) ) );
        _valueWritesCounter.setOutput ( _valuesWritesOutput = new DataItemCounterOutput ( getId ( "valueWrites" ) ) );
        _itemsValue.setOutput ( _itemsOutput = new DataItemCounterOutput ( getId ( "items" ) ) );
        _sessionsValue.setOutput ( _sessionsOutput = new DataItemCounterOutput ( getId ( "sessions" ) ) );
        _attributeEventsCounter.setOutput ( _attributeEventsOutput = new DataItemCounterOutput ( getId ( "attributeEvents") ) );
        _valueEventsCounter.setOutput ( _valueEventsOutput = new DataItemCounterOutput ( getId ( "valueEvents") ) );
    }

    /**
     * register with the hive
     */
    public void register ( HiveCommon hive, FolderCommon folder )
    {
        unregister ();

        _hive = hive;
        _folder = folder;

        registerOutput ( "sessions", _sessionsOutput, "Number of connected sessions" );
        registerOutput ( "items", _itemsOutput, "Number of registered items" );
        registerOutput ( "attributeWrites", _attributeWritesOutput, "Number of attribute write operations" );
        registerOutput ( "valueWrites", _valuesWritesOutput, "Number of value write operations" );
        registerOutput ( "valueEvents", _valueEventsOutput, "Number of value events" );
        registerOutput ( "attributeEvents", _attributeEventsOutput, "Number of attribute events" );

        _timer = new Timer ( "HiveStatsTimer", true );
        _timer.scheduleAtFixedRate ( new TimerTask () {

            @Override
            public void run ()
            {
                HiveCommonStatisticsGenerator.this.tick ();
            }
        }, new Date (), 1000 );
    }

    protected void registerOutput ( String name, CounterOutput output, String description )
    {
        /*
        FolderCommon localFolder = new FolderCommon ();
        _folder.add ( name, localFolder, new MapBuilder<String,Variant> ().getMap () );
        */

        output.register ( _hive, _folder, description );
    }

    protected void unregisterOutput ( String name, CounterOutput output )
    {
        output.unregister ( _hive, _folder );
    }

    /**
     * unregister with the hive
     */
    public void unregister ()
    {
        if ( _hive != null )
        {
            unregisterOutput ( "sessions", _sessionsOutput );
            unregisterOutput ( "items", _itemsOutput );
            unregisterOutput ( "attributeWrites", _attributeWritesOutput );
            unregisterOutput ( "valueWrites", _valuesWritesOutput );
            unregisterOutput ( "attributeEvents", _attributeEventsOutput );
            unregisterOutput ( "valueEvents", _valueEventsOutput );
            _hive = null;
        }
        if ( _folder != null )
        {
            _folder = null;
        }
        if ( _timer != null )
        {
            _timer.cancel ();
            _timer = null;
        }
    }

    protected String getId ( String itemId )
    {
        return _itemPrefix + "." + itemId;
    }

}
