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

package org.openscada.da.server.test.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AbstractPropertyChange
{
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport ( this );

    public void addPropertyChangeListener ( final PropertyChangeListener listener )
    {
        this.propertyChangeSupport.addPropertyChangeListener ( listener );
    }

    public void addPropertyChangeListener ( final String propertyName, final PropertyChangeListener listener )
    {
        this.propertyChangeSupport.addPropertyChangeListener ( propertyName, listener );
    }

    protected void firePropertyChange ( final PropertyChangeEvent evt )
    {
        this.propertyChangeSupport.firePropertyChange ( evt );
    }

    protected void firePropertyChange ( final String propertyName, final boolean oldValue, final boolean newValue )
    {
        this.propertyChangeSupport.firePropertyChange ( propertyName, oldValue, newValue );
    }

    protected void firePropertyChange ( final String propertyName, final int oldValue, final int newValue )
    {
        this.propertyChangeSupport.firePropertyChange ( propertyName, oldValue, newValue );
    }

    protected void firePropertyChange ( final String propertyName, final Object oldValue, final Object newValue )
    {
        this.propertyChangeSupport.firePropertyChange ( propertyName, oldValue, newValue );
    }

    public void removePropertyChangeListener ( final PropertyChangeListener listener )
    {
        this.propertyChangeSupport.removePropertyChangeListener ( listener );
    }

    public void removePropertyChangeListener ( final String propertyName, final PropertyChangeListener listener )
    {
        this.propertyChangeSupport.removePropertyChangeListener ( propertyName, listener );
    }
}
