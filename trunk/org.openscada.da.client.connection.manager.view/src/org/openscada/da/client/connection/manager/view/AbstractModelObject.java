/**
 * 
 */
package org.openscada.da.client.connection.manager.view;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class AbstractModelObject
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

    public void removePropertyChangeListener ( final PropertyChangeListener listener )
    {
        this.propertyChangeSupport.removePropertyChangeListener ( listener );
    }

    public void removePropertyChangeListener ( final String propertyName, final PropertyChangeListener listener )
    {
        this.propertyChangeSupport.removePropertyChangeListener ( propertyName, listener );
    }

    protected void firePropertyChange ( final String propertyName, final Object oldValue, final Object newValue )
    {
        this.propertyChangeSupport.firePropertyChange ( propertyName, oldValue, newValue );
    }
}