package org.openscada.ae.storage.common.test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.openscada.ae.core.EventInformation;
import org.openscada.ae.storage.common.Subscription;
import org.openscada.ae.storage.common.SubscriptionObserver;
import org.openscada.ae.storage.common.SubscriptionReader;

public class MockSubscriptionReader implements SubscriptionReader
{
    
    public class Step
    {
        private int _preDelay = 0;
        private EventInformation [] _events = null;
        private int _postDelay = 0;
        
        public Step ( int preDelay, EventInformation[] events, int postDelay )
        {
            super ();
            _preDelay = preDelay;
            _events = events;
            _postDelay = postDelay;
        }
        
        public EventInformation[] getEvents ()
        {
            return _events;
        }
        public void setEvents ( EventInformation[] events )
        {
            _events = events;
        }
        public int getPostDelay ()
        {
            return _postDelay;
        }
        public void setPostDelay ( int postDelay )
        {
            _postDelay = postDelay;
        }
        public int getPreDelay ()
        {
            return _preDelay;
        }
        public void setPreDelay ( int preDelay )
        {
            _preDelay = preDelay;
        }
    }
    
    private LinkedList<Step> _steps = null;
    private List<Step> _initialSteps = null;
    
    private LinkedList<EventInformation> _eventQueue = new LinkedList<EventInformation> ();
    
    private Thread _thread = null;
    private SubscriptionObserver _observer = null;
    private Subscription _subscription = null;

    public EventInformation[] fetchNext ( int maxBatchSize )
    {
        List<EventInformation> buffer = new LinkedList<EventInformation> ();
        synchronized ( _eventQueue )
        {
            EventInformation eventInformation;
            int i = 0;
            while ( ( ( eventInformation = _eventQueue.poll () ) != null ) && ( ( i < maxBatchSize ) || ( i == 0 ) ) )
            {
                buffer.add ( eventInformation );
                i++;
            }
        }
        return buffer.toArray ( new EventInformation[buffer.size ()] );
    }

    public boolean hasMoreElements ()
    {
        if ( _steps == null )
            return false;
        
        return _steps.size () > 0;
    }

    public void open ( Subscription subscription, SubscriptionObserver observer )
    {
        if ( _thread == null )
        {
            _observer = observer;
            _subscription = subscription;
            _steps = new LinkedList<Step> ( _initialSteps );
            _thread = new Thread ( new Runnable () {

                public void run ()
                {
                    try
                    {
                        runner ();
                    }
                    catch ( InterruptedException e )
                    {
                        e.printStackTrace();
                    }
                }} );
            _thread.start ();
        }
    }
    
    private void runner () throws InterruptedException
    {
        while ( true )
        {
            Step step = null;
            synchronized ( this )
            {
                if ( _steps == null)
                    return;
                step = _steps.poll ();
                if ( step == null )
                    return;
                
                if ( step.getPreDelay () > 0 )
                    Thread.sleep ( step.getPreDelay () );
                
                if ( step.getEvents () != null )
                {
                    synchronized ( _eventQueue )
                    {
                        _eventQueue.addAll ( Arrays.asList ( step.getEvents () ) );
                    }
                    _observer.changed ( _subscription );
                }
                
                if ( step.getPostDelay () > 0 )
                    Thread.sleep ( step.getPostDelay () );   
            }
        }
    }

    synchronized public void close ()
    {
        _steps = null;
    }

    public void setInitialSteps ( List<Step> initialSteps )
    {
        _initialSteps = initialSteps;
    }
    
}
