package org.eclipse.scada.ae.filter.test;

import java.util.Date;
import java.util.UUID;

import org.eclipse.scada.ae.Event;
import org.eclipse.scada.ae.Event.EventBuilder;
import org.eclipse.scada.ae.Event.Fields;
import org.eclipse.scada.ae.filter.internal.EventMatcherImpl;
import org.junit.Assert;
import org.junit.Test;

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
