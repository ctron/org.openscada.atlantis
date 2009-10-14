package org.openscada.core.ui.connection.commands;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.openscada.core.ui.connection.data.ConnectionHolder;
import org.openscada.ui.databinding.AbstractSelectionHandler;
import org.openscada.ui.databinding.AdapterHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConnectionHandler extends AbstractSelectionHandler
{
    private static final Logger logger = LoggerFactory.getLogger ( AbstractConnectionHandler.class );

    protected Collection<ConnectionHolder> getConnections ()
    {
        final Collection<ConnectionHolder> result = new LinkedList<ConnectionHolder> ();

        final IStructuredSelection sel = getSelection ();

        if ( sel != null && !sel.isEmpty () )
        {
            for ( final Iterator<?> i = sel.iterator (); i.hasNext (); )
            {
                final Object o = i.next ();

                logger.debug ( "Try to add: {}", o.getClass () );

                final ConnectionHolder holder = (ConnectionHolder)AdapterHelper.adapt ( o, ConnectionHolder.class );
                if ( holder != null )
                {
                    result.add ( holder );
                }
            }
        }

        return result;
    }

}