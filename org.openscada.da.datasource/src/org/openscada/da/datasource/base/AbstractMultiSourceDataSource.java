/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.datasource.base;

import java.util.Map;

import org.eclipse.scada.core.VariantType;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolTracker;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.DataSourceHandler;
import org.openscada.da.datasource.MultiDataSourceListener;
import org.osgi.framework.InvalidSyntaxException;

public abstract class AbstractMultiSourceDataSource extends AbstractDataSource
{

    private final MultiDataSourceListener listener;

    public AbstractMultiSourceDataSource ( final ObjectPoolTracker<DataSource> poolTracker )
    {
        super ();
        this.listener = new MultiDataSourceListener ( poolTracker ) {

            @Override
            protected void handleChange ( final Map<String, DataSourceHandler> sources )
            {
                AbstractMultiSourceDataSource.this.handleChange ( sources );
            }
        };
    }

    protected void setDataSources ( final Map<String, String> parameters ) throws InvalidSyntaxException
    {
        this.listener.setDataSources ( parameters );
    }

    protected abstract void handleChange ( Map<String, DataSourceHandler> sources );

    /**
     * Clear all datasources
     */
    protected void clearSources ()
    {
        this.listener.clearSources ();
    }

    public void dispose ()
    {
        this.listener.dispose ();
    }

    public void addDataSource ( final String datasourceKey, final String datasourceId, final VariantType type ) throws InvalidSyntaxException
    {
        this.listener.addDataSource ( datasourceKey, datasourceId, type );
    }

    public Map<String, DataSourceHandler> getSourcesCopy ()
    {
        return this.listener.getSourcesCopy ();
    }

    public VariantType getType ( final String type )
    {
        return this.listener.getType ( type );
    }
}