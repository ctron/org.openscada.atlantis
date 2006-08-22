package org.openscada.da.core.common.chained.test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.common.ItemListener;
import org.openscada.da.core.data.Variant;

public class TestItemListener implements ItemListener
{
    private static Logger _log = Logger.getLogger ( TestItemListener.class );
    
    private List<EventEntry> _events = new LinkedList<EventEntry> ();
    
    public void attributesChanged ( DataItem item, Map<String, Variant> attributes )
    {
        _log.debug ( String.format ( "Attributes changed for %s: size %d", item.getInformation ().getName (), attributes.size () ) );
        _events.add ( new EventEntry ( item, null, attributes ) );
    }

    public void valueChanged ( DataItem item, Variant variant )
    {
        _log.debug ( String.format ( "Value changed for %s: %s", item.getInformation ().getName (), variant.asString ( "<null>" ) ) );
        _events.add ( new EventEntry ( item, variant, null ) );
    }
    
    public void assertEquals ( EventEntry [] events )
    {
        Assert.assertEquals ( "Events are not the same", events, _events.toArray ( new EventEntry[_events.size ()] ) );
    }
    
    public void assertEquals ( Collection<EventEntry> events )
    {
        Assert.assertEquals ( "Events are not the same", events.toArray ( new EventEntry[events.size ()] ), _events.toArray ( new EventEntry[_events.size ()] ) );
    }

}
