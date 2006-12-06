package org.openscada.da.client.test.views.realtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.da.client.test.impl.HiveItem;

public class ListData implements Observer
{
    private static Logger _log = Logger.getLogger ( ListData.class );
    
    private List<ListEntry> _items = new LinkedList<ListEntry> ();
    private Set<Listener> _listeners = new HashSet<Listener> ();

    synchronized public List<ListEntry> getItems ()
    {
        return new ArrayList<ListEntry> ( _items );
    }

    synchronized public void setItems ( List<ListEntry> items )
    {
        clear ();
        
        _items = items;
        fireAdded ( _items.toArray ( new ListEntry[_items.size ()] ) );
        for ( ListEntry entry : items )
        {
            entry.addObserver ( this );
        }
    }
    
    synchronized public void add ( ListEntry entry )
    {
        if ( _items.add ( entry ) )
        {
            fireAdded ( new ListEntry[] { entry } );
            entry.addObserver ( this );
        }
    }

    synchronized public void add ( HiveItem hiveItem )
    {
        ListEntry item = new ListEntry ();
        item.setDataItem ( hiveItem );
        
        add ( item );
    }
    
    synchronized public void remove ( ListEntry entry )
    {
        if ( _items.remove ( entry ) )
        {
            entry.deleteObserver ( this );
            fireRemoved ( new ListEntry[] { entry } );
        }
    }
    
    synchronized public void clear ()
    {
        for ( ListEntry entry : _items )
        {
            entry.deleteObserver ( this );
        }
        _items.clear ();
        fireRemoved ( _items.toArray ( new ListEntry[_items.size ()] ) );
    }
    
    synchronized public void addListener ( Listener listener )
    {
        _listeners.add ( listener );
        
        // now fill the new listener with what we already have
        if ( !_items.isEmpty () )
            listener.added ( _items.toArray ( new ListEntry[_items.size ()] ) );
    }
    
    synchronized public void removeListener ( Listener listener )
    {
        _listeners.remove ( listener );
    }
    
    synchronized protected void fireAdded ( ListEntry [] entries )
    {
        _log.debug ( String.format ( "Fire add for %d items", entries.length ) );
        for ( Listener listener : _listeners )
        {
            try
            {
                listener.added ( entries );
            }
            catch ( Exception e )
            {
                _log.warn ( "Failed while sending add notification", e );
            }
        }
    }
    
    synchronized protected void fireRemoved ( ListEntry [] entries )
    {
        for ( Listener listener : _listeners )
        {
            try
            {
                listener.removed ( entries );
            }
            catch ( Exception e )
            {
                _log.warn ( "Failed while sending remove notification", e );
            }
        }
    }
    
    synchronized protected void fireUpdated ( ListEntry [] entries )
    {
        _log.debug ( "Updating items: " + entries.length );
        
        for ( Listener listener : _listeners )
        {
            try
            {
                listener.updated ( entries );
            }
            catch ( Exception e )
            {
                _log.warn ( "Failed while sending update notification", e );
            }
        }
    }

    synchronized public void update ( Observable o, Object arg )
    {
        if ( (o instanceof ListEntry) && _items.contains ( o ) )
        {
            fireUpdated ( new ListEntry[] { (ListEntry)o } );
        }
    }
}
