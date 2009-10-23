package org.openscada.ae.ui.testing.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.client.ConditionListener;
import org.openscada.ae.ui.connection.data.BrowserEntryBean;
import org.openscada.core.subscription.SubscriptionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEntryViewPart extends ViewPart
{

    private final class ConditionListenerImpl implements ConditionListener
    {
        private boolean disposed = false;

        public synchronized void dispose ()
        {
            this.disposed = true;
        }

        public synchronized void statusChanged ( final SubscriptionState state )
        {
            if ( !this.disposed )
            {
                AbstractEntryViewPart.this.handleStatusChanged ( state );
            }
        }

        public synchronized void dataChanged ( final ConditionStatusInformation[] addedOrUpdated, final String[] removed )
        {
            if ( !this.disposed )
            {
                AbstractEntryViewPart.this.handleDataChanged ( addedOrUpdated, removed, false );
            }
        }
    }

    private final static Logger logger = LoggerFactory.getLogger ( AbstractEntryViewPart.class );

    protected BrowserEntryBean entry;

    private ISelectionListener selectionListener;

    private ConditionListenerImpl listener;

    @Override
    public void dispose ()
    {
        removeSelectionListener ();
        super.dispose ();
    }

    protected void addSelectionListener ()
    {
        if ( this.selectionListener == null )
        {
            getViewSite ().getWorkbenchWindow ().getSelectionService ().addSelectionListener ( this.selectionListener = new ISelectionListener () {

                public void selectionChanged ( final IWorkbenchPart part, final ISelection selection )
                {
                    AbstractEntryViewPart.this.setSelection ( selection );
                }
            } );
        }
    }

    protected void removeSelectionListener ()
    {
        if ( this.selectionListener != null )
        {
            getViewSite ().getWorkbenchWindow ().getSelectionService ().removeSelectionListener ( this.selectionListener );
            this.selectionListener = null;
        }
    }

    protected BrowserEntryBean getQueryFromSelection ( final ISelection selection )
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
        if ( o instanceof BrowserEntryBean )
        {
            return (BrowserEntryBean)o;
        }
        return null;
    }

    protected synchronized void setSelection ( final ISelection selection )
    {
        final BrowserEntryBean query = getQueryFromSelection ( selection );
        if ( query != this.entry )
        {
            clear ();
            if ( query != null )
            {
                setQuery ( query );
            }
        }
    }

    protected void setQuery ( final BrowserEntryBean query )
    {
        logger.info ( "Setting entry: {}", query ); //$NON-NLS-1$

        this.entry = query;
        if ( this.listener != null )
        {
            this.listener.dispose ();
            this.listener = null;
        }
        this.entry.getConnection ().getConnection ().setConditionListener ( this.entry.getEntry ().getId (), this.listener = new ConditionListenerImpl () );
    }

    protected abstract void handleDataChanged ( final ConditionStatusInformation[] addedOrUpdated, final String[] removed, final boolean full );

    protected abstract void handleStatusChanged ( final SubscriptionState state );

    protected void clear ()
    {
        if ( this.entry != null )
        {
            if ( this.listener != null )
            {
                this.listener.dispose ();
                this.listener = null;
            }
            this.entry.getConnection ().getConnection ().setConditionListener ( this.entry.getEntry ().getId (), null );
            this.entry = null;
        }
    }
}
