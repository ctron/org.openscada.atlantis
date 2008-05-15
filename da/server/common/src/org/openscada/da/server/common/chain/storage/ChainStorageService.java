package org.openscada.da.server.common.chain.storage;

import java.util.Map;
import java.util.Set;

import org.openscada.core.Variant;
import org.openscada.da.server.common.HiveService;

public interface ChainStorageService extends HiveService
{
    public static final String SERVICE_ID = "chainStorageService";
    
    public abstract void storeValues ( String itemId, Map<String, Variant> values );

    public abstract Map<String, Variant> loadValues ( String itemId, Set<String> valueNames );
}
