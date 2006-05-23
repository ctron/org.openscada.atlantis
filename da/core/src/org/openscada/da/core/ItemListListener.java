package org.openscada.da.core;

import java.util.Collection;

public interface ItemListListener
{
    void changed ( Collection<DataItemInformation> added, Collection<String> removed, boolean initial );
}
