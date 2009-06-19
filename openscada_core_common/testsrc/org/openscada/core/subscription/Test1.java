package org.openscada.core.subscription;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class Test1
{
    private SubscriptionManager _manager = null;

    @Before
    public void setup ()
    {
        this._manager = new SubscriptionManager ();
    }

    @After
    public void cleanup ()
    {
        this._manager = null;
    }

    /**
     * Perfom a simple subscribe/unsubscribe without a subscription source attached
     * @throws Exception
     */
    @Test
    public void test1 () throws Exception
    {
        SubscriptionRecorder recorder = new SubscriptionRecorder ();

        this._manager.subscribe ( "", recorder );
        this._manager.unsubscribe ( "", recorder );

        Assert.assertArrayEquals ( "Events are not the same", new SubscriptionStateEvent[] { new SubscriptionStateEvent ( SubscriptionState.GRANTED ), new SubscriptionStateEvent ( SubscriptionState.DISCONNECTED ) }, recorder.getList ().toArray ( new SubscriptionStateEvent[0] ) );

        Assert.assertEquals ( "Number of subscriptions does not match", 0, this._manager.getSubscriptionCount () );
    }

    /**
     * Perform a subscribe/set/unset/unsubscribe sequence
     * @throws Exception
     */
    @Test
    public void test2 () throws Exception
    {
        SubscriptionRecorder recorder = new SubscriptionRecorder ();
        SubscriptionSourceTestImpl source = new SubscriptionSourceTestImpl ();

        this._manager.subscribe ( "", recorder );
        this._manager.setSource ( "", source );
        this._manager.setSource ( "", null );
        this._manager.unsubscribe ( "", recorder );

        Assert.assertArrayEquals ( "Events are not the same", new Object[] { new SubscriptionStateEvent ( SubscriptionState.GRANTED ), new SubscriptionStateEvent ( SubscriptionState.CONNECTED ), new SubscriptionSourceEvent ( true, source ), new SubscriptionSourceEvent ( false, source ), new SubscriptionStateEvent ( SubscriptionState.GRANTED ), new SubscriptionStateEvent ( SubscriptionState.DISCONNECTED ) }, recorder.getList ().toArray ( new Object[0] ) );

        Assert.assertEquals ( "Number of subscriptions does not match", 0, this._manager.getSubscriptionCount () );
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
        SubscriptionSourceTestImpl source = new SubscriptionSourceTestImpl ();

        this._manager.setSource ( "", source );
        this._manager.subscribe ( "", recorder );
        this._manager.unsubscribe ( "", recorder );
        this._manager.setSource ( "", null );

        Assert.assertArrayEquals ( "Events are not the same", new Object[] { new SubscriptionStateEvent ( SubscriptionState.CONNECTED ), new SubscriptionSourceEvent ( true, source ), new SubscriptionSourceEvent ( false, source ), new SubscriptionStateEvent ( SubscriptionState.DISCONNECTED ) }, recorder.getList ().toArray ( new Object[0] ) );

        Assert.assertEquals ( "Number of subscriptions does not match", 0, this._manager.getSubscriptionCount () );
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
        SubscriptionSourceTestImpl source = new SubscriptionSourceTestImpl ();

        this._manager.setSource ( "", source );
        this._manager.subscribe ( "", recorder );
        this._manager.setSource ( "", null );
        this._manager.unsubscribe ( "", recorder );

        Assert.assertArrayEquals ( "Events are not the same", new Object[] { new SubscriptionStateEvent ( SubscriptionState.CONNECTED ), new SubscriptionSourceEvent ( true, source ), new SubscriptionSourceEvent ( false, source ), new SubscriptionStateEvent ( SubscriptionState.GRANTED ), new SubscriptionStateEvent ( SubscriptionState.DISCONNECTED ) }, recorder.getList ().toArray ( new Object[0] ) );

        Assert.assertEquals ( "Number of subscriptions does not match", 0, this._manager.getSubscriptionCount () );
    }

    /**
     * Test the method {@link SubscriptionManager#getAllGrantedTopics()}
     * @throws Exception
     */
    @SuppressWarnings ( "unchecked" )
    @Test
    public void test5 () throws Exception
    {
        SubscriptionRecorder recorder = new SubscriptionRecorder ();

        this._manager.subscribe ( "1", recorder );
        this._manager.subscribe ( "2", recorder );

        List<Object> topics = new LinkedList<Object> ();
        topics.add ( "1" );
        topics.add ( "2" );
        Collections.sort ( (List)topics );

        List<Object> actualTopics = this._manager.getAllGrantedTopics ();
        Collections.sort ( (List)actualTopics );

        Assert.assertEquals ( "Topics do not match", topics, actualTopics );

        this._manager.unsubscribe ( "1", recorder );
        this._manager.unsubscribe ( "2", recorder );

        Assert.assertEquals ( "Topics do not match", new LinkedList<Object> (), this._manager.getAllGrantedTopics () );
    }
}
