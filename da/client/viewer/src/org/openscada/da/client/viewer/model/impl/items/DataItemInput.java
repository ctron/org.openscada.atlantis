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

package org.openscada.da.client.viewer.model.impl.items;

import java.util.EnumSet;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.client.Connection;
import org.openscada.da.client.viewer.model.AlreadyConnectedException;
import org.openscada.da.client.viewer.model.Connector;
import org.openscada.da.client.viewer.model.InputDefinition;
import org.openscada.da.client.viewer.model.NotConnectedException;
import org.openscada.da.client.viewer.model.Type;

public class DataItemInput implements InputDefinition
{
    private static Logger _log = Logger.getLogger ( DataItemInput.class );
    
    private String _name = null;
    private Connection _connection = null;
    private String _item = null;

    private Connector _connector = null;

    public DataItemInput ( Connection connection, String item, String name )
    {
        _connection = connection;
        _item = item;
        _name = name;
    }

    public EnumSet<Type> getSupportedTypes ()
    {
        return EnumSet.of ( Type.VARIANT );
    }

    public void connect ( Connector connector ) throws AlreadyConnectedException
    {
        if ( _connector == null )
        {
            _connector = connector;
        }
        else
        {
            throw new AlreadyConnectedException ();
        }
    }

    public void disconnect ( Connector connector ) throws NotConnectedException
    {
        if ( _connector == connector )
            _connector = null;
        else
            throw new NotConnectedException ();
    }

    public String getName ()
    {
        return _name;
    }

    public void update ( Type type, Object value )
    {
        _log.debug ( String.format ( "DataItemInput Update: %s/%s", type, value ) );
        
        switch ( type )
        {
        case VARIANT:
            write ( (Variant)value );
            break;
        case BOOLEAN:
            write ( new Variant ( (Boolean)value ) );
            break;
        case INTEGER:
            write ( new Variant ( (Long)value ) );
            break;
        case STRING:
            write ( new Variant ( (String)value ) );
            break;
        case DOUBLE:
            write ( new Variant ( (Double)value ) );
            break;
        case NULL:
            break;
        default:
            write ( new Variant () );
            break;
        }
    }

    protected void write ( Variant value )
    {
        if ( value == null )
            return;
        
        try
        {
            _connection.write ( _item, value );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            // TODO: handle error
        }
    }
}
