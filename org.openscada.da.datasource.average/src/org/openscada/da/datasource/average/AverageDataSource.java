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

package org.openscada.da.datasource.average;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.DataSourceListener;
import org.openscada.da.datasource.MultiDataSourceTracker;
import org.openscada.da.datasource.MultiDataSourceTracker.ServiceListener;
import org.openscada.da.datasource.base.DataInputSource;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AverageDataSource implements ServiceListener
{
    private final static Logger logger = LoggerFactory.getLogger ( AverageDataSource.class );

    private final ObjectPoolTracker<DataSource> poolTracker;

    private MultiDataSourceTracker tracker;

    private Set<String> sourceIds;

    private final ConcurrentMap<DataSource, DataSourceHandler> sources = new ConcurrentHashMap<DataSource, DataSourceHandler> ();

    private int noOfValidSourcesRequired = 0;

    private final String configurationId;

    private final ObjectPoolImpl<DataSource> dsObjectPool;

    private final DataInputSource sumDataSource;

    private final DataInputSource minDataSource;

    private final DataInputSource maxDataSource;

    private final DataInputSource meanDataSource;

    private final DataInputSource medianDataSource;

    private final DataInputSource deviationDataSource;

    public AverageDataSource ( final String configurationId, final ObjectPoolTracker<DataSource> poolTracker, final ExecutorService executor, final ObjectPoolImpl<DataSource> dsObjectPool )
    {
        this.poolTracker = poolTracker;
        this.configurationId = configurationId;
        this.dsObjectPool = dsObjectPool;

        this.sumDataSource = new DataInputSource ( executor );
        this.minDataSource = new DataInputSource ( executor );
        this.maxDataSource = new DataInputSource ( executor );
        this.meanDataSource = new DataInputSource ( executor );
        this.medianDataSource = new DataInputSource ( executor );
        this.deviationDataSource = new DataInputSource ( executor );

        {
            final String id = this.configurationId + ".min";
            final Dictionary<String, String> properties = new Hashtable<String, String> ( 1 );
            properties.put ( DataSource.DATA_SOURCE_ID, id );
            this.dsObjectPool.addService ( id, this.minDataSource, null );
        }
        {
            final String id = this.configurationId + ".sum";
            final Dictionary<String, String> properties = new Hashtable<String, String> ( 1 );
            properties.put ( DataSource.DATA_SOURCE_ID, id );
            this.dsObjectPool.addService ( id, this.sumDataSource, null );
        }
        {
            final String id = this.configurationId + ".max";
            final Dictionary<String, String> properties = new Hashtable<String, String> ( 1 );
            properties.put ( DataSource.DATA_SOURCE_ID, id );
            this.dsObjectPool.addService ( id, this.maxDataSource, null );
        }
        {
            final String id = this.configurationId + ".mean";
            final Dictionary<String, String> properties = new Hashtable<String, String> ( 1 );
            properties.put ( DataSource.DATA_SOURCE_ID, id );
            this.dsObjectPool.addService ( id, this.meanDataSource, null );
        }
        {
            final String id = this.configurationId + ".median";
            final Dictionary<String, String> properties = new Hashtable<String, String> ( 1 );
            properties.put ( DataSource.DATA_SOURCE_ID, id );
            this.dsObjectPool.addService ( id, this.medianDataSource, null );
        }
        {
            final String id = this.configurationId + ".deviation";
            final Dictionary<String, String> properties = new Hashtable<String, String> ( 1 );
            properties.put ( DataSource.DATA_SOURCE_ID, id );
            this.dsObjectPool.addService ( id, this.deviationDataSource, null );
        }
    }

    public void dispose ()
    {
        this.dsObjectPool.removeService ( this.configurationId + ".sum", this.sumDataSource );
        this.dsObjectPool.removeService ( this.configurationId + ".min", this.minDataSource );
        this.dsObjectPool.removeService ( this.configurationId + ".max", this.maxDataSource );
        this.dsObjectPool.removeService ( this.configurationId + ".mean", this.meanDataSource );
        this.dsObjectPool.removeService ( this.configurationId + ".median", this.medianDataSource );
        this.dsObjectPool.removeService ( this.configurationId + ".deviation", this.deviationDataSource );

        for ( final DataSourceHandler handler : this.sources.values () )
        {
            handler.dispose ();
        }

        if ( this.tracker != null )
        {
            this.tracker.close ();
            this.tracker = null;
        }
    }

    public void update ( final Map<String, String> properties ) throws Exception
    {
        setSources ( properties.get ( "sources" ) );
        setValidSourcesRequired ( properties.get ( "validSourcesRequired" ) );
        handleChange ();
    }

    private void setSources ( final String str ) throws InvalidSyntaxException
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
            this.tracker = null;
        }

        for ( final DataSourceHandler source : this.sources.values () )
        {
            source.dispose ();
        }
        this.sources.clear ();
        this.sourceIds = convertSources ( str );

        if ( this.sourceIds.isEmpty () )
        {
            // nothing to do if we don't have any source
            return;
        }

        this.tracker = new MultiDataSourceTracker ( this.poolTracker, this.sourceIds, this );
        this.tracker.open ();
    }

    private Set<String> convertSources ( final String sources )
    {
        if ( sources == null )
        {
            throw new IllegalArgumentException ( "'sources' must be set" );
        }

        return new LinkedHashSet<String> ( Arrays.asList ( sources.split ( "[, ]+" ) ) );
    }

    private void setValidSourcesRequired ( final String validSourcesRequired )
    {
        // special case empty:
        // only if all items are invalid, the resultant value is also invalid
        if ( validSourcesRequired == null || validSourcesRequired.isEmpty () )
        {
            this.noOfValidSourcesRequired = 0;
            return;
        }
        // special case 100%:
        // all values must be valid to have a valid result
        if ( validSourcesRequired.equals ( "100%" ) )
        {
            this.noOfValidSourcesRequired = this.sourceIds.size ();
            return;
        }
        try
        {
            if ( validSourcesRequired.endsWith ( "%" ) )
            {
                // handle percentages
                final int percent = Integer.parseInt ( validSourcesRequired.replace ( "%", "" ) );
                if ( percent >= 100 )
                {
                    this.noOfValidSourcesRequired = this.sourceIds.size ();
                    return;
                }
                this.noOfValidSourcesRequired = Long.valueOf ( Math.round ( percent * this.sourceIds.size () / 100.0 ) ).intValue ();
            }
            else
            {
                // handle absolute values
                this.noOfValidSourcesRequired = Integer.parseInt ( validSourcesRequired );
                if ( this.noOfValidSourcesRequired > this.sourceIds.size () )
                {
                    logger.warn ( "'validSourcesRequired' was '{}' which should be less then the number of all elements", validSourcesRequired, this.sourceIds.size () );
                    this.noOfValidSourcesRequired = this.sourceIds.size ();
                }
            }
        }
        catch ( final NumberFormatException e )
        {
            logger.warn ( "'validSourcesRequired' was '{}' which could not be parsed", validSourcesRequired, e );
            // re-throw for the CA to get notified
            throw e;
        }
    }

    protected void handleChange ()
    {
        final ArrayList<Double> validValues = new ArrayList<Double> ( this.sources.size () );

        Double sum = null;
        Double min = null;
        Double max = null;
        Double mean = null;
        Double median = null;
        Double deviation = null;
        for ( final DataSourceHandler handler : this.sources.values () )
        {
            final DataItemValue div = handler.getValue ();
            if ( div != null && div.isConnected () && !div.isError () )
            {
                if ( div.getValue () != null && div.getValue ().isNumber () )
                {
                    final Double d = div.getValue ().asDouble ( 0.0 );
                    min = min == null ? d : d < min ? d : min;
                    max = max == null ? d : d > max ? d : max;
                    sum = sum == null ? d : sum + d;
                    validValues.add ( d );
                }
            }
        }

        if ( validValues.size () > 0 )
        {
            mean = sum / validValues.size ();
            median = validValues.get ( validValues.size () / 2 );
            Double dd = 0.0;
            for ( final Double d : validValues )
            {
                dd += Math.pow ( d - mean, 2 );
            }
            deviation = Math.sqrt ( dd / validValues.size () );
        }

        if ( validValues.size () < this.noOfValidSourcesRequired )
        {
            sum = null;
            mean = null;
            median = null;
            deviation = null;
        }

        this.sumDataSource.setValue ( new DataItemValue.Builder ().setSubscriptionState ( SubscriptionState.CONNECTED ).setValue ( Variant.valueOf ( sum ) ).build () );
        this.minDataSource.setValue ( new DataItemValue.Builder ().setSubscriptionState ( SubscriptionState.CONNECTED ).setValue ( Variant.valueOf ( min ) ).build () );
        this.maxDataSource.setValue ( new DataItemValue.Builder ().setSubscriptionState ( SubscriptionState.CONNECTED ).setValue ( Variant.valueOf ( max ) ).build () );
        this.meanDataSource.setValue ( new DataItemValue.Builder ().setSubscriptionState ( SubscriptionState.CONNECTED ).setValue ( Variant.valueOf ( mean ) ).build () );
        this.medianDataSource.setValue ( new DataItemValue.Builder ().setSubscriptionState ( SubscriptionState.CONNECTED ).setValue ( Variant.valueOf ( median ) ).build () );
        this.deviationDataSource.setValue ( new DataItemValue.Builder ().setSubscriptionState ( SubscriptionState.CONNECTED ).setValue ( Variant.valueOf ( deviation ) ).build () );
    }

    private class DataSourceHandler implements DataSourceListener
    {
        private final DataSource dataSource;

        private DataItemValue value;

        DataSourceHandler ( final DataSource dataSource )
        {
            this.dataSource = dataSource;

            dataSource.addListener ( this );
        }

        public void dispose ()
        {
            this.dataSource.removeListener ( this );
        }

        public DataItemValue getValue ()
        {
            return this.value;
        }

        @Override
        public void stateChanged ( final DataItemValue value )
        {
            this.value = value;
            handleChange ();
        }
    }

    private synchronized void addSource ( final DataSource dataSource )
    {
        logger.info ( "Adding source: {}", new Object[] { dataSource } );

        final DataSourceHandler handler = new DataSourceHandler ( dataSource );

        final DataSourceHandler oldHandler = this.sources.put ( dataSource, handler );
        if ( oldHandler != null )
        {
            oldHandler.dispose ();
        }

        handleChange ();
    }

    private synchronized void updateSource ( final DataSource dataSource )
    {
        logger.info ( "Updating source: {} / {}", new Object[] { dataSource } );

        final DataSourceHandler handler = this.sources.get ( dataSource );
        if ( handler != null )
        {
            handleChange ();
        }
    }

    private synchronized void removeSource ( final DataSource dataSource )
    {
        logger.info ( "Removing source: {}", dataSource );

        final DataSourceHandler handler = this.sources.remove ( dataSource );
        if ( handler != null )
        {
            handler.dispose ();
            handleChange ();
        }
    }

    @Override
    public void dataSourceAdded ( final String id, final Dictionary<?, ?> properties, final DataSource dataSource )
    {
        addSource ( dataSource );
    }

    @Override
    public void dataSourceRemoved ( final String id, final Dictionary<?, ?> properties, final DataSource dataSource )
    {
        updateSource ( dataSource );
    }

    @Override
    public void dataSourceModified ( final String id, final Dictionary<?, ?> properties, final DataSource dataSource )
    {
        removeSource ( dataSource );
    }
}
