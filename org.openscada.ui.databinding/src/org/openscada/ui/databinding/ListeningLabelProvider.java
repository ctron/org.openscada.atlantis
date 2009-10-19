package org.openscada.ui.databinding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1
 * 
 */
public class ListeningLabelProvider extends ViewerLabelProvider
{

    private final static Logger logger = LoggerFactory.getLogger ( ListeningLabelProvider.class );

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

    private final Set<IObservableSet> items = new HashSet<IObservableSet> ();

    private final IObservableFactory factory;

    private final Map<Object, IObservableSet> itemMap = new HashMap<Object, IObservableSet> ();

    /**
     * @param itemsThatNeedLabels
     * @param connectionObservableFactory 
     */
    public ListeningLabelProvider ( final IObservableSet itemsThatNeedLabels, final IObservableFactory factory )
    {
        this ( factory );
        addSource ( itemsThatNeedLabels );
    }

    public ListeningLabelProvider ( final IObservableFactory factory )
    {
        this.factory = factory;
    }

    protected synchronized void addSource ( final IObservableSet observableSet )
    {
        if ( observableSet == null )
        {
            return;
        }

        this.items.add ( observableSet );
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
        this.items.remove ( observableSet );
    }

    protected synchronized void addListenerTo ( final Object next )
    {
        logger.debug ( "Add listener to: {}", next ); //$NON-NLS-1$

        final IObservableSet set = (IObservableSet)this.factory.createObservable ( next );

        if ( set != null )
        {
            this.itemMap.put ( next, set );
            addSource ( set );
        }
    }

    protected synchronized void removeListenerFrom ( final Object next )
    {
        logger.debug ( "Remove listener from: {}", next ); //$NON-NLS-1$
        removeSource ( this.itemMap.remove ( next ) );
    }

    public synchronized void dispose ()
    {
        for ( final IObservableSet set : this.items )
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
