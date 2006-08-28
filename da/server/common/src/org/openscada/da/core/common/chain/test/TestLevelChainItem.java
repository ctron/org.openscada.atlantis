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

package org.openscada.da.core.common.chain.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscada.da.core.Variant;
import org.openscada.da.core.common.chain.item.LevelAlarmChainItem;
import org.openscada.da.core.server.IODirection;
import org.openscada.da.core.server.WriteAttributesOperationListener.Results;
import org.openscada.utils.collection.MapBuilder;

public class TestLevelChainItem extends TestInputChain
{
    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger ( TestLevelChainItem.class );
    
    protected LevelAlarmChainItem _levelAlarm = null;
    
    @Before
    public void setupLevelAlarm ()
    {
        _levelAlarm = new LevelAlarmChainItem ();
    }
    
    @Test
    public void testSetValueAfterAttributes ()
    {
        
        Map<String,Variant> attributes = new HashMap<String,Variant> ();
        
        _dataItem.addChainElement ( IODirection.INPUT, _levelAlarm );
        
        attributes.put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 3 ) );
        _dataItem.setAttributes ( attributes );
        addEvent ( attributes );
        
        _dataItem.updateValue ( new Variant ( 4 ) );
        addEvent ( new Variant ( 4 ) );
        
        addEvent ( new MapBuilder<String, Variant> ().put ( LevelAlarmChainItem.HIGH_ALARM, new Variant ( true ) ).getMap () );
        
        assertEvents ();
    }
    
    @Test
    public void testSetValueBeforeAttributes ()
    {
        Map<String,Variant> attributes = new HashMap<String,Variant> ();
        
        _dataItem.addChainElement ( IODirection.INPUT, _levelAlarm );

        _dataItem.updateValue ( new Variant ( 4 ) );
        addEvent ( new Variant ( 4 ) );
        
        attributes.put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 3 ) );
        _dataItem.setAttributes ( attributes );
        
        // both changes come in the same event
        addEvent ( new MapBuilder<String, Variant> ( attributes ).put ( LevelAlarmChainItem.HIGH_ALARM, new Variant ( true ) ).getMap () );
        
        assertEvents ();
    }
    
    @Test
    public void testSetValueAfterAttributesNoInject ()
    {
        
        Map<String,Variant> attributes = new HashMap<String,Variant> ();
        
        attributes.put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 3 ) );
        Results results = _dataItem.setAttributes ( attributes );
        Assert.assertFalse ( "Result is empty", results.isEmpty () );
        Assert.assertFalse ( "Result is ok although it should not", results.isSuccess () );
        Assert.assertFalse ( results.get ( LevelAlarmChainItem.HIGH_PRESET ).isSuccess () );
        
        _dataItem.updateValue ( new Variant ( 4 ) );
        addEvent ( new Variant ( 4 ) );
        
        // no alarm must be present since chain item is not present 
        assertEvents ();
    }
    
    @Test
    public void testSetValueAndModifyLevel ()
    {
        
        Map<String,Variant> attributes = new HashMap<String,Variant> ();
        
        _dataItem.addChainElement ( IODirection.INPUT, _levelAlarm );
        
        attributes.put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 3 ) );
        _dataItem.setAttributes ( attributes );
        addEvent ( attributes );
        
        _dataItem.updateValue ( new Variant ( 4 ) );
        addEvent ( new Variant ( 4 ) );
        
        addEvent ( new MapBuilder<String, Variant> ().put ( LevelAlarmChainItem.HIGH_ALARM, new Variant ( true ) ).getMap () );
        
        assertEvents ();
        
        attributes = new HashMap<String, Variant> ();
        attributes.put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 5 ) );
        _dataItem.setAttributes ( attributes );
        addEvent ( new MapBuilder<String, Variant> ()
                .put ( LevelAlarmChainItem.HIGH_ALARM, new Variant ( false ) )
                .put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 5 ) )
                .getMap () );
        
        assertEvents ();
        
        Assert.assertEquals ( new MapBuilder<String, Variant> ()
                .put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 5 ) )
                .put ( LevelAlarmChainItem.HIGH_ALARM, new Variant ( false ) )
                .getMap (), _dataItem.getAttributes () );
    }
    
    @Test
    public void testSetValueAndClearLevel ()
    {
        
        Map<String,Variant> attributes = new HashMap<String,Variant> ();
        
        _dataItem.addChainElement ( IODirection.INPUT, _levelAlarm );
        
        attributes.put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 3 ) );
        _dataItem.setAttributes ( attributes );
        addEvent ( attributes );
        
        _dataItem.updateValue ( new Variant ( 4 ) );
        addEvent ( new Variant ( 4 ) );
        
        addEvent ( new MapBuilder<String, Variant> ().put ( LevelAlarmChainItem.HIGH_ALARM, new Variant ( true ) ).getMap () );
        
        assertEvents ();
        
        attributes = new HashMap<String, Variant> ();
        attributes.put ( LevelAlarmChainItem.HIGH_PRESET, null );
        _dataItem.setAttributes ( attributes );
        addEvent ( new MapBuilder<String, Variant> ()
                .put ( LevelAlarmChainItem.HIGH_PRESET, null )
                .put ( LevelAlarmChainItem.HIGH_ALARM, null )
                .getMap () );
        
        assertEvents ();
        
        Assert.assertEquals ( new MapBuilder<String, Variant> ()
                .getMap (), _dataItem.getAttributes () );
    }
    
}
