package org.openscada.ae.storage.test;

import org.openscada.ae.core.EventInformation;

public interface PushEventReader
{
    public abstract void pushEvent ( EventInformation event );
}
