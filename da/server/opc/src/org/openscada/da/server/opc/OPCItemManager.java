package org.openscada.da.server.opc;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.browser.common.query.InvisibleStorage;
import org.openscada.da.core.browser.common.query.SubscribeableStorage;
import org.openscada.da.core.common.DataItemInformationBase;

public class OPCItemManager
{
    private Hive _hive = null;
    
    private Map<String,OPCItem> _itemMap = new HashMap<String, OPCItem> ();
    private Map<OPCItem,List<OPCItemDescription>> _descriptionMap = new HashMap<OPCItem, List<OPCItemDescription>> ();
    
    private InvisibleStorage _storage = new InvisibleStorage ();

    private OPCConnection _connection = null;
    
    public OPCItemManager ( OPCConnection connection, Hive hive )
    {
        super ();
        _hive = hive;
        _connection  = connection;
    }
    
    public synchronized void addItem ( OPCItem item )
    {
        _itemMap.put ( item.getId (), item );
        _descriptionMap.put ( item, new LinkedList<OPCItemDescription> () );
        _hive.registerItem ( item );
    }
    
    public synchronized void removeItem ( OPCItem item )
    {
        // first remove all item descriptions
        removeDescriptions ( item );
        
        // now remove the item itself
        _hive.unregisterItem ( item );
        _itemMap.remove ( item.getId () );
        _descriptionMap.remove ( item );
    }
    
    protected synchronized void removeDescriptions ( OPCItem item )
    {
        List<OPCItemDescription> descriptions = _descriptionMap.get ( item );
        for ( OPCItemDescription desc : descriptions )
        {
            _storage.removed ( desc.getItemDescriptor () );
        }
    }
    
    public synchronized void addItemDescription ( OPCItem item, Map<String, Variant> description )
    {
        if ( !_descriptionMap.containsKey ( item ) )
            return;
        
        OPCItemDescription desc = new OPCItemDescription ( item, description );
        _descriptionMap.get ( item ).add ( desc );
        
        _storage.added ( desc.getItemDescriptor () );
    }
    
    public synchronized OPCItem getItem ( String opcItemId, EnumSet<IODirection> ioDirection )
    {
        OPCItem item = _itemMap.get ( opcItemId );
        if ( item != null )
            return item;
        
        DataItemInformationBase di = new DataItemInformationBase ( _connection.getBaseId () + "." + opcItemId, ioDirection );
        try
        {
            item = new OPCItem ( di, _connection, opcItemId );
            addItem ( item );
            return item;
        }
        catch ( Exception e )
        {
            return null;
        }
    }
    
    /**
     * Clear all items
     *
     */
    public synchronized void clear ()
    {
        _storage.clear ();
        
        for ( Map.Entry<String,OPCItem> entry : _itemMap.entrySet () )
        {
            _hive.unregisterItem ( entry.getValue () );
        }
        
        _itemMap.clear ();
        _descriptionMap.clear ();
    }
    
    public SubscribeableStorage getStorage ()
    {
        return _storage;
    }
}
