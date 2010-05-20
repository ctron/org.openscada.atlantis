/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.opc.job.impl;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.da.core.IODirection;
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
import org.openscada.utils.str.StringHelper;

public class BrowseJob extends ThreadJob implements JobResult<BrowseResult>
{
    private static Logger logger = Logger.getLogger ( BrowseJob.class );

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
        if ( logger.isInfoEnabled () )
        {
            logger.info ( String.format ( "Completed (Leaves: %s, Branches: %s)", result.getLeaves ().size (), result.getBranches ().size () ) );
        }
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

    public BrowseResult getResult ()
    {
        return this.result;
    }

}
