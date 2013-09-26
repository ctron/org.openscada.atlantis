/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.mapper.osgi.jdbc;

import org.eclipse.scada.utils.beans.AbstractPropertyChange;

public class JdbcValueMapperState extends AbstractPropertyChange
{
    private static final String PROP_LOADING = "loading";

    private static final String PROP_ERROR = "error";

    private static final String PROP_ENTRIES = "entries";

    private boolean loading;

    private boolean error;

    private int entries;

    public boolean isLoading ()
    {
        return this.loading;
    }

    public void setLoading ( final boolean loading )
    {
        firePropertyChange ( PROP_LOADING, this.loading, this.loading = loading );
    }

    public boolean isError ()
    {
        return this.error;
    }

    public void setError ( final boolean error )
    {
        firePropertyChange ( PROP_ERROR, this.error, this.error = error );
    }

    public int getEntries ()
    {
        return this.entries;
    }

    public void setEntries ( final int entries )
    {
        firePropertyChange ( PROP_ENTRIES, this.entries, this.entries = entries );
    }
}
