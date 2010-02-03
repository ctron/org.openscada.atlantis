/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.exporter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * An abstract base class for property change support. Derive your class from this one if you
 * would like to add default property change support
 * @author Jens Reimann
 *
 */
public class AbstractPropertyChange
{

    protected transient final PropertyChangeSupport listeners = new PropertyChangeSupport ( this );

    public void addPropertyChangeListener ( final PropertyChangeListener listener )
    {
        this.listeners.addPropertyChangeListener ( listener );
    }

    public void removePropertyChangeListener ( final PropertyChangeListener listener )
    {
        this.listeners.removePropertyChangeListener ( listener );
    }

    public void addPropertyChangeListener ( final String propertyName, final PropertyChangeListener listener )
    {
        this.listeners.addPropertyChangeListener ( propertyName, listener );
    }

    public void removePropertyChangeListener ( final String propertyName, final PropertyChangeListener listener )
    {
        this.listeners.removePropertyChangeListener ( propertyName, listener );
    }

}