package org.eclipse.scada.da.server.proxy;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import org.openscada.core.Variant;
import org.eclipse.scada.core.subscription.SubscriptionState;
import org.eclipse.scada.da.client.ItemUpdateListener;

public class RecordItemUpdateListener implements ItemUpdateListener
{

    private final Collection<Runnable> events = new LinkedList<Runnable> ();

    private HashSet<Integer> currentSequence;

    protected synchronized void addEvent ( final Runnable event )
    {
        this.events.add ( event );
    }

    public synchronized void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        final Calendar c = Calendar.getInstance ();

        try
        {
            final int intValue = value.asInteger ();
            if ( !this.currentSequence.contains ( intValue ) )
            {
                addEvent ( new Runnable () {

                    public void run ()
                    {
                        System.out.println ( String.format ( "%tc: Value: %s", c, value ) );
                    }
                } );
            }
        }
        catch ( final Throwable e )
        {
            e.printStackTrace ();
        }

    }

    public synchronized void notifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
    {
    }

    public void dump ()
    {
        for ( final Runnable r : this.events )
        {
            r.run ();
        }
    }

    public synchronized void switchTo ( final Integer... sequence )
    {
        this.currentSequence = new HashSet<Integer> ( Arrays.asList ( sequence ) );
    }
}
