package org.openscada.da.core.common.chained.test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscada.da.core.common.chained.DataItemInputChained;
import org.openscada.da.core.data.Variant;

public class TestInputChain
{
    protected TestItemListener _listener = null;
    protected DataItemInputChained _dataItem = null;
    protected List<EventEntry> _expectedEvents = new LinkedList<EventEntry> ();
    
    @BeforeClass
    public static void setupLog4j ()
    {
        BasicConfigurator.configure ();
    }
    
    @Before
    public void init ()
    {
        _listener = new TestItemListener ();
        _dataItem = new DataItemInputChained ( "test-id" );
        _dataItem.setListener ( _listener );
    }
    
    @Test
    public void testValueEvent () throws Exception
    {
        _dataItem.updateValue ( new Variant ( 1 ) );
        addEvent ( new Variant ( 1 ) );
        assertEvents ();
    }
    
    @Test
    public void testAttributeEvent () throws Exception
    {
        Map<String,Variant> attributes = new HashMap<String,Variant> ();
        attributes.put ( "name", new Variant ( "value" ) );
        
        _dataItem.updateAttributes ( attributes );
        addEvent ( attributes );

        assertEvents ();
    }
    
    @Test
    public void testPreSetValue () throws Exception 
    {
        _dataItem.setListener ( null );
        _dataItem.updateValue ( new Variant ( true ) );
        assertEvents ();
        
        addEvent ( new Variant ( true ) );
        _dataItem.setListener ( _listener );
        assertEvents ();
    }
    
    @Test
    public void testPreSetAttributes () throws Exception 
    {
        Map<String,Variant> attributes = new HashMap<String,Variant> ();
        attributes.put ( "test", new Variant ( "test") );
        
        _dataItem.setListener ( null );
        _dataItem.updateAttributes ( attributes );
        assertEvents ();
        
        addEvent ( attributes );
        _dataItem.setListener ( _listener );
        assertEvents ();
    }
    
    protected void assertEvents ()
    {
        _listener.assertEquals ( _expectedEvents );
    }
    
    protected void addEvent ( Variant value )
    {
        _expectedEvents.add ( new EventEntry ( _dataItem, new Variant ( value ), null ) );
    }
    
    protected void addEvent ( Map<String, Variant> attributes )
    {
        _expectedEvents.add ( new EventEntry ( _dataItem, null, new HashMap<String, Variant> ( attributes ) ) );
    }
}
