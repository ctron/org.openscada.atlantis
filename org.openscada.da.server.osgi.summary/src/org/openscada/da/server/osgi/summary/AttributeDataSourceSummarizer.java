/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.osgi.summary;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.master.MasterItem;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.openscada.utils.str.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeDataSourceSummarizer extends AbstractDataSourceSummarizer
{

    private final static Logger logger = LoggerFactory.getLogger ( AttributeDataSourceSummarizer.class );

    private final Set<DataSource> matchingSources = new HashSet<DataSource> ();

    private final Builder countValue = new Builder ();

    private String attributeName;

    private boolean onlyMaster;

    public AttributeDataSourceSummarizer ( final Executor executor, final ObjectPoolTracker tracker )
    {
        super ( executor, tracker );
        this.countValue.setSubscriptionState ( SubscriptionState.CONNECTED );
        this.countValue.setValue ( Variant.valueOf ( 0 ) );
        updateData ( this.countValue.build () );
    }

    @Override
    protected void handleAdding ( final DataSource source )
    {
    }

    @Override
    protected synchronized void handleRemoved ( final DataSource source )
    {
        if ( this.matchingSources.remove ( source ) )
        {
            updateStats ();
        }
    }

    @Override
    protected synchronized void handleStateChange ( final DataSource source, final DataItemValue value )
    {
        if ( isMatch ( source, value ) )
        {
            if ( this.matchingSources.add ( source ) )
            {
                updateStats ();
            }
        }
        else
        {
            if ( this.matchingSources.remove ( source ) )
            {
                updateStats ();
            }
        }
    }

    protected boolean isMatch ( final DataSource source, final DataItemValue value )
    {
        if ( ! ( source instanceof MasterItem ) && this.onlyMaster )
        {
            return false;
        }

        if ( value == null )
        {
            return false;
        }
        if ( value.getAttributes () == null )
        {
            return false;
        }
        final Variant attrValue = value.getAttributes ().get ( this.attributeName );
        if ( attrValue == null )
        {
            return false;
        }
        return attrValue.asBoolean ();
    }

    protected void updateStats ()
    {
        final int count = this.matchingSources.size ();

        logger.debug ( "Update size: {}", count );

        this.countValue.setValue ( Variant.valueOf ( count ) );
        if ( count < 10 )
        {
            this.countValue.setAttribute ( "debug.info", Variant.valueOf ( StringHelper.join ( this.matchingSources, "," ) ) );
        }
        else
        {
            this.countValue.clearAttribute ( "debug.info" );
        }
        updateData ( this.countValue.build () );
    }

    public void dispose ()
    {
        close ();
    }

    public void update ( final Map<String, String> parameters ) throws Exception
    {
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        close ();

        this.attributeName = cfg.getStringChecked ( "attribute", "'attribute' must be set" );
        this.onlyMaster = cfg.getBoolean ( "onlyMaster", false );

        open ();
    }

}
