/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.server.common.impl.stats;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.impl.HiveCommon;

public class HiveCommonStatisticsGenerator extends HiveStatisticsGenerator
{
    private HiveCommon _hive;

    private final String _itemPrefix;

    private FolderCommon _folder;

    private DataItemCounterOutput _attributeWritesOutput;

    private DataItemCounterOutput _valuesWritesOutput;

    private DataItemCounterOutput _itemsOutput;

    private DataItemCounterOutput _sessionsOutput;

    private Timer _timer;

    private DataItemCounterOutput _attributeEventsOutput;

    private DataItemCounterOutput _valueEventsOutput;

    public HiveCommonStatisticsGenerator ( final String itemPrefix )
    {
        this._itemPrefix = itemPrefix;
        this._attributeWritesCounter.setOutput ( this._attributeWritesOutput = new DataItemCounterOutput ( getId ( "attributeWrites" ) ) );
        this._valueWritesCounter.setOutput ( this._valuesWritesOutput = new DataItemCounterOutput ( getId ( "valueWrites" ) ) );
        this._itemsValue.setOutput ( this._itemsOutput = new DataItemCounterOutput ( getId ( "items" ) ) );
        this._sessionsValue.setOutput ( this._sessionsOutput = new DataItemCounterOutput ( getId ( "sessions" ) ) );
        this._attributeEventsCounter.setOutput ( this._attributeEventsOutput = new DataItemCounterOutput ( getId ( "attributeEvents" ) ) );
        this._valueEventsCounter.setOutput ( this._valueEventsOutput = new DataItemCounterOutput ( getId ( "valueEvents" ) ) );
    }

    /**
     * register with the hive
     */
    public void register ( final HiveCommon hive, final FolderCommon folder )
    {
        unregister ();

        this._hive = hive;
        this._folder = folder;

        registerOutput ( "sessions", this._sessionsOutput, "Number of connected sessions" );
        registerOutput ( "items", this._itemsOutput, "Number of registered items" );
        registerOutput ( "attributeWrites", this._attributeWritesOutput, "Number of attribute write operations" );
        registerOutput ( "valueWrites", this._valuesWritesOutput, "Number of value write operations" );
        registerOutput ( "valueEvents", this._valueEventsOutput, "Number of value events" );
        registerOutput ( "attributeEvents", this._attributeEventsOutput, "Number of attribute events" );

        this._timer = new Timer ( "HiveStatsTimer", true );
        this._timer.scheduleAtFixedRate ( new TimerTask () {

            @Override
            public void run ()
            {
                HiveCommonStatisticsGenerator.this.tick ();
            }
        }, new Date (), 1000 );
    }

    protected void registerOutput ( final String name, final CounterOutput output, final String description )
    {
        /*
        FolderCommon localFolder = new FolderCommon ();
        _folder.add ( name, localFolder, new MapBuilder<String,Variant> ().getMap () );
        */

        output.register ( this._hive, this._folder, description );
    }

    protected void unregisterOutput ( final String name, final CounterOutput output )
    {
        output.unregister ( this._hive, this._folder );
    }

    /**
     * unregister with the hive
     */
    public void unregister ()
    {
        if ( this._hive != null )
        {
            unregisterOutput ( "sessions", this._sessionsOutput );
            unregisterOutput ( "items", this._itemsOutput );
            unregisterOutput ( "attributeWrites", this._attributeWritesOutput );
            unregisterOutput ( "valueWrites", this._valuesWritesOutput );
            unregisterOutput ( "attributeEvents", this._attributeEventsOutput );
            unregisterOutput ( "valueEvents", this._valueEventsOutput );
            this._hive = null;
        }
        if ( this._folder != null )
        {
            this._folder = null;
        }
        if ( this._timer != null )
        {
            this._timer.cancel ();
            this._timer = null;
        }
    }

    protected String getId ( final String itemId )
    {
        return this._itemPrefix + "." + itemId;
    }

}
