/**
 * 
 */
package org.openscada.core.ui.connection.views;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.openscada.core.ui.connection.data.ConnectionHolder;

final class ConnectionElementsObservableFactory implements IObservableFactory
{
    public IObservable createObservable ( final Object target )
    {
        if ( target instanceof ConnectionHolder )
        {
            return BeanProperties.value ( ConnectionHolder.PROP_CONNECTION_SERVICE ).observe ( target );
            // return BeansObservables.observeValue ( target, ConnectionHolder.PROP_CONNECTION_SERVICE );
        }
        return null;
    }
}