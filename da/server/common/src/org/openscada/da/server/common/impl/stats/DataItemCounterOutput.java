package org.openscada.da.server.common.impl.stats;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.impl.HiveCommon;

public class DataItemCounterOutput implements CounterOutput
{
    private DataItemInputChained _valueItem;
    private DataItemInputChained _totalItem;
    
    public DataItemCounterOutput ( String itemId )
    {
        _valueItem = new DataItemInputChained ( itemId + ".average" );
        _totalItem = new DataItemInputChained ( itemId + ".total" );
    }
    
    public void register ( HiveCommon hive, FolderCommon rootFolder, String description )
    {
        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        
        hive.registerItem ( _valueItem );
        attributes.put ( "description", new Variant ( description + " - Average value" ) );
        rootFolder.add ( _valueItem.getInformation ().getName (), _valueItem, attributes );
        
        attributes.clear ();
        
        hive.registerItem ( _totalItem );
        attributes.put ( "description", new Variant ( description + " - Total counter" ) );
        rootFolder.add ( _totalItem.getInformation ().getName (), _totalItem, attributes );
    }
    
    public void unregister ( HiveCommon hive, FolderCommon rootFolder )
    {
        rootFolder.remove ( _valueItem );
        hive.unregisterItem ( _valueItem );
        
        rootFolder.remove ( _totalItem );
        hive.unregisterItem ( _totalItem );

    }
    
    public void setTickValue ( double average , long total )
    {
        _valueItem.updateValue ( new Variant ( average ) );
        _totalItem.updateValue ( new Variant ( total ) );
    }

}
