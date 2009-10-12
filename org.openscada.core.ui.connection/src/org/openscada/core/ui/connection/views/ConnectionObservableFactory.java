/**
 * 
 */
package org.openscada.core.ui.connection.views;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.openscada.core.ui.connection.Activator;
import org.openscada.core.ui.connection.data.ConnectionDiscovererBean;
import org.openscada.core.ui.connection.data.ConnectionHolder;

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

    public IObservableMap createElementMap ( final Object target, final IObservableSet observableSet )
    {
        if ( target instanceof ConnectionDiscovererBean )
        {
            return BeansObservables.observeMap ( observableSet, ConnectionHolder.PROP_CONNECTION_SERVICE );
        }
        return null;
    }
}