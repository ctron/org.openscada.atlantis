/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.simulation.component.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.simulation.component.Hive;
import org.openscada.utils.collection.MapBuilder;

public class SimpleScale extends BaseModule
{
    private static Logger _log = Logger.getLogger ( SimpleScale.class );

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

            public void command ( final Variant value )
            {
                startWeight ();
            }
        } );

        this._valueInput = getInput ( "value", attributes );
        this._errorInput = getInput ( "error", attributes );
        this._activeInput = getInput ( "active", new MapBuilder<String, Variant> ( attributes ).put ( "description", new Variant ( "An indicator if a weight process is active. True means: active, false: not active" ) ).getMap () );
        this._activeInput.updateData ( new Variant ( false ), null, null );
    }

    protected synchronized void startWeight ()
    {
        if ( this._thread != null )
        {
            return;
        }
        this._thread = new Thread ( new Runnable () {

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
        _log.debug ( String.format ( "Weight delay: %d", delay ) );

        this._activeInput.updateData ( new Variant ( true ), new MapBuilder<String, Variant> ().put ( "sim.scale.last-delay", new Variant ( delay ) ).getMap (), null );

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

        this._activeInput.updateData ( new Variant ( false ), null, null );

        this._thread = null;
    }

    protected void finishWeight ( final int value )
    {
        this._valueInput.updateData ( new Variant ( value ), null, null );
        this._errorInput.updateData ( new Variant (), null, null );
    }

    protected void finishWithError ( final int errorCode )
    {
        this._valueInput.updateData ( new Variant (), null, null );
        this._errorInput.updateData ( new Variant ( errorCode ), null, null );
    }

}
