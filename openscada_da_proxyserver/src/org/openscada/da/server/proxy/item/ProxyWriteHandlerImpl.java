/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.proxy.item;

import java.util.Collections;
import java.util.Map;

import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.NoConnectionException;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.server.proxy.connection.ProxySubConnection;
import org.openscada.da.server.proxy.utils.ProxyPrefixName;
import org.openscada.da.server.proxy.utils.ProxySubConnectionId;
import org.openscada.da.server.proxy.utils.ProxyUtils;

public class ProxyWriteHandlerImpl extends ProxyItemSupport implements ProxyWriteHandler
{
    protected final Map<ProxySubConnectionId, ProxySubConnection> subConnections;

    public ProxyWriteHandlerImpl ( final String separator, final ProxyPrefixName prefix, final Map<ProxySubConnectionId, ProxySubConnection> subConnections, final ProxySubConnectionId currentConnection, final String proxyItemId )
    {
        super ( separator, prefix, currentConnection, proxyItemId );
        this.subConnections = Collections.unmodifiableMap ( subConnections );
    }

    /* (non-Javadoc)
     * @see org.openscada.da.server.proxy.ProxyWriteHandler#write(java.lang.String, org.openscada.core.Variant)
     */
    public void write ( final String itemId, final Variant value ) throws NoConnectionException, OperationException
    {
        final ProxySubConnection subConnection = this.subConnections.get ( this.currentConnection );
        final String actualItemId = ProxyUtils.originalItemId ( itemId, this.separator, this.prefix, subConnection.getPrefix () );
        subConnection.getConnection ().write ( actualItemId, value );
    }

    /* (non-Javadoc)
     * @see org.openscada.da.server.proxy.ProxyWriteHandler#writeAttributes(java.lang.String, java.util.Map, org.openscada.da.core.WriteAttributeResults)
     */
    public void writeAttributes ( final String itemId, final Map<String, Variant> attributes, final WriteAttributeResults writeAttributeResults )
    {
        final ProxySubConnection subConnection = this.subConnections.get ( this.currentConnection );
        final String actualItemId = ProxyUtils.originalItemId ( itemId, this.separator, this.prefix, subConnection.getPrefix () );
        WriteAttributeResults actualWriteAttributeResults;
        try
        {
            actualWriteAttributeResults = subConnection.getConnection ().writeAttributes ( actualItemId, attributes );
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
