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

package org.openscada.da.master;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.sec.UserInformationPrincipal;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.util.tracker.ServiceTracker;

public abstract class AbstractConfigurableMasterHandlerImpl extends AbstractMasterHandlerImpl
{

    private final String prefix;

    private final ServiceTracker<ConfigurationAdministrator,ConfigurationAdministrator> tracker;

    private final String factoryId;

    private final String configurationId;

    public AbstractConfigurableMasterHandlerImpl ( final String configurationId, final ObjectPoolTracker poolTracker, final int priority, final ServiceTracker<ConfigurationAdministrator,ConfigurationAdministrator> caTracker, final String prefix, final String factoryId )
    {
        super ( poolTracker, priority );
        this.configurationId = configurationId;
        this.tracker = caTracker;
        this.prefix = prefix;
        this.factoryId = factoryId;
    }

    protected String getPrefixed ( final String id )
    {
        if ( id == null )
        {
            return this.prefix;
        }
        else
        {
            return this.prefix + "." + id;
        }
    }

    @Override
    public WriteRequestResult processWrite ( final WriteRequest request )
    {
        if ( request.getAttributes () == null )
        {
            return null;
        }

        // extract our prefixed attributes
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        for ( final Map.Entry<String, Variant> entry : request.getAttributes ().entrySet () )
        {
            final String key = entry.getKey ();
            if ( key.startsWith ( this.prefix + "." ) )
            {
                attributes.put ( key.substring ( ( this.prefix + "." ).length () ), entry.getValue () );
            }
        }

        if ( attributes.isEmpty () )
        {
            // we have nothing to do
            return null;
        }

        try
        {
            final WriteAttributeResults result = handleUpdate ( Collections.unmodifiableMap ( attributes ), request.getOperationParameters () );

            // remove processed attributes
            for ( final String attr : result.keySet () )
            {
                attributes.remove ( attr );
            }

            for ( final String attr : attributes.keySet () )
            {
                result.put ( attr, new WriteAttributeResult ( new OperationException ( String.format ( "Attribute '%s' is not supported", this.prefix + "." + attr ) ) ) );
            }

            final Map<String, Variant> newAttributes = new HashMap<String, Variant> ( request.getAttributes () );
            final WriteAttributeResults fullResults = new WriteAttributeResults ();
            for ( final Map.Entry<String, WriteAttributeResult> entry : result.entrySet () )
            {
                final String fullKey = this.prefix + "." + entry.getKey ();
                fullResults.put ( fullKey, entry.getValue () );

                // remove from list of "to be written" attributes
                newAttributes.remove ( fullKey );
            }

            return new WriteRequestResult ( request.getValue (), newAttributes, fullResults );
        }
        catch ( final Throwable e )
        {
            return new WriteRequestResult ( e );
        }
    }

    /**
     * This method will be called on write request that have attributes which match our prefix.
     * @param writeInformation the write information of the write request
     * @param attributes the filtered attributes that match our prefix 
     * @return the attribute result of the written attributes
     * @throws Exception if anything goes wrong
     */
    protected abstract WriteAttributeResults handleUpdate ( final Map<String, Variant> attributes, final OperationParameters operationParameters ) throws Exception;

    protected WriteAttributeResults updateConfiguration ( final Map<String, String> data, final Map<String, Variant> attributes, final boolean fullSet, final OperationParameters operationParameters ) throws OperationException
    {
        final WriteAttributeResults result = new WriteAttributeResults ();

        if ( data.isEmpty () )
        {
            return result;
        }

        final ConfigurationAdministrator service = this.tracker.getService ();
        if ( ! ( service instanceof ConfigurationAdministrator ) )
        {
            final OperationException error = new OperationException ( "Configuration administrator not available" );
            for ( final String attr : data.keySet () )
            {
                result.put ( attr, new WriteAttributeResult ( error ) );
            }
            return result;
        }
        else
        {
            for ( final String attr : data.keySet () )
            {
                if ( attributes.containsKey ( attr ) )
                {
                    // only add key to result if it was requested
                    result.put ( attr, WriteAttributeResult.OK );
                }
            }

            final ConfigurationAdministrator admin = (ConfigurationAdministrator)service;

            admin.updateConfiguration ( UserInformationPrincipal.create ( operationParameters.getUserInformation () ), this.factoryId, this.configurationId, data, fullSet );

            return result;
        }
    }
}
