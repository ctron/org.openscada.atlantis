package org.openscada.hd.ui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.openscada.hd.QueryListener;
import org.openscada.hd.ui.data.QueryBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class QueryViewPart extends ViewPart implements QueryListener
{

    private final static Logger logger = LoggerFactory.getLogger ( QueryViewPart.class );

    protected volatile QueryBuffer query;

    private ISelectionListener selectionListener;

    @Override
    public void dispose ()
    {
        removeListener ();
        super.dispose ();
    }

    protected void addListener ()
    {
        if ( this.selectionListener == null )
        {
            getViewSite ().getWorkbenchWindow ().getSelectionService ().addSelectionListener ( this.selectionListener = new ISelectionListener () {

                public void selectionChanged ( final IWorkbenchPart part, final ISelection selection )
                {
                    QueryViewPart.this.setSelection ( selection );
                }
            } );
        }
    }

    protected void removeListener ()
    {
        if ( this.selectionListener != null )
        {
            getViewSite ().getWorkbenchWindow ().getSelectionService ().removeSelectionListener ( this.selectionListener );
            this.selectionListener = null;
        }
    }

    protected QueryBuffer getQueryFromSelection ( final ISelection selection )
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
        if ( o instanceof QueryBuffer )
        {
            return (QueryBuffer)o;
        }
        return null;
    }

    protected void setSelection ( final ISelection selection )
    {
        final QueryBuffer query = getQueryFromSelection ( selection );
        if ( query != this.query )
        {
            clear ();
            if ( query != null )
            {
                setQuery ( query );
            }
        }
    }

    protected void setQuery ( final QueryBuffer query )
    {
        logger.info ( "Setting query: {}", query ); //$NON-NLS-1$

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
