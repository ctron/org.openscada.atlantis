package org.openscada.da.client.net;

import java.util.Collection;

import org.openscada.da.core.DataItemInformation;

public interface ItemListListener
{
    void changed ( Collection<DataItemInformation> added, Collection<String> removed, boolean initial );
}
