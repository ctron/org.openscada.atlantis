package org.openscada.da.server.common.impl.stats;

import org.openscada.core.Variant;
import org.openscada.da.core.server.Session;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.impl.SessionCommon;


public interface HiveEventListener
{

    public abstract void sessionCreated ( SessionCommon session );

    public abstract void sessionDestroyed ( SessionCommon session );

    public abstract void itemRegistered ( DataItem item );

    public abstract void startWriteAttributes ( Session session, String itemId, int size );

    public abstract void startWrite ( Session session, String itemName, Variant value );

    public abstract void attributesChanged ( DataItem item, int size );

    public abstract void valueChanged ( DataItem item, Variant variant, boolean cache );

    public abstract void itemUnregistered ( DataItem item );

}
