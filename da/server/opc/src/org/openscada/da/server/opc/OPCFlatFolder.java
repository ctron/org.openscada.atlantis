package org.openscada.da.server.opc;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.jinterop.dcom.common.JIException;
import org.openscada.core.Variant;
import org.openscada.opc.lib.da.browser.FlatBrowser;

public class OPCFlatFolder extends OPCBaseFolder
{

    private FlatBrowser _browser;

    public OPCFlatFolder ( OPCItemManager itemManager, FlatBrowser browser )
    {
        super ( itemManager );
        _browser = browser;
    }

    @Override
    protected void fill () throws IllegalArgumentException, UnknownHostException, JIException
    {
        for ( String opcItemId : _browser.browse () )
        {
            OPCItem item = _itemManager.getItem ( opcItemId );
            
            Map<String, Variant> attributes = new HashMap<String, Variant> ();
            attributes.put ( "opc.item-id", new Variant ( opcItemId ) );
            _folder.add ( opcItemId, item, attributes );
        }
    }

}
