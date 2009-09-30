package org.openscada.hd.ui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.part.ViewPart;
import org.openscada.hd.QueryListener;
import org.openscada.hd.ui.data.QueryBufferBean;

public abstract class QueryViewPart extends ViewPart implements QueryListener
{

    protected QueryBufferBean query;

    protected QueryBufferBean getQueryFromSelection ( final ISelection selection )
    {
        if ( selection.isEmpty () )
        {
            return null;
        }
        if ( ! ( selection instanceof IStructuredSelection ) )
        {
            return null;
        }
        final Object o = ( (IStructuredSelection)selection ).getFirstElement ();
        if ( o instanceof QueryBufferBean )
        {
            return (QueryBufferBean)o;
        }
        return null;
    }

    protected void setSelection ( final ISelection selection )
    {
        final QueryBufferBean query = getQueryFromSelection ( selection );
        if ( query != this.query )
        {
            clear ();
            if ( query != null )
            {
                setQuery ( query );
            }
        }
    }

    protected void setQuery ( final QueryBufferBean query )
    {
        this.query = query;
        this.query.addQueryListener ( this );
    }

    protected void clear ()
    {
        if ( this.query != null )
        {
            this.query.removeQueryListener ( this );
            this.query = null;
        }
    }

}
