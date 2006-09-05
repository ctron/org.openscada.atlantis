package org.openscada.ae.storage.common.memory;

import org.openscada.ae.core.EventInformation;

public interface PushEventReader
{
    public abstract void pushEvent ( EventInformation event );
}
