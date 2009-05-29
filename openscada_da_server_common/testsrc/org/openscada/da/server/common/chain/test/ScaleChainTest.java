/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.chain.MemoryItemChained;
import org.openscada.da.server.common.chain.item.ScaleInputItem;
import org.openscada.da.server.common.chain.item.ScaleOutputItem;

public class ScaleChainTest
{
    private MemoryItemChained _item = null;

    private ScaleInputItem _scaleInput = null;

    private ScaleOutputItem _scaleOutput = null;

    public static final String INPUT_FACTOR = "org.openscada.da.scale.input.factor";

    public static final String OUTPUT_FACTOR = "org.openscada.da.scale.output.factor";

    protected HiveServiceRegistry serviceRegistry = new HiveServiceRegistryTestImpl ();

    @Before
    public void init ()
    {
        this._item = new MemoryItemChained ( "test-id" );
        this._scaleInput = new ScaleInputItem ( this.serviceRegistry );
        this._scaleOutput = new ScaleOutputItem ( this.serviceRegistry );
        this._item.addChainElement ( IODirection.INPUT, this._scaleInput );
        this._item.addChainElement ( IODirection.OUTPUT, this._scaleOutput );
    }

    @Test
    public void testNoop () throws InvalidOperationException, NullValueException, NotConvertableException, OperationException, InterruptedException, ExecutionException
    {
        setAndTest ( 2, 2 );
    }

    @Test
    public void testInput1 () throws ExecutionException, InterruptedException, InvalidOperationException, NullValueException, NotConvertableException, OperationException
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        attributes.put ( INPUT_FACTOR, new Variant ( 3 ) );
        this._item.startSetAttributes ( attributes ).get ();

        setAndTest ( 2, 6 );
    }

    @Test
    public void testInput2 () throws InvalidOperationException, NullValueException, NotConvertableException, OperationException, InterruptedException, ExecutionException
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        setValue ( 2 );
        attributes.put ( INPUT_FACTOR, new Variant ( 3 ) );
        this._item.startSetAttributes ( attributes ).get ();

        testValue ( 6 );
    }

    @Test
    public void testInput3 () throws InvalidOperationException, NullValueException, NotConvertableException, OperationException, InterruptedException, ExecutionException
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        setValue ( 2 );
        attributes.put ( INPUT_FACTOR, new Variant ( 3 ) );
        this._item.startSetAttributes ( attributes ).get ();

        testValue ( 6 );

        attributes.put ( INPUT_FACTOR, new Variant () );
        this._item.startSetAttributes ( attributes ).get ();

        testValue ( 2 );
    }

    @Test
    public void testOutput1 () throws InvalidOperationException, NullValueException, NotConvertableException, OperationException, InterruptedException, ExecutionException
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        attributes.put ( OUTPUT_FACTOR, new Variant ( 3 ) );
        this._item.startSetAttributes ( attributes ).get ();

        setAndTest ( 2, 6 );
    }

    @Test
    public void testInputOutput1 () throws InvalidOperationException, NullValueException, NotConvertableException, OperationException, InterruptedException, ExecutionException
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        attributes.put ( OUTPUT_FACTOR, new Variant ( 3 ) );
        attributes.put ( INPUT_FACTOR, new Variant ( 5 ) );
        this._item.startSetAttributes ( attributes ).get ();

        setAndTest ( 2, 2 * 3 * 5 );

        attributes.put ( OUTPUT_FACTOR, new Variant () );
        attributes.put ( INPUT_FACTOR, new Variant () );
        this._item.startSetAttributes ( attributes ).get ();

        // Output filter is still active since the value was not re-written
        testValue ( 6 );

        // not it must be 2 again
        setValue ( 2 );
        testValue ( 2 );
    }

    @Test
    public void testInputOutput2 () throws InvalidOperationException, NullValueException, NotConvertableException, OperationException, InterruptedException, ExecutionException
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        setAndTest ( 2, 2 );

        attributes.put ( OUTPUT_FACTOR, new Variant ( 3 ) );
        attributes.put ( INPUT_FACTOR, new Variant ( 5 ) );
        this._item.startSetAttributes ( attributes ).get ();

        testValue ( 2 * 5 );
        setAndTest ( 2, 2 * 3 * 5 );

        attributes.put ( OUTPUT_FACTOR, new Variant () );
        attributes.put ( INPUT_FACTOR, new Variant () );
        this._item.startSetAttributes ( attributes ).get ();

        testValue ( 2 * 3 );
        setAndTest ( 2, 2 );
    }

    public void setValue ( final double value ) throws InvalidOperationException, NullValueException, NotConvertableException, OperationException, InterruptedException, ExecutionException
    {
        this._item.startWriteValue ( new Variant ( value ) ).get ();
    }

    public void testValue ( final double value ) throws InvalidOperationException, InterruptedException, ExecutionException
    {
        Assert.assertEquals ( new Variant ( value ), this._item.readValue ().get () );
    }

    public void setAndTest ( final double writeValue, final double expectedValue ) throws InvalidOperationException, NullValueException, NotConvertableException, OperationException, InterruptedException, ExecutionException
    {
        setValue ( writeValue );
        testValue ( expectedValue );
    }
}
