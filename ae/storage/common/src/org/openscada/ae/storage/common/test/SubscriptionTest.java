package org.openscada.ae.storage.common.test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscada.ae.core.Event;
import org.openscada.ae.core.EventInformation;
import org.openscada.ae.core.Listener;
import org.openscada.ae.core.NoSuchQueryException;
import org.openscada.core.InvalidSessionException;

public class SubscriptionTest extends BaseTest implements Listener
{

    private int _unsubscribeEvents = 0;
    private List<EventInformation> _eventInformations = new LinkedList<EventInformation> ();
    
    @Before
    public void setup () throws InvalidSessionException, NoSuchQueryException
    {
        _unsubscribeEvents = 0;
        _eventInformations = new LinkedList<EventInformation> ();
    }
    
    @After
    public void cleanup () throws InvalidSessionException, NoSuchQueryException
    {
    }
    
    @Test
    public void testUnsubscribe () throws InvalidSessionException, NoSuchQueryException
    {
        Assert.assertEquals ( _unsubscribeEvents, 0 );
        _storage.subscribe ( _session, "test1", this, 5, 0 );
        Assert.assertEquals ( _unsubscribeEvents, 0 );
        _storage.unsubscribe ( _session, "test1", this );
        Assert.assertEquals ( _unsubscribeEvents, 1 );
    }
    
    @Test
    public void testData () throws InvalidSessionException, NoSuchQueryException, InterruptedException
    {
        Assert.assertEquals ( _unsubscribeEvents, 0 );
        _storage.subscribe ( _session, "test1", this, 5, 0 );
        Assert.assertEquals ( _unsubscribeEvents, 0 );
        
        Thread.sleep ( 2 );
        _storage.unsubscribe ( _session, "test1", this );
        Assert.assertEquals ( _unsubscribeEvents, 1 );
        
        EventInformation [] eventInformation = {
                new EventInformation ( new Event ( "ev1" ), EventInformation.ACTION_ADDED ),
        };
        assertData ( eventInformation );
    }
    
    public void assertData ( EventInformation [] expectedData )
    {
        Assert.assertEquals ( expectedData.length, _eventInformations.size () );
        for ( int i = 0; i < expectedData.length; i++ )
        {
            Assert.assertEquals ( expectedData[i].getAction (), _eventInformations.get ( i ).getAction () );
            Assert.assertEquals ( expectedData[i].getEvent (), _eventInformations.get ( i ).getEvent () );
        }
    }
    
    public void events ( EventInformation[] events )
    {
        _eventInformations.addAll ( Arrays.asList ( events ) );
    }

    public void unsubscribed ( String reason )
    {
        _unsubscribeEvents++;
    }

}
