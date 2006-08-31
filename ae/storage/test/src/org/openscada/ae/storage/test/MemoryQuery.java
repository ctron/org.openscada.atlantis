package org.openscada.ae.storage.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openscada.ae.core.Event;
import org.openscada.ae.core.EventInformation;
import org.openscada.ae.storage.common.Query;
import org.openscada.ae.storage.common.Reader;
import org.openscada.ae.storage.common.SubscriptionReader;

public class MemoryQuery implements Query, ListeningQuery, InitialEventsProvider
{
    private Set<Event> _events = new HashSet<Event> ();
    
    private Set<PushEventReader> _subscriptionReaders = new HashSet<PushEventReader> ();
    
    synchronized public Reader createReader ()
    {
        return new CollectionReader ( new ArrayList<Event> ( _events ) );
    }

    synchronized public SubscriptionReader createSubscriptionReader ( int archiveSet )
    {
        QueueSubscriptionReader reader = new QueueSubscriptionReader ( this, this );
        _subscriptionReaders.add ( reader );
        return reader;
    }

    synchronized public void submitEvent ( Event event )
    {
        EventInformation eventInformation = new EventInformation ( event, EventInformation.ACTION_ADDED );
        
        for ( PushEventReader reader : _subscriptionReaders )
        {
            reader.pushEvent ( eventInformation );
        }
    }
    
    /* (non-Javadoc)
     * @see org.openscada.ae.storage.test.ListeningQuery#notifyClose(org.openscada.ae.storage.common.SubscriptionReader)
     */
    synchronized public void notifyClose ( SubscriptionReader reader )
    {
        _subscriptionReaders.remove ( reader );
    }

    synchronized public Collection<Event> getInitialEvents ()
    {
        return new ArrayList<Event> ( _events );
    }

}
