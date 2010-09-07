/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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
