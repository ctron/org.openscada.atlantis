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

package org.openscada.da.client.viewer.model;

import java.util.Collection;

import org.openscada.da.client.viewer.configurator.ConfigurationError;

public interface Container extends DynamicObject
{
    public abstract void add ( DynamicObject object );
    public abstract void remove ( DynamicObject object );
    public abstract Collection<DynamicObject> getObjects ();
    
    public class Export
    {
        public String _object;
        public String _name;
        public String _alias;
        
        public Export ( String object, String name, String alias )
        {
            _object = object;
            _name = name;
            _alias = alias;
        }

        public String getAlias ()
        {
            return _alias;
        }

        public void setAlias ( String alias )
        {
            _alias = alias;
        }

        public String getName ()
        {
            return _name;
        }

        public void setName ( String name )
        {
            _name = name;
        }

        public String getObject ()
        {
            return _object;
        }

        public void setObject ( String object )
        {
            _object = object;
        }
    }
    
    public abstract void addInputExport ( Export export ) throws ConfigurationError;
    public abstract void removeInputExport ( String eportName );
    public abstract Collection<Export> getInputExports ();
    
    public abstract void addOutputExport ( Export export ) throws ConfigurationError;
    public abstract void removeOutputExport ( String exportName );
    public abstract Collection<Export> getOutputExports ();
    
    public abstract void add ( Connector connector );
    public abstract void remove ( Connector connector );
    public abstract Collection<Connector> getConnectors ();

}
