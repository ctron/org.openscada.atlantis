package org.openscada.da.core;

import java.util.Collection;

public interface ItemListListener
{
    void changed ( Collection<String> added, Collection<String> removed, boolean initial );
}
