package org.openscada.ae.storage.common.data;

import org.openscada.ae.core.Event;

public interface Reader
{
    Event [] readNext ( int maxCount );
}
