/**
 * 
 */
package org.openscada.core.ui.connection.views;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.openscada.core.ui.connection.Activator;
import org.openscada.core.ui.connection.data.ConnectionDiscovererBean;

final class ConnectionObservableFactory implements IObservableFactory
{
    public IObservable createObservable ( final Object target )
    {
        if ( target == Activator.ROOT )
        {
            return Activator.getDefault ().getDiscovererSet ();
        }
        else if ( target instanceof ConnectionDiscovererBean )
        {
            final ConnectionDiscovererBean element = (ConnectionDiscovererBean)target;
            return element.getKnownConnections ();
        }
        return null;
    }
}