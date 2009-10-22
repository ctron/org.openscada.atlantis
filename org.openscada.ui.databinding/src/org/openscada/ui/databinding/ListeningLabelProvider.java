package org.openscada.ui.databinding;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;

/**
 * @since 1.1
 * 
 */
public class ListeningLabelProvider extends ViewerLabelProvider
{

    private final ISetChangeListener listener = new ISetChangeListener () {
        public void handleSetChange ( final SetChangeEvent event )
        {
            for ( final Iterator<?> it = event.diff.getAdditions ().iterator (); it.hasNext (); )
            {
                addListenerTo ( it.next () );
            }
            for ( final Iterator<?> it = event.diff.getRemovals ().iterator (); it.hasNext (); )
            {
                removeListenerFrom ( it.next () );
            }
        }
    };

    private final Set<IObservableSet> sources = new HashSet<IObservableSet> ();

    private boolean disposed;

    /**
     * @param itemsThatNeedLabels
     */
    public ListeningLabelProvider ( final IObservableSet itemsThatNeedLabels )
    {
        addSource ( itemsThatNeedLabels );
    }

    public ListeningLabelProvider ()
    {
    }

    protected synchronized void addSource ( final IObservableSet observableSet )
    {
        if ( observableSet == null )
        {
            return;
        }

        this.sources.add ( observableSet );
        observableSet.addSetChangeListener ( this.listener );
        for ( final Iterator<?> it = observableSet.iterator (); it.hasNext (); )
        {
            addListenerTo ( it.next () );
        }
    }

    protected synchronized void removeSource ( final IObservableSet observableSet )
    {
        if ( observableSet == null )
        {
            return;
        }

        for ( final Iterator<?> it = observableSet.iterator (); it.hasNext (); )
        {
            removeListenerFrom ( it.next () );
        }
        observableSet.removeSetChangeListener ( this.listener );

        if ( !this.disposed )
        {
            this.sources.remove ( observableSet );
        }
    }

    protected synchronized void addListenerTo ( final Object next )
    {
    }

    protected synchronized void removeListenerFrom ( final Object next )
    {
    }

    public synchronized void dispose ()
    {
        this.disposed = true;

        for ( final IObservableSet set : this.sources )
        {
            if ( !set.isDisposed () )
            {
                for ( final Iterator<?> iter = set.iterator (); iter.hasNext (); )
                {
                    removeListenerFrom ( iter.next () );
                }
            }
            set.removeSetChangeListener ( this.listener );
        }
        super.dispose ();
    }

}
