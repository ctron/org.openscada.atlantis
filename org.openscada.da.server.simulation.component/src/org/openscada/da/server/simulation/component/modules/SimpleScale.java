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

package org.openscada.da.server.simulation.component.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.openscada.core.Variant;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.simulation.component.Hive;
import org.openscada.utils.collection.MapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleScale extends BaseModule
{
    private final static Logger logger = LoggerFactory.getLogger ( SimpleScale.class );

    private Thread _thread = null;

    private final int _minDelay = 2 * 1000;

    private final int _maxDelay = 10 * 1000;

    private final int _minWeight = 10000;

    private final int _maxWeight = 30000;

    private final double _errorRatio = 0.10;

    private final DataItemInputChained _valueInput;

    private final DataItemInputChained _errorInput;

    private final DataItemInputChained _activeInput;

    private static Random _random = new Random ();

    public SimpleScale ( final Hive hive, final String id )
    {
        super ( hive, "scale." + id );

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "tag", new Variant ( "scale." + id ) );

        final DataItemCommand startCommand = getOutput ( "start", attributes );
        startCommand.addListener ( new DataItemCommand.Listener () {

            @Override
            public void command ( final Variant value )
            {
                startWeight ();
            }
        } );

        this._valueInput = getInput ( "value", attributes );
        this._errorInput = getInput ( "error", attributes );
        this._activeInput = getInput ( "active", new MapBuilder<String, Variant> ( attributes ).put ( "description", new Variant ( "An indicator if a weight process is active. True means: active, false: not active" ) ).getMap () );
        this._activeInput.updateData ( Variant.valueOf ( false ), null, null );
    }

    protected synchronized void startWeight ()
    {
        if ( this._thread != null )
        {
            return;
        }
        this._thread = new Thread ( new Runnable () {

            @Override
            public void run ()
            {
                performWeight ();
            }
        } );
        this._thread.start ();
    }

    protected void performWeight ()
    {
        final int delay = this._minDelay + _random.nextInt ( this._maxDelay - this._minDelay );
        logger.debug ( String.format ( "Weight delay: %d", delay ) );

        this._activeInput.updateData ( Variant.valueOf ( true ), new MapBuilder<String, Variant> ().put ( "sim.scale.last-delay", new Variant ( delay ) ).getMap (), null );

        try
        {
            Thread.sleep ( delay );
        }
        catch ( final InterruptedException e )
        {
        }

        final boolean error = _random.nextDouble () < this._errorRatio;

        if ( error )
        {
            final int errorCode = _random.nextInt ( 255 );
            finishWithError ( errorCode );
        }
        else
        {
            final int weight = this._minWeight + _random.nextInt ( this._maxWeight - this._minWeight );
            finishWeight ( weight );
        }

        this._activeInput.updateData ( Variant.valueOf ( false ), null, null );

        this._thread = null;
    }

    protected void finishWeight ( final int value )
    {
        this._valueInput.updateData ( Variant.valueOf ( value ), null, null );
        this._errorInput.updateData ( Variant.NULL, null, null );
    }

    protected void finishWithError ( final int errorCode )
    {
        this._valueInput.updateData ( Variant.NULL, null, null );
        this._errorInput.updateData ( Variant.valueOf ( errorCode ), null, null );
    }

}
