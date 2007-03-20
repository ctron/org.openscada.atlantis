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

package org.openscada.da.client.viewer.model.impl;

import org.apache.log4j.Logger;
import org.openscada.da.client.viewer.model.AlreadyConnectedException;
import org.openscada.da.client.viewer.model.Connector;
import org.openscada.da.client.viewer.model.InputDefinition;
import org.openscada.da.client.viewer.model.NotConnectedException;
import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.OutputListener;
import org.openscada.da.client.viewer.model.Type;

/**
 * A simple pass-through connector that will set the input with the value of
 * the output.
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public class PassThroughConnector implements Connector, OutputListener
{
    private static Logger _log = Logger.getLogger ( PassThroughConnector.class );
    
    private InputDefinition _input = null;
    private OutputDefinition _output = null;
    
    private Type _lastType = Type.NULL;
    private Object _lastValue = null;

    public InputDefinition getInput ()
    {
        return _input;
    }

    public synchronized void setInput ( InputDefinition input ) throws AlreadyConnectedException
    {
        if ( _input != null )
        {
            try
            {
                _input.disconnect ( this );
            }
            catch ( NotConnectedException e )
            {
                _log.warn ( "Failed to disconnect", e );
            }
        }

        _input = input;

        if ( _input != null )
        {
            _input.connect ( this );
            _input.update ( _lastType, _lastValue );
        }
    }

    public OutputDefinition getOutput ()
    {
        return _output;
    }

    public synchronized void setOutput ( OutputDefinition output )
    {
        if ( _output != null )
        {
            _output.removeListener ( this );
        }
        
        _output = output;
        
        if ( _output != null )
        {
            _output.addListener ( this );
        }
    }

    public void update ( Type type, Object value )
    {
        _log.debug ( String.format ( "Passing on value ('%s'->'%s'): %s/%s (%s)", _output.getName (), _input.getName (), type.name (), value, this.getClass () ) );
        
        _lastType = type;
        _lastValue = value;
        if ( _input != null )
        {
            _input.update ( type, value );
        }
    }
    
    public void dispose ()
    {
        try
        {
            setInput ( null );
        }
        catch ( AlreadyConnectedException e )
        {
            // will not be thrown since we only disconnect
        }
        setOutput ( null );
        
        // dispose value
        _lastType = Type.NULL;
        _lastValue = null;
    }
}
