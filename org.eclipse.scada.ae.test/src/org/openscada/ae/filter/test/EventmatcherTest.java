package org.openscada.ae.filter.test;

import java.util.Date;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.Event.Fields;
import org.openscada.ae.filter.internal.EventMatcherImpl;

public class EventmatcherTest
{
    @Test
    public void testMatchesMultiple ()
    {
        EventMatcherImpl em = new EventMatcherImpl ( "(|(monitorType=R-L)(monitorType=R-HH))" );
        EventBuilder eb = Event.create ().id ( new UUID ( 0, 1 ) ).sourceTimestamp ( new Date () ).entryTimestamp ( new Date () );
        eb.attribute ( Fields.MONITOR_TYPE, "R-L" );
        Assert.assertTrue ( em.matches ( eb.build () ) );
    }
}
