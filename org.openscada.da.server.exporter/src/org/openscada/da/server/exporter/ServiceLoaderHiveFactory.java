/*
 * This file is part of the OpenSCADA project
 * 
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

package org.openscada.da.server.exporter;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.openscada.da.core.server.Hive;
import org.openscada.da.core.server.HiveCreator;

/**
 * This hive factory creates hives by locating its {@link HiveCreator} first
 * using
 * the {@link ServiceLoader} facility
 * 
 * @author Jens Reimann
 */
public class ServiceLoaderHiveFactory extends AbstractHiveFactory
{

    @Override
    public Hive createHive ( final String reference, final HiveConfigurationType configuration ) throws Exception
    {
        final ServiceLoader<HiveCreator> serviceLoader = ServiceLoader.load ( HiveCreator.class );

        final Iterator<HiveCreator> i = serviceLoader.iterator ();
        while ( i.hasNext () )
        {
            final HiveCreator creator = i.next ();
            final Hive hive = creator.createHive ( reference, configuration );
            if ( hive != null )
            {
                return hive;
            }
        }

        return null;
    }

}
