/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.eclipse.scada.da.server.exec.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.da.server.common.AttributeMode;
import org.eclipse.scada.da.server.common.chain.DataItemInputChained;
import org.eclipse.scada.da.server.common.impl.HiveCommon;
import org.eclipse.scada.da.server.common.item.factory.DefaultChainItemFactory;
import org.eclipse.scada.da.server.common.item.factory.FolderItemFactory;
import org.eclipse.scada.da.server.exec.Hive;
import org.eclipse.scada.da.server.exec.extractor.Extractor;
import org.eclipse.scada.da.server.exec.util.CommandExecutor;

public class AbstractSingleCommand
{

    protected final String id;

    private HiveCommon hive;

    protected final ProcessConfiguration processConfiguration;

    protected final Collection<Extractor> extractors;

    private FolderItemFactory itemFactory;

    private FolderCommon folderCommon;

    private DataItemInputChained runningItem;

    private DataItemInputChained exitCodeItem;

    private DataItemInputChained execItem;

    public AbstractSingleCommand ( final String id, final ProcessConfiguration processConfiguration, final Collection<Extractor> extractors )
    {
        this.id = id;
        this.processConfiguration = processConfiguration;
        this.extractors = extractors;
    }

    protected void execute ( final ProcessBuilder processBuilder, final ProcessListener listener )
    {
        this.runningItem.updateData ( Variant.TRUE, null, null );

        final ExecutionResult result;
        try
        {
            result = CommandExecutor.executeCommand ( processBuilder, listener );
        }
        finally
        {
            this.runningItem.updateData ( Variant.FALSE, null, null );
            this.execItem.updateData ( Variant.NULL, new HashMap<String, Variant> (), AttributeMode.SET );
        }

        updateStatus ( result );

        for ( final Extractor extractor : this.extractors )
        {
            extractor.process ( result );
        }
    }

    private void updateStatus ( final ExecutionResult result )
    {
        this.exitCodeItem.updateData ( Variant.valueOf ( result.getExitValue () ), null, null );

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "exec.runtime", Variant.valueOf ( result.getRuntime () ) );
        attributes.put ( "exec.exitCode", Variant.valueOf ( result.getExitValue () ) );
        attributes.put ( "exec.standardOutput", Variant.valueOf ( result.getOutput () ) );
        attributes.put ( "exec.errorOutput", Variant.valueOf ( result.getErrorOutput () ) );
        this.execItem.updateData ( Variant.valueOf ( result.toString () ), attributes, AttributeMode.SET );
    }

    public void register ( final Hive hive, final FolderCommon parentFolder )
    {
        this.hive = hive;
        this.folderCommon = parentFolder;

        this.itemFactory = new DefaultChainItemFactory ( this.hive, this.folderCommon, this.id, this.id );
        this.runningItem = this.itemFactory.createInput ( "running", null );
        this.exitCodeItem = this.itemFactory.createInput ( "exitCode", null );
        this.execItem = this.itemFactory.createInput ( "exec", null );

        for ( final Extractor ext : this.extractors )
        {
            ext.register ( hive, this.itemFactory );
        }
    }

    public void unregister ()
    {
        if ( this.itemFactory != null )
        {
            this.itemFactory.dispose ();
            this.itemFactory = null;
        }
        this.hive = null;
    }

    /**
     * Gets the data item factory for this item
     * <p>
     * Returns <code>null</code> until the {@link #register(Hive, FolderCommon)}
     * method was called.
     * <p>
     * The registry will be disposed when {@link #unregister()} is called. There
     * is no need to unregister items manually.
     * 
     * @return the item factory used for this item
     */
    protected FolderItemFactory getItemFactory ()
    {
        return this.itemFactory;
    }

}
