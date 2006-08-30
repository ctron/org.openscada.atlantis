package org.openscada.ae.storage.common.test;


import org.junit.Assert;
import org.junit.Test;
import org.openscada.ae.core.Event;
import org.openscada.ae.core.NoSuchQueryException;
import org.openscada.ae.storage.common.Reader;
import org.openscada.core.InvalidSessionException;

public class ReaderTest extends BaseTest
{
    @Test
    public void performRead () throws InvalidSessionException, NoSuchQueryException
    {
        Event[] events = _storage.read ( _session, "test1" );
        Event[] expectedEvents = {
                new Event ( "1" ),
                new Event ( "2" ),
        };
        
        Assert.assertEquals ( expectedEvents, events );
    }
}
