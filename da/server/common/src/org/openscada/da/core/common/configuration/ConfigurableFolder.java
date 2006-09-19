package org.openscada.da.core.common.configuration;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.browser.common.Folder;
import org.openscada.da.core.common.DataItem;

public interface ConfigurableFolder extends Folder
{

    public abstract boolean add ( String name, Folder folder, Map<String, Variant> attributes );

    public abstract boolean add ( String name, DataItem item, Map<String, Variant> attributes );

}