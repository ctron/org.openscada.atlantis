/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.exec.extractor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.item.factory.DefaultChainItemFactory;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.exec.Hive;
import org.openscada.da.server.exec.command.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link AbstractBaseExtractor} class implements most of the stuff you need
 * in order
 * to implement a full extractor. Handling of item factory, error handling and
 * destruction (unregistering)
 * are handled for you.
 * 
 * @author Jens Reimann
 */
public abstract class AbstractBaseExtractor implements Extractor
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractBaseExtractor.class );

    private final List<DataItemInputChained> inputs = new LinkedList<DataItemInputChained> ();

    private FolderItemFactory itemFactory;

    private Hive hive;

    private final String id;

    public AbstractBaseExtractor ( final String id )
    {
        this.id = id;
    }

    protected DataItemInputChained createInput ( final String localId )
    {
        final DataItemInputChained item = this.itemFactory.createInput ( localId, null );
        this.inputs.add ( item );
        return item;
    }

    @Override
    public void process ( final ExecutionResult result )
    {
        if ( result.getExecutionError () != null )
        {
            setError ( result.getExecutionError (), "exec" );
        }
        else
        {
            try
            {
                doProcess ( result );
            }
            catch ( final Throwable e )
            {
                setError ( e, "value" );
            }
        }
    }

    /**
     * Set the error flags
     * 
     * @param executionError
     *            the error that occurred
     */
    protected void setError ( final Throwable executionError, final String which )
    {
        logger.info ( "Setting error", executionError );

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( which + ".error", Variant.valueOf ( true ) );
        if ( executionError != null )
        {
            attributes.put ( which + ".error.message", Variant.valueOf ( executionError.getMessage () ) );
        }

        for ( final DataItemInputChained item : this.inputs )
        {
            item.updateData ( null, attributes, AttributeMode.UPDATE );
        }
    }

    protected void fillNoError ( final Map<String, Variant> attributes )
    {
        attributes.put ( "exec.error", null );
        attributes.put ( "exec.error.message", null );
        attributes.put ( "value.error", null );
        attributes.put ( "value.error.message", null );
    }

    /**
     * Process the execution result as input.
     * <p>
     * This method will only be called if no execution error occurred. Otherwise
     * the error flags of all data items are automatically set.
     * <p>
     * This method must apply the attributes from {@link #fillNoError(Map)} when
     * updating items. This is needed since applying them afterwards would cause
     * two updates on the dataitem and the state would by async to the value
     * itself.
     * 
     * @param result
     *            the execution result that has to be processes
     * @throws Exception
     *             if anything goes wrong
     */
    protected abstract void doProcess ( ExecutionResult result ) throws Exception;

    @Override
    public void register ( final Hive hive, final FolderItemFactory folderItemFactory )
    {
        this.hive = hive;
        this.itemFactory = new DefaultChainItemFactory ( folderItemFactory, this.hive, folderItemFactory.getFolder (), this.id, this.id );
        folderItemFactory.addSubFactory ( this.itemFactory );
    }

    @Override
    public void unregister ()
    {
        this.inputs.clear ();
        this.itemFactory.dispose ();
    }

}
