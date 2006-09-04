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
import org.openscada.core.Variant;

public class TimedGeneratorMemoryQuery implements Query, ListeningQuery, InitialEventsProvider, Runnable
{
    private Set<Event> _events = new HashSet<Event> ();
    
    private Set<PushEventReader> _subscriptionReaders = new HashSet<PushEventReader> ();
    
    private Thread _runner = new Thread ( this );
    
    public TimedGeneratorMemoryQuery ()
    {
        _runner.setDaemon ( true );
        _runner.start ();
    }
    
    synchronized public Reader createReader ()
    {
        return new CollectionReader ( new ArrayList<Event> ( _events ) );
    }

    synchronized public SubscriptionReader createSubscriptionReader ( int archiveSet )
    {
        QueueSubscriptionReader reader = new QueueSubscriptionReader ( this, this, archiveSet );
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
        _events.add ( event );
    }
    
    /* (non-Javadoc)
     * @see org.openscada.ae.storage.test.ListeningQuery#notifyClose(org.openscada.ae.storage.common.SubscriptionReader)
     */
    synchronized public void notifyClose ( SubscriptionReader reader )
    {
        _subscriptionReaders.remove ( reader );
    }

    synchronized public Event [] getInitialEvents ()
    {
        return _events.toArray ( new Event [ _events.size () ] );
    }

    public void run ()
    {
        while ( true )
        {
            long ts = System.currentTimeMillis ();
            Event event = new Event ( toString () + "." + ts );
            event.getAttributes ().put ( "test", new Variant ( "Just a test" ) );
            event.getAttributes ().put ( "ts", new Variant ( ts ) );
            submitEvent ( event );
            
            try
            {
                Thread.sleep ( 2 * 1000 );
            }
            catch ( InterruptedException e )
            {
            }
        }
    }

}
