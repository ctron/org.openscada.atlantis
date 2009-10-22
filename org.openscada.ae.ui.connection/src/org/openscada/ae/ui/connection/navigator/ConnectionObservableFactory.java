package org.openscada.ae.ui.connection.navigator;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.openscada.ae.ui.connection.internal.ConnectionWrapper;
import org.openscada.core.ui.connection.data.ConnectionHolder;

final class ConnectionObservableFactory implements IObservableFactory
{
    public IObservable createObservable ( final Object target )
    {
        if ( target instanceof ConnectionHolder )
        {
            return new ConnectionWrapper ( (ConnectionHolder)target );
        }

        return null;
    }

}