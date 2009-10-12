package org.openscada.core.ui.connection.views;

import org.eclipse.jface.databinding.viewers.ObservableSetTreeContentProvider;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;
import org.openscada.core.ui.connection.Activator;
import org.openscada.core.ui.connection.data.ConnectionDiscovererBean;
import org.openscada.core.ui.connection.data.ConnectionHolder;

public class ConnectionContentProvider extends ObservableSetTreeContentProvider
{

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
        super ( new ConnectionObservableFactory (), new TreeStructureAdvisorExtension () );
    }

}
