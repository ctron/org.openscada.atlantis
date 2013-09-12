/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.common.impl.stats;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.scada.utils.concurrent.NamedThreadFactory;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.impl.HiveCommon;

public class HiveCommonStatisticsGenerator extends HiveStatisticsGenerator
{
    private HiveCommon hive;

    private final String itemPrefix;

    private FolderCommon folder;

    private DataItemCounterOutput attributeWritesOutput;

    private DataItemCounterOutput valuesWritesOutput;

    private DataItemCounterOutput itemsOutput;

    private DataItemCounterOutput sessionsOutput;

    private ScheduledExecutorService timer;

    private DataItemCounterOutput attributeEventsOutput;

    private DataItemCounterOutput valueEventsOutput;

    public HiveCommonStatisticsGenerator ( final String itemPrefix )
    {
        this.itemPrefix = itemPrefix;
        this.attributeWritesCounter.setOutput ( this.attributeWritesOutput = new DataItemCounterOutput ( getId ( "attributeWrites" ) ) );
        this.valueWritesCounter.setOutput ( this.valuesWritesOutput = new DataItemCounterOutput ( getId ( "valueWrites" ) ) );
        this.itemsValue.setOutput ( this.itemsOutput = new DataItemCounterOutput ( getId ( "items" ) ) );
        this.sessionsValue.setOutput ( this.sessionsOutput = new DataItemCounterOutput ( getId ( "sessions" ) ) );
        this.attributeEventsCounter.setOutput ( this.attributeEventsOutput = new DataItemCounterOutput ( getId ( "attributeEvents" ) ) );
        this.valueEventsCounter.setOutput ( this.valueEventsOutput = new DataItemCounterOutput ( getId ( "valueEvents" ) ) );
    }

    /**
     * register with the hive
     */
    public void register ( final HiveCommon hive, final FolderCommon folder )
    {
        unregister ();

        this.hive = hive;
        this.folder = folder;

        registerOutput ( "sessions", this.sessionsOutput, "Number of connected sessions" );
        registerOutput ( "items", this.itemsOutput, "Number of registered items" );
        registerOutput ( "attributeWrites", this.attributeWritesOutput, "Number of attribute write operations" );
        registerOutput ( "valueWrites", this.valuesWritesOutput, "Number of value write operations" );
        registerOutput ( "valueEvents", this.valueEventsOutput, "Number of value events" );
        registerOutput ( "attributeEvents", this.attributeEventsOutput, "Number of attribute events" );

        this.timer = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( "HiveStatsTimer" ) );
        this.timer.scheduleAtFixedRate ( new Runnable () {

            @Override
            public void run ()
            {
                HiveCommonStatisticsGenerator.this.tick ();
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS );
    }

    protected void registerOutput ( final String name, final CounterOutput output, final String description )
    {
        output.register ( this.hive, this.folder, description );
    }

    protected void unregisterOutput ( final String name, final CounterOutput output )
    {
        output.unregister ( this.hive, this.folder );
    }

    /**
     * unregister with the hive
     */
    public void unregister ()
    {
        if ( this.hive != null )
        {
            unregisterOutput ( "sessions", this.sessionsOutput );
            unregisterOutput ( "items", this.itemsOutput );
            unregisterOutput ( "futureWrappers", this.itemsOutput );
            unregisterOutput ( "attributeWrites", this.attributeWritesOutput );
            unregisterOutput ( "valueWrites", this.valuesWritesOutput );
            unregisterOutput ( "attributeEvents", this.attributeEventsOutput );
            unregisterOutput ( "valueEvents", this.valueEventsOutput );
            this.hive = null;
        }
        if ( this.folder != null )
        {
            this.folder = null;
        }
        if ( this.timer != null )
        {
            this.timer.shutdown ();
            this.timer = null;
        }
    }

    protected String getId ( final String itemId )
    {
        return this.itemPrefix + "." + itemId;
    }

}
