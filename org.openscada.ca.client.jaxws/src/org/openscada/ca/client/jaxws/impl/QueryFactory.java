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

package org.openscada.ca.client.jaxws.impl;

import java.util.concurrent.Callable;

import org.openscada.ca.FactoryInformation;
import org.openscada.ca.client.jaxws.RemoteConfigurationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryFactory implements Callable<FactoryInformation>
{

    private final static Logger logger = LoggerFactory.getLogger ( QueryFactory.class );

    private final RemoteConfigurationClient client;

    private final String factoryId;

    public QueryFactory ( final RemoteConfigurationClient client, final String factoryId )
    {
        this.factoryId = factoryId;
        this.client = client;
    }

    public FactoryInformation call () throws Exception
    {
        logger.debug ( "Query data : {}", this.factoryId );
        final FactoryInformation result = this.client.getPort ().getFactory ( this.factoryId );
        logger.debug ( "Query data : {} -> {}", new Object[] { this.factoryId, result } );
        return result;
    }

}
