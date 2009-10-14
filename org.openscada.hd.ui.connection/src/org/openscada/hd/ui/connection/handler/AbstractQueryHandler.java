package org.openscada.hd.ui.connection.handler;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.openscada.hd.ui.connection.internal.QueryBufferBean;
import org.openscada.ui.databinding.AbstractSelectionHandler;
import org.openscada.ui.databinding.AdapterHelper;

public abstract class AbstractQueryHandler extends AbstractSelectionHandler
{
    protected Collection<QueryBufferBean> getQueries ()
    {
        final Collection<QueryBufferBean> result = new LinkedList<QueryBufferBean> ();

        final IStructuredSelection sel = getSelection ();

        if ( sel != null && !sel.isEmpty () )
        {
            for ( final Iterator<?> i = sel.iterator (); i.hasNext (); )
            {
                final Object o = i.next ();

                final QueryBufferBean item = (QueryBufferBean)AdapterHelper.adapt ( o, QueryBufferBean.class );
                if ( item != null )
                {
                    result.add ( item );
                }
            }
        }

        return result;
    }

}