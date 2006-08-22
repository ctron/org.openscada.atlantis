package org.openscada.da.core.common.chained.test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscada.da.core.WriteAttributesOperationListener.Result;
import org.openscada.da.core.WriteAttributesOperationListener.Results;
import org.openscada.da.core.common.chained.LevelAlarmChainItem;
import org.openscada.da.core.data.Variant;
import org.openscada.utils.collection.MapBuilder;

public class TestLevelChainItem extends TestInputChain
{
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
        
        _dataItem.addInputChainElement ( _levelAlarm );
        
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
        
        _dataItem.addInputChainElement ( _levelAlarm );

        _dataItem.updateValue ( new Variant ( 4 ) );
        addEvent ( new Variant ( 4 ) );
        
        attributes.put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 3 ) );
        _dataItem.setAttributes ( attributes );
        
        // both changes come in the same event
        addEvent ( new MapBuilder<String, Variant> ( attributes ).put ( LevelAlarmChainItem.HIGH_ALARM, new Variant ( true ) ).getMap () );
        
        assertEvents ();
    }
    
    @Test
    public void testSetValueAfterAttributesLateInject ()
    {
        
        Map<String,Variant> attributes = new HashMap<String,Variant> ();
        
        attributes.put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 3 ) );
        Results results = _dataItem.setAttributes ( attributes );
        Assert.assertFalse ( "Result is empty", results.isEmpty () );
        Assert.assertFalse ( "Result is ok although it should not", results.isSuccess () );
        Assert.assertFalse ( results.get ( LevelAlarmChainItem.HIGH_PRESET ).isSuccess () );
        addEvent ( attributes );
        
        _dataItem.updateValue ( new Variant ( 4 ) );
        addEvent ( new Variant ( 4 ) );
        
        // no alarm must be present since chain item is not present 
        assertEvents ();
    }
    
    @Test
    public void testSetValueAndModifyLevel ()
    {
        
        Map<String,Variant> attributes = new HashMap<String,Variant> ();
        
        _dataItem.addInputChainElement ( _levelAlarm );
        
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
                .put ( LevelAlarmChainItem.HIGH_ALARM, null )
                .put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 5 ) )
                .getMap () );
        
        assertEvents ();
        
        Assert.assertEquals ( new MapBuilder<String, Variant> ()
                .put ( LevelAlarmChainItem.HIGH_PRESET, new Variant ( 5 ) )
                .getMap (), _dataItem.getAttributes () );
    }
    
    @Test
    public void testSetValueAndClearLevel ()
    {
        
        Map<String,Variant> attributes = new HashMap<String,Variant> ();
        
        _dataItem.addInputChainElement ( _levelAlarm );
        
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
