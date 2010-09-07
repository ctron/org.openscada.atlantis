/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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
