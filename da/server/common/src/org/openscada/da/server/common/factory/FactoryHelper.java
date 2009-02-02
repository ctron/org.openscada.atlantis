/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.server.common.factory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.chain.ChainItem;
import org.openscada.da.server.common.chain.ChainProcessEntry;
import org.openscada.da.server.common.configuration.ConfigurationError;

public class FactoryHelper
{
    private static Logger log = Logger.getLogger ( FactoryHelper.class );

    static public ChainItem createChainItem ( final HiveServiceRegistry serviceRegistry, final Class<?> clazz ) throws ConfigurationError
    {
        Object whatObject;
        try
        {
            Constructor<?> ctor = null;
            try
            {
                ctor = clazz.getConstructor ( HiveServiceRegistry.class );
            }
            catch ( final Throwable e )
            {
                log.debug ( "Failed to load ctor for HiveServiceRegistry" );
            }

            if ( ctor != null )
            {
                whatObject = ctor.newInstance ( serviceRegistry );
            }
            else
            {
                whatObject = clazz.newInstance ();
            }
        }
        catch ( final Exception e )
        {
            throw new ConfigurationError ( "Unable to instatiate chain item", e );
        }

        if ( ! ( whatObject instanceof ChainItem ) )
        {
            throw new ConfigurationError ( String.format ( "Chain item %s does not implement interface ChainItem", clazz ) );
        }

        return (ChainItem)whatObject;
    }

    public static List<ChainProcessEntry> instantiateChainList ( final HiveServiceRegistry serviceRegistry, final List<ChainEntry> chainEntries ) throws ConfigurationError
    {
        final List<ChainProcessEntry> list = new ArrayList<ChainProcessEntry> ();

        for ( final ChainEntry entry : chainEntries )
        {
            final ChainProcessEntry processEntry = new ChainProcessEntry ();

            processEntry.setWhen ( entry.getWhen () );
            processEntry.setWhat ( createChainItem ( serviceRegistry, entry.getWhat () ) );

            list.add ( processEntry );
        }

        return list;
    }

}
