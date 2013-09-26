/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.core.subscription;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.scada.core.data.SubscriptionState;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class Test1
{
    private SubscriptionManager manager = null;

    @Before
    public void setup ()
    {
        this.manager = new SubscriptionManager ();
    }

    @After
    public void cleanup ()
    {
        this.manager = null;
    }

    /**
     * Perform a simple subscribe/unsubscribe without a subscription source attached
     * @throws Exception
     */
    @Test
    public void test1 () throws Exception
    {
        final SubscriptionRecorder recorder = new SubscriptionRecorder ();

        this.manager.subscribe ( "", recorder );
        this.manager.unsubscribe ( "", recorder );

        Assert.assertArrayEquals ( "Events are not the same", new SubscriptionStateEvent[] { new SubscriptionStateEvent ( SubscriptionState.GRANTED ), new SubscriptionStateEvent ( SubscriptionState.DISCONNECTED ) }, recorder.getList ().toArray ( new SubscriptionStateEvent[0] ) );

        Assert.assertEquals ( "Number of subscriptions does not match", 0, this.manager.getSubscriptionCount () );
    }

    /**
     * Perform a subscribe/set/unset/unsubscribe sequence
     * @throws Exception
     */
    @Test
    public void test2 () throws Exception
    {
        final SubscriptionRecorder recorder = new SubscriptionRecorder ();
        final SubscriptionSourceTestImpl source = new SubscriptionSourceTestImpl ();

        this.manager.subscribe ( "", recorder );
        this.manager.setSource ( "", source );
        this.manager.setSource ( "", null );
        this.manager.unsubscribe ( "", recorder );

        Assert.assertArrayEquals ( "Events are not the same", new Object[] { new SubscriptionStateEvent ( SubscriptionState.GRANTED ), new SubscriptionStateEvent ( SubscriptionState.CONNECTED ), new SubscriptionSourceEvent ( true, source ), new SubscriptionSourceEvent ( false, source ), new SubscriptionStateEvent ( SubscriptionState.GRANTED ), new SubscriptionStateEvent ( SubscriptionState.DISCONNECTED ) }, recorder.getList ().toArray ( new Object[0] ) );

        Assert.assertEquals ( "Number of subscriptions does not match", 0, this.manager.getSubscriptionCount () );
    }

    /**
     * Perform a common subscribe/unsubscribe with a subscriptions source being present
     * before the subscription.
     * @throws Exception
     */
    @Test
    public void test3 () throws Exception
    {
        final SubscriptionRecorder recorder = new SubscriptionRecorder ();
        final SubscriptionSourceTestImpl source = new SubscriptionSourceTestImpl ();

        this.manager.setSource ( "", source );
        this.manager.subscribe ( "", recorder );
        this.manager.unsubscribe ( "", recorder );
        this.manager.setSource ( "", null );

        Assert.assertArrayEquals ( "Events are not the same", new Object[] { new SubscriptionStateEvent ( SubscriptionState.CONNECTED ), new SubscriptionSourceEvent ( true, source ), new SubscriptionSourceEvent ( false, source ), new SubscriptionStateEvent ( SubscriptionState.DISCONNECTED ) }, recorder.getList ().toArray ( new Object[0] ) );

        Assert.assertEquals ( "Number of subscriptions does not match", 0, this.manager.getSubscriptionCount () );
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
        final SubscriptionRecorder recorder = new SubscriptionRecorder ();
        final SubscriptionSourceTestImpl source = new SubscriptionSourceTestImpl ();

        this.manager.setSource ( "", source );
        this.manager.subscribe ( "", recorder );
        this.manager.setSource ( "", null );
        this.manager.unsubscribe ( "", recorder );

        Assert.assertArrayEquals ( "Events are not the same", new Object[] { new SubscriptionStateEvent ( SubscriptionState.CONNECTED ), new SubscriptionSourceEvent ( true, source ), new SubscriptionSourceEvent ( false, source ), new SubscriptionStateEvent ( SubscriptionState.GRANTED ), new SubscriptionStateEvent ( SubscriptionState.DISCONNECTED ) }, recorder.getList ().toArray ( new Object[0] ) );

        Assert.assertEquals ( "Number of subscriptions does not match", 0, this.manager.getSubscriptionCount () );
    }

    /**
     * Test the method {@link SubscriptionManager#getAllGrantedTopics()}
     * @throws Exception
     */
    @SuppressWarnings ( { "unchecked", "rawtypes" } )
    @Test
    public void test5 () throws Exception
    {
        final SubscriptionRecorder recorder = new SubscriptionRecorder ();

        this.manager.subscribe ( "1", recorder );
        this.manager.subscribe ( "2", recorder );

        final List<String> topics = new LinkedList<String> ();
        topics.add ( "1" );
        topics.add ( "2" );
        Collections.sort ( topics );

        final List<Object> actualTopics = this.manager.getAllGrantedTopics ();
        Collections.sort ( (List)actualTopics );

        Assert.assertEquals ( "Topics do not match", topics, actualTopics );

        this.manager.unsubscribe ( "1", recorder );
        this.manager.unsubscribe ( "2", recorder );

        Assert.assertEquals ( "Topics do not match", new LinkedList<Object> (), this.manager.getAllGrantedTopics () );
    }
}
