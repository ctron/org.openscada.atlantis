/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.sec.osgi.manager;

import java.util.Map;

import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.AuthorizationService;
import org.openscada.sec.authz.AuthorizationContext;
import org.openscada.sec.authz.AuthorizationRule;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationEntry extends AbstractBaseRule implements Comparable<ConfigurationEntry>
{

    private final static Logger logger = LoggerFactory.getLogger ( ConfigurationEntry.class );

    private final String id;

    private final Map<String, String> properties;

    private final AuthorizationManagerImpl managerImpl;

    private final String serviceType;

    private final int priority;

    private volatile AuthorizationRule rule;

    private AuthorizationService service;

    private Throwable error;

    public ConfigurationEntry ( final AuthorizationManagerImpl managerImpl, final String id, final String serviceType, final int priority, final Map<String, String> properties )
    {
        this.managerImpl = managerImpl;
        this.id = id;
        this.serviceType = serviceType;
        this.properties = properties;
        this.priority = priority;
    }

    public String getId ()
    {
        return this.id;
    }

    public int getPriority ()
    {
        return this.priority;
    }

    public String getServiceType ()
    {
        return this.serviceType;
    }

    public Throwable getError ()
    {
        return this.error;
    }

    public boolean isRealized ()
    {
        return this.rule != null;
    }

    public boolean realize ()
    {
        if ( this.rule != null )
        {
            return false;
        }

        logger.debug ( "Trying to realize configuration - id: {}, serviceType: {}, properties: {}", new Object[] { this.id, this.serviceType, this.properties } );

        final AuthorizationService service = this.managerImpl.findService ( this.serviceType );
        if ( service != null )
        {
            this.service = service;
            try
            {
                this.rule = service.createRule ( this.properties );
                this.error = null;
                return true;
            }
            catch ( final Exception e )
            {
                this.error = e;
                logger.warn ( "Failed to realize rule", e );
                return false;
            }
        }
        return false;
    }

    public void unrealize ()
    {
        if ( this.rule != null )
        {
            logger.debug ( "Unrealizing rule - id: {}", this.id );
            this.rule.dispose ();
            this.rule = null;
            this.service = null;
            this.error = null;
        }
    }

    @Override
    public void dispose ()
    {
        unrealize ();
    }

    public boolean realizedBy ( final AuthorizationService service )
    {
        return this.service == service;
    }

    @Override
    protected NotifyFuture<AuthorizationResult> procesAuthorize ( final AuthorizationContext context )
    {
        final AuthorizationRule rule = this.rule;
        if ( rule == null )
        {
            return new InstantErrorFuture<AuthorizationResult> ( new IllegalStateException ( String.format ( "Configuration entry is not realized - id: %s, serviceType: %s", this.id, this.serviceType ) ) );
        }
        return rule.authorize ( context );
    }

    /**
     * Sorted by highest priority first
     */
    @Override
    public int compareTo ( final ConfigurationEntry o )
    {
        if ( o == null )
        {
            return 1;
        }

        return this.priority < o.priority ? 1 : this.priority == o.priority ? 0 : -1;
    }

}
