package org.openscada.core.ui.connection.views;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.jface.databinding.viewers.ObservableSetTreeContentProvider;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;
import org.openscada.core.ui.connection.Activator;
import org.openscada.core.ui.connection.ConnectionDiscovererBean;
import org.openscada.core.ui.connection.ConnectionHolder;

public class ConnectionContentProvider extends ObservableSetTreeContentProvider
{

    private final static class IObservableFactoryImplementation implements IObservableFactory
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

    private final static class TreeStructureAdvisorExtension extends TreeStructureAdvisor
    {
        @Override
        public Object getParent ( final Object element )
        {
            if ( element instanceof ConnectionDiscovererBean )
            {
                return Activator.ROOT;
            }
            if ( element instanceof ConnectionHolder )
            {
                return ( (ConnectionHolder)element ).getDiscoverer ();
            }
            return super.getParent ( element );
        }
    }

    public ConnectionContentProvider ()
    {
        super ( new IObservableFactoryImplementation (), new TreeStructureAdvisorExtension () );
    }

}
