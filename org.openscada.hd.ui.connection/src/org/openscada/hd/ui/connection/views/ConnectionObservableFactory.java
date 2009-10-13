/**
 * 
 */
package org.openscada.hd.ui.connection.views;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.openscada.core.ui.connection.data.ConnectionDiscovererBean;
import org.openscada.core.ui.connection.data.ConnectionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ConnectionObservableFactory implements IObservableFactory
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionObservableFactory.class );

    public IObservable createObservable ( final Object target )
    {
        if ( target instanceof ConnectionHolder )
        {
            return new ConnectionWrapper ( (ConnectionHolder)target );
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