package org.openscada.da.server.opc2.job.impl;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.opc2.browser.BrowseRequest;
import org.openscada.da.server.opc2.browser.BrowseResult;
import org.openscada.da.server.opc2.browser.BrowseResultEntry;
import org.openscada.da.server.opc2.connection.OPCModel;
import org.openscada.da.server.opc2.job.JobResult;
import org.openscada.da.server.opc2.job.ThreadJob;
import org.openscada.opc.dcom.da.OPCBROWSEDIRECTION;
import org.openscada.opc.dcom.da.OPCBROWSETYPE;
import org.openscada.opc.dcom.da.impl.OPCBrowseServerAddressSpace;
import org.openscada.opc.lib.da.browser.Access;

public class BrowseJob extends ThreadJob implements JobResult<BrowseResult>
{

    private OPCModel model;

    private BrowseResult result;

    private int batchSize = 100;

    private BrowseRequest request;

    public BrowseJob ( long timeout, OPCModel model, BrowseRequest request )
    {
        super ( timeout );
        this.model = model;
        this.request = request;
    }

    @Override
    protected void perform () throws Exception
    {
        OPCBrowseServerAddressSpace browser = this.model.getServer ().getBrowser ();
        if ( browser == null )
        {
            return;
        }

        BrowseResult result = new BrowseResult ();
        int accessMask = Access.READ.getCode () | Access.WRITE.getCode ();

        // move in position
        browser.changePosition ( null, OPCBROWSEDIRECTION.OPC_BROWSE_TO );
        for ( String path : this.request.getPath () )
        {
            browser.changePosition ( path, OPCBROWSEDIRECTION.OPC_BROWSE_DOWN );
        }

        // get the branches 
        result.setBranches ( browser.browse ( OPCBROWSETYPE.OPC_BRANCH, "", accessMask, JIVariant.VT_EMPTY ).asCollection ( batchSize ) );

        // get the leaves
        Collection<String> readLeaves = browser.browse ( OPCBROWSETYPE.OPC_LEAF, "", Access.READ.getCode (), JIVariant.VT_EMPTY ).asCollection ( batchSize );
        Collection<String> writeLeaves = browser.browse ( OPCBROWSETYPE.OPC_LEAF, "", Access.WRITE.getCode (), JIVariant.VT_EMPTY ).asCollection ( batchSize );
        processLeaves ( result, browser, readLeaves, writeLeaves );

        // result
        this.result = result;
    }

    private void processLeaves ( BrowseResult result, OPCBrowseServerAddressSpace browser, Collection<String> readLeaves, Collection<String> writeLeaves ) throws JIException
    {
        Map<String, BrowseResultEntry> leavesResult = new HashMap<String, BrowseResultEntry> ();

        // add read leaves
        for ( String leaf : readLeaves )
        {
            BrowseResultEntry entry = new BrowseResultEntry ();
            entry.setEntryName ( leaf );
            String itemId = browser.getItemID ( leaf );
            entry.setItemId ( itemId );
            entry.setIoDirections ( EnumSet.of ( IODirection.INPUT ) );

            leavesResult.put ( leaf, entry );
        }

        // add write leaves
        for ( String leaf : writeLeaves )
        {
            BrowseResultEntry entry = leavesResult.get ( leaf );
            if ( entry != null )
            {
                entry.getIoDirections ().add ( IODirection.OUTPUT );
            }
            else
            {
                entry = new BrowseResultEntry ();
                entry.setEntryName ( leaf );
                String itemId = browser.getItemID ( leaf );
                entry.setItemId ( itemId );
                entry.setIoDirections ( EnumSet.of ( IODirection.OUTPUT ) );

                leavesResult.put ( leaf, entry );
            }
        }

        result.setLeaves ( leavesResult.values () );
    }

    public BrowseResult getResult ()
    {
        return result;
    }

}
