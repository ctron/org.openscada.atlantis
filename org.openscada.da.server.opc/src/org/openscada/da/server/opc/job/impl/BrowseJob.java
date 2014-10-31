/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.opc.job.impl;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.scada.da.data.IODirection;
import org.eclipse.scada.utils.str.StringHelper;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.da.server.opc.browser.BrowseRequest;
import org.openscada.da.server.opc.browser.BrowseResult;
import org.openscada.da.server.opc.browser.BrowseResultEntry;
import org.openscada.da.server.opc.connection.OPCModel;
import org.openscada.da.server.opc.job.JobResult;
import org.openscada.da.server.opc.job.ThreadJob;
import org.openscada.opc.dcom.da.OPCBROWSEDIRECTION;
import org.openscada.opc.dcom.da.OPCBROWSETYPE;
import org.openscada.opc.dcom.da.impl.OPCBrowseServerAddressSpace;
import org.openscada.opc.lib.da.browser.Access;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowseJob extends ThreadJob implements JobResult<BrowseResult>
{

    private final static Logger logger = LoggerFactory.getLogger ( BrowseJob.class );

    private final OPCModel model;

    private BrowseResult result;

    private final int batchSize = 100;

    private final BrowseRequest request;

    public BrowseJob ( final long timeout, final OPCModel model, final BrowseRequest request )
    {
        super ( timeout );
        this.model = model;
        this.request = request;
    }

    @Override
    protected void perform () throws Exception
    {
        if ( logger.isInfoEnabled () )
        {
            logger.info ( String.format ( "Browsing folder: %s", StringHelper.join ( this.request.getPath (), "/" ) ) );
        }

        final OPCBrowseServerAddressSpace browser = this.model.getServer ().getBrowser ();
        if ( browser == null )
        {
            logger.warn ( "Unable to fetch browser" );
            return;
        }

        final BrowseResult result = new BrowseResult ();
        final int accessMask = Access.READ.getCode () | Access.WRITE.getCode ();

        // move in position
        browser.changePosition ( null, OPCBROWSEDIRECTION.OPC_BROWSE_TO );
        for ( final String path : this.request.getPath () )
        {
            browser.changePosition ( path, OPCBROWSEDIRECTION.OPC_BROWSE_DOWN );
        }

        // get the branches 
        result.setBranches ( browser.browse ( OPCBROWSETYPE.OPC_BRANCH, "", accessMask, JIVariant.VT_EMPTY ).asCollection ( this.batchSize ) );

        // get the leaves
        final Collection<String> readLeaves = browser.browse ( OPCBROWSETYPE.OPC_LEAF, "", Access.READ.getCode (), JIVariant.VT_EMPTY ).asCollection ( this.batchSize );
        final Collection<String> writeLeaves = browser.browse ( OPCBROWSETYPE.OPC_LEAF, "", Access.WRITE.getCode (), JIVariant.VT_EMPTY ).asCollection ( this.batchSize );
        processLeaves ( result, browser, readLeaves, writeLeaves );

        // result
        this.result = result;
        logger.info ( "Completed (Leaves: {}, Branches: {})", result.getLeaves ().size (), result.getBranches ().size () );
    }

    private void processLeaves ( final BrowseResult result, final OPCBrowseServerAddressSpace browser, final Collection<String> readLeaves, final Collection<String> writeLeaves ) throws JIException
    {
        final Map<String, BrowseResultEntry> leavesResult = new HashMap<String, BrowseResultEntry> ();

        // add read leaves
        for ( final String leaf : readLeaves )
        {
            final BrowseResultEntry entry = new BrowseResultEntry ();
            entry.setEntryName ( leaf );
            final String itemId = browser.getItemID ( leaf );
            entry.setItemId ( itemId );
            entry.setIoDirections ( EnumSet.of ( IODirection.INPUT ) );

            leavesResult.put ( leaf, entry );
        }

        // add write leaves
        for ( final String leaf : writeLeaves )
        {
            BrowseResultEntry entry = leavesResult.get ( leaf );
            if ( entry != null )
            {
                // if we already found it as a read leaf we simply add "output"
                entry.getIoDirections ().add ( IODirection.OUTPUT );
            }
            else
            {
                entry = new BrowseResultEntry ();
                entry.setEntryName ( leaf );
                final String itemId = browser.getItemID ( leaf );
                entry.setItemId ( itemId );
                entry.setIoDirections ( EnumSet.of ( IODirection.OUTPUT ) );

                leavesResult.put ( leaf, entry );
            }
        }

        result.setLeaves ( leavesResult.values () );
    }

    @Override
    public BrowseResult getResult ()
    {
        return this.result;
    }

}
