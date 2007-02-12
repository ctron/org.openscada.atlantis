package org.openscada.core.subscription;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscada.core.subscription.SubscriptionManager;
import org.openscada.core.subscription.SubscriptionState;

public class Test1
{
    private SubscriptionManager _manager = null;

    @Before
    public void setup ()
    {
        _manager = new SubscriptionManager ();
    }

    @After
    public void cleanup ()
    {
        _manager = null;
    }

    /**
     * Perfom a simple subscribe/unsubscribe without a subscription source attached
     * @throws Exception
     */
    @Test
    public void test1 () throws Exception
    {
        SubscriptionRecorder recorder = new SubscriptionRecorder ();

        _manager.subscribe ( "", recorder );
        _manager.unsubscribe ( "", recorder );

        Assert.assertEquals ( "Events are not the same", new SubscriptionStateEvent[] {
                new SubscriptionStateEvent ( SubscriptionState.GRANTED ),
                new SubscriptionStateEvent ( SubscriptionState.DISCONNECTED )
        }, recorder.getList ().toArray ( new SubscriptionStateEvent[0] ) );
        
        Assert.assertEquals ( "Number of subscriptions does not match", 0, _manager.getSubscriptionCount () );
    }
    
    /**
     * Perform a subscribe/set/unset/unsubscribe sequence
     * @throws Exception
     */
    @Test
    public void test2 () throws Exception
    {
        SubscriptionRecorder recorder = new SubscriptionRecorder ();
        TestSubscriptionSource source = new TestSubscriptionSource ();

        _manager.subscribe ( "", recorder );
        _manager.setSource ( "", source );
        _manager.setSource ( "", null );
        _manager.unsubscribe ( "", recorder );

        Assert.assertEquals ( "Events are not the same", new Object[] {
                new SubscriptionStateEvent ( SubscriptionState.GRANTED ),
                new SubscriptionStateEvent ( SubscriptionState.CONNECTED ),
                new SubscriptionSourceEvent ( true, source ),
                new SubscriptionSourceEvent ( false, source ),
                new SubscriptionStateEvent ( SubscriptionState.GRANTED ),
                new SubscriptionStateEvent ( SubscriptionState.DISCONNECTED )
        }, recorder.getList ().toArray ( new Object[0] ) );
        
        Assert.assertEquals ( "Number of subscriptions does not match", 0, _manager.getSubscriptionCount () );
    }
    
    /**
     * Perform a common subscribe/unsubscribe with a subscriptions source being present
     * before the subscription.
     * @throws Exception
     */
    @Test
    public void test3 () throws Exception
    {
        SubscriptionRecorder recorder = new SubscriptionRecorder ();
        TestSubscriptionSource source = new TestSubscriptionSource ();

        _manager.setSource ( "", source );
        _manager.subscribe ( "", recorder );        
        _manager.unsubscribe ( "", recorder );
        _manager.setSource ( "", null );

        Assert.assertEquals ( "Events are not the same", new Object[] {
                new SubscriptionStateEvent ( SubscriptionState.CONNECTED ),
                new SubscriptionSourceEvent ( true, source ),
                new SubscriptionSourceEvent ( false, source ),
                new SubscriptionStateEvent ( SubscriptionState.DISCONNECTED )
        }, recorder.getList ().toArray ( new Object[0] ) );
        
        Assert.assertEquals ( "Number of subscriptions does not match", 0, _manager.getSubscriptionCount () );
    }
    
    /**
     * Perform a common subscribe/unsubscribe with a subscriptions source being present
     * before the subscription but loosing the subscription source while beeing
     * connected.
     * @throws Exception
     */
    @Test
    public void test4 () throws Exception
    {
        SubscriptionRecorder recorder = new SubscriptionRecorder ();
        TestSubscriptionSource source = new TestSubscriptionSource ();

        _manager.setSource ( "", source );
        _manager.subscribe ( "", recorder );
        _manager.setSource ( "", null );
        _manager.unsubscribe ( "", recorder );

        Assert.assertEquals ( "Events are not the same", new Object[] {
                new SubscriptionStateEvent ( SubscriptionState.CONNECTED ),
                new SubscriptionSourceEvent ( true, source ),
                new SubscriptionSourceEvent ( false, source ),
                new SubscriptionStateEvent ( SubscriptionState.GRANTED ),
                new SubscriptionStateEvent ( SubscriptionState.DISCONNECTED )
        }, recorder.getList ().toArray ( new Object[0] ) );
        
        Assert.assertEquals ( "Number of subscriptions does not match", 0, _manager.getSubscriptionCount () );
    }
}
