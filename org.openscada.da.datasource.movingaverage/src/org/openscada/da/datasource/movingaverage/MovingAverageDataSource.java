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

package org.openscada.da.datasource.movingaverage;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.DataSourceListener;
import org.openscada.da.datasource.SingleDataSourceTracker;
import org.openscada.da.datasource.SingleDataSourceTracker.ServiceListener;
import org.openscada.da.datasource.base.DataInputSource;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovingAverageDataSource implements DataSourceListener
{
    private final static Logger logger = LoggerFactory.getLogger ( MovingAverageDataSource.class );

    private DataItemValueRange valueRange;

    private final ExecutorService executor;

    private final ScheduledExecutorService scheduler;

    private SingleDataSourceTracker dataSourceTracker;

    private String dataSourceId;

    private DataSource dataSource;

    private final String configurationId;

    private final ObjectPoolTracker<DataSource> poolTracker;

    private final ObjectPoolImpl<DataSource> dsObjectPool;

    private ScheduledFuture<?> triggerFuture;

    private long trigger;

    private long range;

    private long nullrange;

    private final DataInputSource minDataSource;

    private final DataInputSource maxDataSource;

    private final DataInputSource arithmeticDataSource;

    private final DataInputSource medianDataSource;

    private final DataInputSource weightedDataSource;

    private final DataInputSource deviationArithmeticDataSource;

    private final DataInputSource deviationWeightedDataSource;

    public MovingAverageDataSource ( final String configurationId, final ExecutorService executor, final ScheduledExecutorService scheduler, final ObjectPoolTracker<DataSource> poolTracker, final ObjectPoolImpl<DataSource> dsObjectPool ) throws InvalidSyntaxException
    {
        this.executor = executor;
        this.scheduler = scheduler;
        this.valueRange = new DataItemValueRange ( executor, 0 );
        this.configurationId = configurationId;
        this.poolTracker = poolTracker;
        this.dsObjectPool = dsObjectPool;
        this.minDataSource = new DataInputSource ( scheduler );
        this.maxDataSource = new DataInputSource ( scheduler );
        this.arithmeticDataSource = new DataInputSource ( scheduler );
        this.medianDataSource = new DataInputSource ( scheduler );
        this.weightedDataSource = new DataInputSource ( scheduler );
        this.deviationArithmeticDataSource = new DataInputSource ( scheduler );
        this.deviationWeightedDataSource = new DataInputSource ( scheduler );

        {
            final String id = this.configurationId + ".min";
            final Dictionary<String, String> properties = new Hashtable<String, String> ( 1 );
            properties.put ( DataSource.DATA_SOURCE_ID, id );
            this.dsObjectPool.addService ( id, this.minDataSource, null );
        }
        {
            final String id = this.configurationId + ".max";
            final Dictionary<String, String> properties = new Hashtable<String, String> ( 1 );
            properties.put ( DataSource.DATA_SOURCE_ID, id );
            this.dsObjectPool.addService ( id, this.maxDataSource, null );
        }
        {
            final String id = this.configurationId + ".arithmetic";
            final Dictionary<String, String> properties = new Hashtable<String, String> ( 1 );
            properties.put ( DataSource.DATA_SOURCE_ID, id );
            this.dsObjectPool.addService ( id, this.arithmeticDataSource, null );
        }
        {
            final String id = this.configurationId + ".median";
            final Dictionary<String, String> properties = new Hashtable<String, String> ( 1 );
            properties.put ( DataSource.DATA_SOURCE_ID, id );
            this.dsObjectPool.addService ( id, this.medianDataSource, null );
        }
        {
            final String id = this.configurationId + ".weighted";
            final Dictionary<String, String> properties = new Hashtable<String, String> ( 1 );
            properties.put ( DataSource.DATA_SOURCE_ID, id );
            this.dsObjectPool.addService ( id, this.weightedDataSource, null );
        }
        {
            final String id = this.configurationId + ".deviationArithmetic";
            final Dictionary<String, String> properties = new Hashtable<String, String> ( 1 );
            properties.put ( DataSource.DATA_SOURCE_ID, id );
            this.dsObjectPool.addService ( id, this.deviationArithmeticDataSource, null );
        }
        {
            final String id = this.configurationId + ".deviationWeighted";
            final Dictionary<String, String> properties = new Hashtable<String, String> ( 1 );
            properties.put ( DataSource.DATA_SOURCE_ID, id );
            this.dsObjectPool.addService ( id, this.deviationWeightedDataSource, null );
        }
    }

    public void dispose ()
    {
        this.dsObjectPool.removeService ( this.configurationId + ".min", this.minDataSource );
        this.dsObjectPool.removeService ( this.configurationId + ".max", this.maxDataSource );
        this.dsObjectPool.removeService ( this.configurationId + ".arithmetic", this.arithmeticDataSource );
        this.dsObjectPool.removeService ( this.configurationId + ".median", this.medianDataSource );
        this.dsObjectPool.removeService ( this.configurationId + ".weighted", this.weightedDataSource );
        this.dsObjectPool.removeService ( this.configurationId + ".deviationArithmetic", this.deviationArithmeticDataSource );
        this.dsObjectPool.removeService ( this.configurationId + ".deviationWeighted", this.deviationWeightedDataSource );
    }

    public void update ( final Map<String, String> parameters ) throws InvalidSyntaxException
    {
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        this.dataSourceId = cfg.getString ( "datasource.id", null ); //$NON-NLS-1$
        this.trigger = cfg.getLong ( "trigger", 60 ); //$NON-NLS-1$
        this.range = cfg.getLong ( "range", 60 * 60 ); //$NON-NLS-1$
        this.nullrange = cfg.getLong ( "nullrange", 60 * 30 ); //$NON-NLS-1$

        handleChange ();
    }

    private void handleChange () throws InvalidSyntaxException
    {
        if ( this.triggerFuture != null )
        {
            this.triggerFuture.cancel ( false );
        }
        this.valueRange = new DataItemValueRange ( this.executor, this.range * 1000 );
        updateDataSource ();
        this.triggerFuture = this.scheduler.scheduleAtFixedRate ( new Runnable () {
            @Override
            public void run ()
            {
                try
                {
                    MovingAverageDataSource.this.valueRange.checkRange ();
                    updateValues ();
                }
                catch ( final Exception e )
                {
                    logger.error ( "failed to run checkRange () or call updateValues ()", e );
                }
            }
        }, this.trigger, this.trigger, TimeUnit.SECONDS );
        try
        {
            updateValues ();
        }
        catch ( final Exception e )
        {
            logger.error ( "failed to update values", e );
        }
    }

    @Override
    public void stateChanged ( final DataItemValue value )
    {
        try
        {
            this.valueRange.add ( DataItemValueLight.valueOf ( value ) );
            updateValues ();
        }
        catch ( Exception e )
        {
            logger.error ( "failed to add DataItemValue or to call updateValues ()", e );
        }
    }

    private void updateValues ()
    {
        final DataItemValueRange.DataItemValueRangeState state = this.valueRange.getState ();
        final AverageValues average = new AverageValues ();
        if ( state.getSize () == 0 )
        {
            // we don't have values fitting within time frame, just use last available
            if ( state.getFirstValue ().hasValue () )
            {
                average.min = state.getFirstValue ().getValue ().asDouble ( 0.0 );
                average.max = state.getFirstValue ().getValue ().asDouble ( 0.0 );
                average.arithmetic = state.getFirstValue ().getValue ().asDouble ( 0.0 );
                average.median = state.getFirstValue ().getValue ().asDouble ( 0.0 );
                average.weighted = state.getFirstValue ().getValue ().asDouble ( 0.0 );
                average.deviationArithmetic = 0.0;
                average.deviationWeighted = 0.0;
            }
        }
        else
        {
            // ok, so we have at least one value in our list (could still be null)
            DataItemValueLight lastValue = new DataItemValueLight ( state.getFirstValue ().getValue (), state.getFirstValue ().getSubscriptionState (), state.getOldestPossibleTimestamp () );
            final Iterator<DataItemValueLight> it = state.getValues ().iterator (); // it is a set, so we have to use an iterator
            for ( int i = 0; i < ( state.getSize () + 1 ); i++ )
            {
                if ( i < state.getSize () )
                {
                    final DataItemValueLight divl = it.next ();
                    final long currentRange = divl.getTimestamp () - lastValue.getTimestamp ();
                    calculateForRange ( average, currentRange, lastValue.getValue () );
                    lastValue = divl;
                }
                else
                {
                    final long currentRange = ( state.getOldestPossibleTimestamp () + this.valueRange.getRange () ) - lastValue.getTimestamp ();
                    calculateForRange ( average, currentRange, lastValue.getValue () );
                }
            }

            // now calculate actual arithmetic average
            if ( average.values.size () > 0 )
            {
                average.arithmetic = average.arithmetic / average.values.size ();
            }

            // now get median
            if ( average.values.size () > 0 )
            {
                average.median = average.values.get ( average.values.size () / 2 );
            }
            // calculate actual weighted value
            if ( average.weighted != null )
            {
                average.weighted = average.weighted / this.valueRange.getRange ();
            }

            // calculate deviation
            if ( !average.values.isEmpty () )
            {
                double da = 0.0;
                double dw = 0.0;
                for ( double v : average.values )
                {
                    da += Math.pow ( v - average.arithmetic, 2.0 );
                    dw += Math.pow ( v - average.weighted, 2.0 );
                }
                average.deviationArithmetic = Math.sqrt ( da / average.values.size () );
                average.deviationWeighted = Math.sqrt ( dw / average.values.size () );
            }

            // handle null range
            if ( average.nullRange >= ( this.nullrange * 1000 ) )
            {
                average.arithmetic = null;
                average.median = null;
                average.weighted = null;
                average.deviationArithmetic = null;
                average.deviationWeighted = null;
            }
        }

        this.minDataSource.setValue ( new DataItemValue.Builder ().setSubscriptionState ( SubscriptionState.CONNECTED ).setValue ( Variant.valueOf ( average.min ) ).build () );
        this.maxDataSource.setValue ( new DataItemValue.Builder ().setSubscriptionState ( SubscriptionState.CONNECTED ).setValue ( Variant.valueOf ( average.max ) ).build () );
        this.arithmeticDataSource.setValue ( new DataItemValue.Builder ().setSubscriptionState ( SubscriptionState.CONNECTED ).setValue ( Variant.valueOf ( average.arithmetic ) ).build () );
        this.medianDataSource.setValue ( new DataItemValue.Builder ().setSubscriptionState ( SubscriptionState.CONNECTED ).setValue ( Variant.valueOf ( average.median ) ).build () );
        this.weightedDataSource.setValue ( new DataItemValue.Builder ().setSubscriptionState ( SubscriptionState.CONNECTED ).setValue ( Variant.valueOf ( average.weighted ) ).build () );
        this.deviationArithmeticDataSource.setValue ( new DataItemValue.Builder ().setSubscriptionState ( SubscriptionState.CONNECTED ).setValue ( Variant.valueOf ( average.deviationArithmetic ) ).build () );
        this.deviationWeightedDataSource.setValue ( new DataItemValue.Builder ().setSubscriptionState ( SubscriptionState.CONNECTED ).setValue ( Variant.valueOf ( average.deviationWeighted ) ).build () );
    }

    private void calculateForRange ( final AverageValues average, final long currentRange, final Variant value )
    {
        if ( !value.isNumber () )
        {
            average.nullRange += currentRange;
        }
        else
        {
            final double d = value.asDouble ( 0.0 );

            average.min = average.min == null ? d : average.min;
            average.max = average.max == null ? d : average.max;
            average.arithmetic = average.arithmetic == null ? 0.0 : average.arithmetic;
            average.weighted = average.weighted == null ? 0.0 : average.weighted;

            average.min = d < average.min ? d : average.min;
            average.max = d > average.max ? d : average.max;
            average.arithmetic += d;
            average.values.add ( d );
            average.weighted += d * currentRange;
        }
    }

    private void updateDataSource () throws InvalidSyntaxException
    {
        logger.debug ( "updateDataSource ()" );
        if ( this.dataSourceTracker != null )
        {
            this.dataSourceTracker.close ();
            this.dataSourceTracker = null;
        }
        if ( this.dataSourceId != null )
        {
            logger.debug ( "track datasource " + this.dataSourceId );
            this.dataSourceTracker = new SingleDataSourceTracker ( this.poolTracker, this.dataSourceId, new ServiceListener () {
                @Override
                public void dataSourceChanged ( final DataSource dataSource )
                {
                    setDataSource ( dataSource );
                }
            } );
            this.dataSourceTracker.open ();
        }
    }

    private void setDataSource ( final DataSource dataSource )
    {
        logger.info ( "Set data source item: {}", dataSource );

        if ( this.dataSource != null )
        {
            this.dataSource.removeListener ( this );
        }
        this.dataSource = dataSource;
        if ( this.dataSource != null )
        {
            this.dataSource.addListener ( this );
        }
    }

    private class AverageValues
    {
        public Double min;

        public Double max;

        public Double arithmetic;

        public Double median;

        public Double weighted;

        public Double deviationArithmetic;

        public Double deviationWeighted;

        public long nullRange = 0;

        public LinkedList<Double> values = new LinkedList<Double> ();
    }
}
