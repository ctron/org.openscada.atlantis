/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.chain.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.chain.item.LevelAlarmChainItem;
import org.openscada.utils.collection.MapBuilder;

public class TestLevelChainItem extends TestInputChain
{
    @SuppressWarnings ( "unused" )
    private static Logger _log = Logger.getLogger ( TestLevelChainItem.class );

    protected LevelAlarmChainItem _levelAlarm = null;

    protected HiveServiceRegistry serviceRegistry = new HiveServiceRegistryTestImpl ();

    @Before
    public void setupLevelAlarm ()
    {
        this._levelAlarm = new LevelAlarmChainItem ( this.serviceRegistry );
    }

    @Test
    public void testSetValueAfterAttributes () throws InterruptedException, ExecutionException
    {

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        this.dataItem.addChainElement ( IODirection.INPUT, this._levelAlarm );

        attributes.put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 3 ) );
        this.dataItem.startSetAttributes ( attributes ).get ();
        addEvent ( null, attributes );

        this.dataItem.updateData ( new Variant ( 4 ), null, null );
        addEvent ( new Variant ( 4 ), new MapBuilder<String, Variant> ().put ( LevelAlarmChainItem.HIGH_ALARM, new Variant ( true ) ).getMap () );

        assertEvents ();
    }

    @Test
    public void testSetValueBeforeAttributes () throws InterruptedException, ExecutionException
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        this.dataItem.addChainElement ( IODirection.INPUT, this._levelAlarm );

        this.dataItem.updateData ( new Variant ( 4 ), null, null );
        addEvent ( new Variant ( 4 ), null );

        attributes.put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 3 ) );
        this.dataItem.startSetAttributes ( attributes ).get ();

        // both changes come in the same event
        addEvent ( null, new MapBuilder<String, Variant> ( attributes ).put ( LevelAlarmChainItem.HIGH_ALARM, new Variant ( true ) ).getMap () );

        assertEvents ();
    }

    @Test
    public void testSetValueAfterAttributesNoInject () throws InterruptedException, ExecutionException
    {

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        attributes.put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 3 ) );
        final WriteAttributeResults writeAttributeResults = this.dataItem.startSetAttributes ( attributes ).get ();
        Assert.assertFalse ( "WriteAttributeResult is empty", writeAttributeResults.isEmpty () );
        Assert.assertFalse ( "WriteAttributeResult is ok although it should not", writeAttributeResults.isSuccess () );
        Assert.assertFalse ( writeAttributeResults.get ( LevelAlarmChainItem.HIGH_PRESET ).isSuccess () );

        this.dataItem.updateData ( new Variant ( 4 ), null, null );
        addEvent ( new Variant ( 4 ), null );

        // no alarm must be present since chain item is not present 
        assertEvents ();
    }

    @Test
    public void testSetValueAndModifyLevel () throws InterruptedException, ExecutionException
    {

        Map<String, Variant> attributes = new HashMap<String, Variant> ();

        this.dataItem.addChainElement ( IODirection.INPUT, this._levelAlarm );

        attributes.put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 3 ) );
        this.dataItem.startSetAttributes ( attributes ).get ();
        addEvent ( null, attributes );

        this.dataItem.updateData ( new Variant ( 4 ), null, null );
        addEvent ( new Variant ( 4 ), new MapBuilder<String, Variant> ().put ( LevelAlarmChainItem.HIGH_ALARM, new Variant ( true ) ).getMap () );

        assertEvents ();

        attributes = new HashMap<String, Variant> ();
        attributes.put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 5 ) );
        this.dataItem.startSetAttributes ( attributes ).get ();
        addEvent ( null, new MapBuilder<String, Variant> ().put ( LevelAlarmChainItem.HIGH_ALARM, new Variant ( false ) ).put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 5 ) ).getMap () );

        assertEvents ();

        Assert.assertEquals ( new MapBuilder<String, Variant> ().put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 5 ) ).put ( LevelAlarmChainItem.HIGH_ALARM, new Variant ( false ) ).getMap (), this.dataItem.getAttributes () );
    }

    @Test
    public void testSetValueAndClearLevel () throws InterruptedException, ExecutionException
    {

        Map<String, Variant> attributes = new HashMap<String, Variant> ();

        this.dataItem.addChainElement ( IODirection.INPUT, this._levelAlarm );

        attributes.put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 3 ) );
        this.dataItem.startSetAttributes ( attributes ).get ();
        addEvent ( null, attributes );

        this.dataItem.updateData ( new Variant ( 4 ), null, null );
        addEvent ( new Variant ( 4 ), new MapBuilder<String, Variant> ().put ( LevelAlarmChainItem.HIGH_ALARM, new Variant ( true ) ).getMap () );

        assertEvents ();

        attributes = new HashMap<String, Variant> ();
        attributes.put ( LevelAlarmChainItem.HIGH_PRESET, null );
        this.dataItem.startSetAttributes ( attributes ).get ();
        addEvent ( null, new MapBuilder<String, Variant> ().put ( LevelAlarmChainItem.HIGH_PRESET, null ).put ( LevelAlarmChainItem.HIGH_ALARM, null ).getMap () );

        assertEvents ();

        Assert.assertEquals ( new MapBuilder<String, Variant> ().getMap (), this.dataItem.getAttributes () );
    }

}
