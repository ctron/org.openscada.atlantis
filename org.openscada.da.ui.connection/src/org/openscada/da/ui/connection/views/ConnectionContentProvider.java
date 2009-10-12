package org.openscada.da.ui.connection.views;

import org.eclipse.jface.databinding.viewers.ObservableSetTreeContentProvider;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionContentProvider extends ObservableSetTreeContentProvider
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionContentProvider.class );

    private final static class TreeStructureAdvisorExtension extends TreeStructureAdvisor
    {
        @Override
        public Object getParent ( final Object element )
        {
            return super.getParent ( element );
        }
    }

    public ConnectionContentProvider ()
    {
        super ( new ConnectionObservableFactory (), new TreeStructureAdvisorExtension () );
    }

}
