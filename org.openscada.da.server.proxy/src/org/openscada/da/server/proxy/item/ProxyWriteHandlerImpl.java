/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.proxy.item;

import java.util.Collections;
import java.util.Map;

import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.NoConnectionException;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.server.proxy.connection.ProxySubConnection;
import org.openscada.da.server.proxy.utils.ProxyPrefixName;
import org.openscada.da.server.proxy.utils.ProxySubConnectionId;
import org.openscada.da.server.proxy.utils.ProxyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyWriteHandlerImpl extends ProxyItemSupport implements ProxyWriteHandler
{

    private final static Logger logger = LoggerFactory.getLogger ( ProxyWriteHandlerImpl.class );

    protected final Map<ProxySubConnectionId, ProxySubConnection> subConnections;

    public ProxyWriteHandlerImpl ( final String separator, final ProxyPrefixName prefix, final Map<ProxySubConnectionId, ProxySubConnection> subConnections, final ProxySubConnectionId currentConnection, final String proxyItemId )
    {
        super ( separator, prefix, currentConnection, proxyItemId );
        this.subConnections = Collections.unmodifiableMap ( subConnections );
    }

    /* (non-Javadoc)
     * @see org.openscada.da.server.proxy.ProxyWriteHandler#write(java.lang.String, org.openscada.core.Variant)
     */
    @Override
    public void write ( final String itemId, final Variant value, final OperationParameters operationParameters ) throws NoConnectionException, OperationException
    {
        logger.debug ( "Writing item - itemId: {}, value: {}, operationParameters: {}", new Object[] { itemId, value, operationParameters } );

        final ProxySubConnection subConnection = this.subConnections.get ( this.currentConnection );
        final String actualItemId = ProxyUtils.originalItemId ( itemId, this.separator, this.prefix, subConnection.getPrefix () );

        logger.debug ( "Writing to subconnection - subConnection: {}, originalItem: {}, actualItem: {}", new Object[] { subConnection.getId (), itemId, actualItemId } );

        subConnection.getConnection ().write ( actualItemId, value, operationParameters );
    }

    /* (non-Javadoc)
     * @see org.openscada.da.server.proxy.ProxyWriteHandler#writeAttributes(java.lang.String, java.util.Map, org.openscada.da.core.WriteAttributeResults)
     */
    @Override
    public void writeAttributes ( final String itemId, final Map<String, Variant> attributes, final WriteAttributeResults writeAttributeResults, final OperationParameters operationParameters )
    {
        final ProxySubConnection subConnection = this.subConnections.get ( this.currentConnection );
        final String actualItemId = ProxyUtils.originalItemId ( itemId, this.separator, this.prefix, subConnection.getPrefix () );
        WriteAttributeResults actualWriteAttributeResults;
        try
        {
            actualWriteAttributeResults = subConnection.getConnection ().writeAttributes ( actualItemId, attributes, operationParameters );
        }
        catch ( final NoConnectionException e )
        {
            actualWriteAttributeResults = attributesCouldNotBeWritten ( attributes, e );
        }
        catch ( final OperationException e )
        {
            actualWriteAttributeResults = attributesCouldNotBeWritten ( attributes, e );
        }
        writeAttributeResults.putAll ( actualWriteAttributeResults );
    }

    /**
     * creates a WriteAttributeResults object for given attributes filled 
     * with given exception for each attribute
     * @param attributes
     * @param e
     * @return
     */
    private WriteAttributeResults attributesCouldNotBeWritten ( final Map<String, Variant> attributes, final Exception e )
    {
        final WriteAttributeResults results = new WriteAttributeResults ();
        for ( final String name : attributes.keySet () )
        {
            results.put ( name, new WriteAttributeResult ( e ) );
        }
        return results;
    }

}
