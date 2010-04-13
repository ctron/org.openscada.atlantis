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

package org.openscada.da.datasource.script;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.VariantEditor;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.utils.osgi.FilterUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

public class WriterController
{
    private static final long DEFAULT_TIMEOUT = 10000;

    private final BundleContext context;

    public WriterController ( final BundleContext context )
    {
        this.context = context;
    }

    public void write ( final String dataSourceId, final Object value ) throws Exception
    {
        final Variant variant = Variant.valueOf ( value );

        final Filter filter = makeFilter ( dataSourceId );

        final ServiceTracker tracker = new ServiceTracker ( this.context, filter, null );

        tracker.open ();
        try
        {
            final DataSource source = (DataSource)tracker.waitForService ( DEFAULT_TIMEOUT );

            if ( source == null )
            {
                throw new IllegalStateException ( String.format ( "Failed to write. Service not found for filter '%s'", filter ) );
            }

            final WriteInformation writeInformation = new WriteInformation ( null );
            source.startWriteValue ( writeInformation, variant );
        }
        catch ( final InterruptedException e )
        {
        }
        finally
        {
            tracker.close ();
        }
    }

    private Filter makeFilter ( final String dataSourceId ) throws InvalidSyntaxException
    {
        final Map<String, String> parameters = new HashMap<String, String> ();
        parameters.put ( "datasource.id", dataSourceId );
        return FilterUtil.createAndFilter ( DataSource.class.getName (), parameters );
    }

    public void writeAsText ( final String itemId, final String value ) throws Exception
    {
        final VariantEditor ve = new VariantEditor ();
        ve.setAsText ( value );
        write ( itemId, ve.getValue () );
    }
}
