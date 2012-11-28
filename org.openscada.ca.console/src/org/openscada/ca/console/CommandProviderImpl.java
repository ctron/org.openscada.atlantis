/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ca.console;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.openscada.ca.FreezableConfigurationAdministrator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class CommandProviderImpl implements CommandProvider
{

    private ServiceRegistration<CommandProvider> reg;

    private ServiceTracker<FreezableConfigurationAdministrator, FreezableConfigurationAdministrator> caTracker;

    public CommandProviderImpl ()
    {
    }

    public void start ( final BundleContext context )
    {
        this.caTracker = new ServiceTracker<FreezableConfigurationAdministrator, FreezableConfigurationAdministrator> ( context, FreezableConfigurationAdministrator.class, null );
        this.caTracker.open ();

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
        this.reg = context.registerService ( CommandProvider.class, this, properties );
    }

    public void stop ( final BundleContext context )
    {
        this.caTracker.close ();
        this.caTracker = null;

        this.reg.unregister ();
        this.reg = null;
    }

    @Override
    public String getHelp ()
    {
        final StringBuilder sb = new StringBuilder ();
        sb.append ( "\n---Configuration Administrator Commands---\n" ); //$NON-NLS-1$
        sb.append ( "\tfreezeCfgAdmin - stop announcing changes\n" ); //$NON-NLS-1$
        sb.append ( "\tthawCfgAdmin - start announcing changes\n" ); //$NON-NLS-1$
        return sb.toString ();
    }

    public void _freezeCfgAdmin ( final CommandInterpreter cmd )
    {
        final Object[] services = this.caTracker.getServices ();
        final int count = services != null ? services.length : 0;
        cmd.println ( String.format ( "Freezing %s configuration administrators", count ) );
        if ( services != null )
        {
            for ( final Object o : services )
            {
                if ( o instanceof FreezableConfigurationAdministrator )
                {
                    cmd.println ( String.format ( "Freeze: %s", o ) );
                    try
                    {
                        ( (FreezableConfigurationAdministrator)o ).freeze ();
                    }
                    catch ( final Exception e )
                    {
                        cmd.println ( "Failed to freeze" );
                        cmd.printStackTrace ( e );
                    }
                }
            }
        }
    }

    public void _thawCfgAdmin ( final CommandInterpreter cmd )
    {
        final Object[] services = this.caTracker.getServices ();
        final int count = services != null ? services.length : 0;
        cmd.println ( String.format ( "Thawing %s configuration administrators", count ) );
        if ( services != null )
        {
            for ( final Object o : services )
            {
                if ( o instanceof FreezableConfigurationAdministrator )
                {
                    cmd.println ( String.format ( "Thaw: %s", o ) );
                    try
                    {
                        ( (FreezableConfigurationAdministrator)o ).thaw ();
                    }
                    catch ( final Exception e )
                    {
                        cmd.println ( "Failed to thaw" );
                        cmd.printStackTrace ( e );
                    }
                }
            }
        }
    }

}
