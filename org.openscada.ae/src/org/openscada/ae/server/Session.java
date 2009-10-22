package org.openscada.ae.server;

import org.openscada.ae.BrowserListener;

public interface Session extends org.openscada.core.server.Session
{
    public void setEventListener ( EventListener listener );

    public void setConditionListener ( ConditionListener listener );

    public void setBrowserListener ( BrowserListener listener );
}
