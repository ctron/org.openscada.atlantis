/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.core.client.common;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.info.StatisticEntry;
import org.openscada.core.info.StatisticsImpl;
import org.openscada.core.info.StatisticsProvider;
import org.openscada.utils.concurrent.NamedThreadFactory;

public class BaseConnection implements StatisticsProvider
{
    protected final ConnectionInformation connectionInformation;

    protected final ScheduledExecutorService executor;

    private volatile Map<String, String> sessionProperties = Collections.emptyMap ();

    protected final StatisticsImpl statistics;

    public BaseConnection ( final ConnectionInformation connectionInformation )
    {
        this.connectionInformation = connectionInformation;
        this.executor = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( connectionInformation.toMaskedString () ) );

        this.statistics = new StatisticsImpl ();
    }

    public void dispose ()
    {
        this.executor.shutdown ();
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return this.connectionInformation;
    }

    protected void setSessionProperties ( final Map<String, String> sessionProperties )
    {
        this.sessionProperties = sessionProperties;
    }

    public Map<String, String> getSessionProperties ()
    {
        return this.sessionProperties;
    }

    @Override
    public Collection<StatisticEntry> getStatistics ()
    {
        return this.statistics.getEntries ();
    }
}
