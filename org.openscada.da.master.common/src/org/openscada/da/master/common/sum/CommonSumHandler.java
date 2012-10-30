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

package org.openscada.da.master.common.sum;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.master.AbstractMasterHandlerImpl;
import org.openscada.da.master.MasterItem;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;

public class CommonSumHandler extends AbstractMasterHandlerImpl
{

    private final List<Entry> entries = new LinkedList<Entry> ();

    public CommonSumHandler ( final ObjectPoolTracker<MasterItem> poolTracker )
    {
        super ( poolTracker );
    }

    @Override
    public synchronized void update ( final UserInformation userInformation, final Map<String, String> parameters ) throws Exception
    {
        super.update ( userInformation, parameters );
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        final boolean debug = cfg.getBoolean ( "debug", false );

        this.entries.clear ();
        final String[] tags = cfg.getStringChecked ( "tag", "'tag' must be set" ).split ( ", ?" );
        for ( final String tag : tags )
        {
            final String prefix = cfg.getString ( String.format ( "tag.%s.prefix", tag ), "osgi.source" );
            final String suffix = cfg.getString ( String.format ( "tag.%s.suffix", tag ) );
            final String pattern = cfg.getString ( String.format ( "tag.%s.pattern", tag ) );
            this.entries.add ( new Entry ( tag, prefix, suffix, pattern, debug ) );
        }

        reprocess ();
    }

    @Override
    public void dataUpdate ( final Map<String, Object> context, final DataItemValue.Builder builder )
    {
        // convert source errors
        convertSource ( builder );

        for ( final Entry entry : this.entries )
        {
            entry.start ( context, builder );
        }

        // sum up
        for ( final Map.Entry<String, Variant> valueEntry : builder.getAttributes ().entrySet () )
        {
            final Variant pValue = valueEntry.getValue ();
            final String name = valueEntry.getKey ();

            for ( final Entry entry : this.entries )
            {
                entry.check ( name, pValue );
            }
        }

        for ( final Entry entry : this.entries )
        {
            entry.end ( context, builder );
        }

    }

    private void convertSource ( final Builder builder )
    {
        for ( final Entry entry : this.entries )
        {
            entry.convertSource ( builder );
        }
    }

}