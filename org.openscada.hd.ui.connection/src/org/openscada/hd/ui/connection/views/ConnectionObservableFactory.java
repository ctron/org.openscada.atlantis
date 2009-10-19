package org.openscada.hd.ui.connection.views;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.openscada.core.ui.connection.data.ConnectionHolder;
import org.openscada.hd.ui.connection.internal.ConnectionWrapper;
import org.openscada.hd.ui.connection.internal.ItemListWrapper;
import org.openscada.hd.ui.connection.internal.QueryWrapper;

final class ConnectionObservableFactory implements IObservableFactory
{
    public IObservable createObservable ( final Object target )
    {
        if ( target instanceof ConnectionHolder )
        {
            return new ConnectionWrapper ( (ConnectionHolder)target );
        }
        else if ( target instanceof ItemListWrapper )
        {
            return new ItemListObserver ( ( (ItemListWrapper)target ).getConnection () );
        }
        else if ( target instanceof QueryWrapper )
        {
            return ( (QueryWrapper)target ).getQueriesObservable ();
        }

        return null;
    }

}