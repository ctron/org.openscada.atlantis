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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.common.chain.MemoryItemChained;
import org.openscada.da.core.common.chain.item.ScaleInputItem;
import org.openscada.da.core.common.chain.item.ScaleOutputItem;
import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;

public class ScaleChainTest
{
    private MemoryItemChained _item = null;
    private ScaleInputItem _scaleInput = null;
    private ScaleOutputItem _scaleOutput = null;
    
    public static final String INPUT_FACTOR = "org.openscada.da.scale.input.factor";
    public static final String OUTPUT_FACTOR = "org.openscada.da.scale.output.factor";
    
    @Before
    public void init ()
    {
        _item = new MemoryItemChained ( "test-id" );
        _scaleInput = new ScaleInputItem ();
        _scaleOutput = new ScaleOutputItem ();
        _item.addChainElement ( IODirection.INPUT, _scaleInput );
        _item.addChainElement ( IODirection.OUTPUT, _scaleOutput );
    }
    
    @Test
    public void testNoop () throws InvalidOperationException, NullValueException, NotConvertableException
    {
        setAndTest ( 2, 2 );
    }
    
    @Test
    public void testInput1 () throws InvalidOperationException, NullValueException, NotConvertableException
    {
        Map<String,Variant> attributes = new HashMap<String, Variant> ();
        
        attributes.put ( INPUT_FACTOR, new Variant ( 3 ) );
        _item.setAttributes ( attributes );
        
        setAndTest ( 2, 6 );
    }
    
    @Test
    public void testInput2 () throws InvalidOperationException, NullValueException, NotConvertableException
    {
        Map<String,Variant> attributes = new HashMap<String, Variant> ();
        
        setValue ( 2 );
        attributes.put ( INPUT_FACTOR, new Variant ( 3 ) );
        _item.setAttributes ( attributes );
        
        testValue ( 6 );
    }
    
    @Test
    public void testInput3 () throws InvalidOperationException, NullValueException, NotConvertableException
    {
        Map<String,Variant> attributes = new HashMap<String, Variant> ();
        
        setValue ( 2 );
        attributes.put ( INPUT_FACTOR, new Variant ( 3 ) );
        _item.setAttributes ( attributes );
        
        testValue ( 6 );
        
        attributes.put ( INPUT_FACTOR, new Variant () );
        _item.setAttributes ( attributes );
        
        testValue ( 2 );
    }
    
    @Test
    public void testOutput1 () throws InvalidOperationException, NullValueException, NotConvertableException
    {
        Map<String,Variant> attributes = new HashMap<String, Variant> ();
        
        attributes.put ( OUTPUT_FACTOR, new Variant ( 3 ) );
        _item.setAttributes ( attributes );
        
        setAndTest ( 2, 6 );
    }
    
    @Test
    public void testInputOutput1 () throws InvalidOperationException, NullValueException, NotConvertableException
    {
        Map<String,Variant> attributes = new HashMap<String, Variant> ();
        
        attributes.put ( OUTPUT_FACTOR, new Variant ( 3 ) );
        attributes.put ( INPUT_FACTOR, new Variant ( 5 ) );
        _item.setAttributes ( attributes );
        
        setAndTest ( 2, 2 * 3 * 5 );
        
        attributes.put ( OUTPUT_FACTOR, new Variant () );
        attributes.put ( INPUT_FACTOR, new Variant () );
        _item.setAttributes ( attributes );
        
        // Output filter is still active since the value was not re-written
        testValue ( 6 );
        
        // not it must be 2 again
        setValue ( 2 );
        testValue ( 2 );
    }
    
    @Test
    public void testInputOutput2 () throws InvalidOperationException, NullValueException, NotConvertableException
    {
        Map<String,Variant> attributes = new HashMap<String, Variant> ();
        
        setAndTest ( 2, 2 );
        
        attributes.put ( OUTPUT_FACTOR, new Variant ( 3 ) );
        attributes.put ( INPUT_FACTOR, new Variant ( 5 ) );
        _item.setAttributes ( attributes );
        
        testValue ( 2 * 5 );
        setAndTest ( 2, 2 * 3 * 5 );
        
        attributes.put ( OUTPUT_FACTOR, new Variant () );
        attributes.put ( INPUT_FACTOR, new Variant () );
        _item.setAttributes ( attributes );
        
        testValue ( 2 * 3 );
        setAndTest ( 2, 2 );
    }
    
    public void setValue ( double value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        _item.setValue ( new Variant ( value ) );
    }
    
    public void testValue ( double value ) throws InvalidOperationException
    {
        Assert.assertEquals ( new Variant ( value ), _item.getValue () );
    }
    
    public void setAndTest ( double writeValue, double expectedValue ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        setValue ( writeValue );
        testValue ( expectedValue );
    }
}
