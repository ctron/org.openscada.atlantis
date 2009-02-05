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

package org.openscada.da.client.viewer.model.impl.containers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.geometry.Point;
import org.openscada.da.client.viewer.model.impl.BooleanSetterOutput;

public class CommandContainer extends ClickableContainer
{
    private static Logger _log = Logger.getLogger ( CommandContainer.class );
    
    private Map<String, CommandInformation> _commands = new HashMap<String, CommandInformation> ();
    
    public CommandContainer ( String id )
    {
        super ( id );
    }

    @Override
    protected void clicked ( ActionEvent event )
    {
        _log.debug ( "Clicked" );
        
        Point p = getBounds ().getCenter ();
        _figure.translateToAbsolute ( p );
        
        CommandMessageDialog cmd = new CommandMessageDialog ( _canvas.getShell (), _canvas.toDisplay ( p.getSWTPoint () ), _commands.values ().toArray ( new CommandInformation [0] ) );
        cmd.open ();
    }
    
    public void setCommands ( String commands )
    {
        clear ();
        
        for ( String command : commands.split ( "," ) )
        {
            String name = "";
            String label = "";
            
            Matcher m = Pattern.compile ( "(.*?)\\|(.*)" ).matcher ( command );
            if ( m.matches () )
            {
                name = m.group ( 1 );
                label = m.group ( 2 );
            }
            else
            {
                name = label = command;
            }
            
            addCommand ( name, label );
        }
    }
    
    public void clear ()
    {
        for ( String name : _commands.keySet () )
        {
            removeCommand ( name );
        }
    }
    
    protected void addCommand ( String name, String label )
    {
        _log.debug ( String.format ( "Adding command %s|%s", name, label ) );
        
        if ( _commands.containsKey ( name ) )
            removeCommand ( name );
        
        CommandInformation ci = new CommandInformation ();
        ci.setName ( name );
        ci.setLabel ( label );
        ci.setOutput ( new BooleanSetterOutput ( name ) );
        
        _commands.put ( name, ci );
        addOutput ( ci.getOutput () );
    }
    
    protected void removeCommand ( String name )
    {
        CommandInformation ci = _commands.remove ( name );
        if ( ci != null )
        {
            removeOutput ( ci.getName () );
        }
    }
}
