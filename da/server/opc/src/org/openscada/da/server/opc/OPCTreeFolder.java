package org.openscada.da.server.opc;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.opc.lib.da.browser.Branch;
import org.openscada.opc.lib.da.browser.Leaf;
import org.openscada.opc.lib.da.browser.TreeBrowser;

public class OPCTreeFolder extends OPCBaseFolder
{
    private static Logger _log = Logger.getLogger ( OPCTreeFolder.class );

    private TreeBrowser _browser;
    private Branch _branch;

    public OPCTreeFolder ( OPCItemManager itemManager, TreeBrowser browser, Branch branch )
    {
        super ( itemManager );
        _browser = browser;
        _branch = branch;
    }

    @Override
    protected void fill ()
    {
        try
        {
            synchronized ( _browser )
            {
                _browser.fillBranches ( _branch );
                _browser.fillLeaves ( _branch );
            }

            for ( Leaf leaf : _branch.getLeaves () )
            {
                OPCItem item = _itemManager.getItem ( leaf.getItemId () );
                if ( item != null )
                {
                    Map<String, Variant> attributes = new HashMap<String, Variant> ();
                    _folder.add ( leaf.getName (), item, attributes );
                }
            }
            for ( Branch branch : _branch.getBranches () )
            {
                Map<String, Variant> attributes = new HashMap<String, Variant> ();
                attributes.put ( "opc.branch.name", new Variant ( branch.getName () ) );
                _folder.add ( branch.getName (), new OPCTreeFolder ( _itemManager, _browser, branch ), attributes );
            }
        }
        catch ( Throwable e )
        {
            _log.warn ( "Failed to browser", e );
        }
    }
}
