package org.openscada.da.server.common.impl.stats;

import org.openscada.core.Variant;
import org.openscada.da.core.server.Session;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.impl.SessionCommon;

public class HiveStatisticsGenerator implements HiveEventListener, Tickable
{
    protected CounterValue _itemsValue = new CounterValue ();
    protected CounterValue _sessionsValue = new CounterValue ();

    protected CounterValue _valueWritesCounter = new CounterValue ();
    protected CounterValue _attributeWritesCounter = new CounterValue ();
    
    protected CounterValue _valueEventsCounter = new CounterValue ();
    protected CounterValue _attributeEventsCounter = new CounterValue ();

    public void itemRegistered ( DataItem item )
    {
        _itemsValue.add ( 1 );
    }

    public void sessionCreated ( SessionCommon session )
    {
        _sessionsValue.add ( 1 );
    }

    public void sessionDestroyed ( SessionCommon session )
    {
        _sessionsValue.add ( -1 );
    }

    public void startWrite ( Session session, String itemName, Variant value )
    {
        _valueWritesCounter.add ( 1 );
    }

    public void startWriteAttributes ( Session session, String itemId, int size )
    {
        _attributeWritesCounter.add ( size );
    }

    public void attributesChanged ( DataItem item, int size )
    {
        _attributeEventsCounter.add ( size );
    }

    public void valueChanged ( DataItem item, Variant variant, boolean cache )
    {
        _valueEventsCounter.add ( 1 );
    }

    public void tick ()
    {
        _attributeWritesCounter.tick ();
        _itemsValue.tick ();
        _sessionsValue.tick ();
        _valueWritesCounter.tick ();
        _valueEventsCounter.tick ();
        _attributeEventsCounter.tick ();
    }

    public void itemUnregistered ( DataItem item )
    {
        _itemsValue.add ( -1 );
    }

}
