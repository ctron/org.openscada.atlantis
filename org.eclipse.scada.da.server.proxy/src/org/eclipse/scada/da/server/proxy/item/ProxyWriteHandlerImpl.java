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

package org.eclipse.scada.da.server.proxy.item;

import java.util.Collections;
import java.util.Map;

import org.eclipse.scada.core.OperationException;
import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.client.NoConnectionException;
import org.eclipse.scada.core.server.OperationParameters;
import org.eclipse.scada.core.server.OperationParametersHelper;
import org.eclipse.scada.da.client.WriteAttributeOperationCallback;
import org.eclipse.scada.da.client.WriteOperationCallback;
import org.eclipse.scada.da.core.WriteAttributeResult;
import org.eclipse.scada.da.core.WriteAttributeResults;
import org.eclipse.scada.da.server.proxy.connection.ProxySubConnection;
import org.eclipse.scada.da.server.proxy.utils.ProxyPrefixName;
import org.eclipse.scada.da.server.proxy.utils.ProxySubConnectionId;
import org.eclipse.scada.da.server.proxy.utils.ProxyUtils;
import org.eclipse.scada.utils.concurrent.AbstractFuture;

public class ProxyWriteHandlerImpl extends ProxyItemSupport implements ProxyWriteHandler
{
    public static class AttributeResultHandler extends AbstractFuture<WriteAttributeResults> implements WriteAttributeOperationCallback
    {
        @Override
        public void failed ( final String error )
        {
            setError ( new RuntimeException ( error ).fillInStackTrace () );
        }

        @Override
        public void error ( final Throwable e )
        {
            setError ( e );
        }

        @Override
        public void complete ( final WriteAttributeResults result )
        {
            setResult ( result );
        }
    }

    public static class ValueResultHandler extends AbstractFuture<Void> implements WriteOperationCallback
    {
        @Override
        public void failed ( final String error )
        {
            setError ( new RuntimeException ( error ).fillInStackTrace () );
        }

        @Override
        public void error ( final Throwable e )
        {
            setError ( e );
        }

        @Override
        public void complete ()
        {
            setResult ( null );
        }
    }

    protected final Map<ProxySubConnectionId, ProxySubConnection> subConnections;

    public ProxyWriteHandlerImpl ( final String separator, final ProxyPrefixName prefix, final Map<ProxySubConnectionId, ProxySubConnection> subConnections, final ProxySubConnectionId currentConnection, final String proxyItemId )
    {
        super ( separator, prefix, currentConnection, proxyItemId );
        this.subConnections = Collections.unmodifiableMap ( subConnections );
    }

    /* (non-Javadoc)
     * @see org.eclipse.scada.da.server.proxy.ProxyWriteHandler#write(java.lang.String, org.eclipse.scada.core.Variant)
     */
    @Override
    public void write ( final String itemId, final Variant value, final OperationParameters operationParameters ) throws NoConnectionException, OperationException
    {
        final ProxySubConnection subConnection = this.subConnections.get ( this.currentConnection );
        final String actualItemId = ProxyUtils.originalItemId ( itemId, this.separator, this.prefix, subConnection.getPrefix () );

        final ValueResultHandler callback = new ValueResultHandler ();
        subConnection.getConnection ().write ( actualItemId, value, OperationParametersHelper.toData ( operationParameters ), callback );
        try
        {
            callback.get ();
        }
        catch ( final Exception e )
        {
            throw new OperationException ( e );
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.scada.da.server.proxy.ProxyWriteHandler#writeAttributes(java.lang.String, java.util.Map, org.eclipse.scada.da.core.WriteAttributeResults)
     */
    @Override
    public void writeAttributes ( final String itemId, final Map<String, Variant> attributes, final WriteAttributeResults writeAttributeResults, final OperationParameters operationParameters )
    {
        final ProxySubConnection subConnection = this.subConnections.get ( this.currentConnection );
        final String actualItemId = ProxyUtils.originalItemId ( itemId, this.separator, this.prefix, subConnection.getPrefix () );

        final AttributeResultHandler callback = new AttributeResultHandler ();

        WriteAttributeResults actualWriteAttributeResults;
        try
        {
            subConnection.getConnection ().writeAttributes ( actualItemId, attributes, OperationParametersHelper.toData ( operationParameters ), callback );
            actualWriteAttributeResults = callback.get ();
        }
        catch ( final Exception e )
        {
            actualWriteAttributeResults = attributesCouldNotBeWritten ( attributes, e );
        }
        writeAttributeResults.putAll ( actualWriteAttributeResults );
    }

    /**
     * creates a WriteAttributeResults object for given attributes filled
     * with given exception for each attribute
     * 
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
