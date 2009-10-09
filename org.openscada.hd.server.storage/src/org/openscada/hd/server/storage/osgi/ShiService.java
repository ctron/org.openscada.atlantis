package org.openscada.hd.server.storage.osgi;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.server.common.StorageHistoricalItem;
import org.openscada.hd.server.storage.ConfigurationImpl;
import org.openscada.hd.server.storage.ExtendedStorageChannel;
import org.openscada.hd.server.storage.calculation.CalculationMethod;
import org.openscada.hd.server.storage.datatypes.DoubleValue;
import org.openscada.hd.server.storage.datatypes.LongValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of StorageHistoricalItem as OSGi service.
 * @author Ludwig Straub
 */
public class ShiService implements StorageHistoricalItem
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( ShiService.class );

    private final ConfigurationImpl configuration;

    private final Map<CalculationMethod, ExtendedStorageChannel> storageChannels;

    private ExtendedStorageChannel rootStorageChannel;

    private boolean started;

    public ShiService ( ConfigurationImpl configuration )
    {
        this.configuration = configuration;
        storageChannels = new HashMap<CalculationMethod, ExtendedStorageChannel> ();
        rootStorageChannel = null;
        started = false;
    }

    public ConfigurationImpl getConfiguration ()
    {
        return configuration;
    }

    /**
     * @see org.openscada.hd.server.common.StorageHistoricalItem#createQuery
     */
    public Query createQuery ( QueryParameters parameters, QueryListener listener )
    {
        return null;
    }

    /**
     * @see org.openscada.hd.server.common.StorageHistoricalItem#getInformation
     */
    public HistoricalItemInformation getInformation ()
    {
        // FIXME: remove the whole method
        return null;
    }

    /**
     * @see org.openscada.hd.server.common.StorageHistoricalItem#updateData
     */
    public synchronized void updateData ( DataItemValue value )
    {
        if ( !started || ( rootStorageChannel == null ) || ( value == null ) )
        {
            return;
        }
        Variant variant = value.getValue ();
        if ( variant == null )
        {
            return;
        }
        final long time = value.getTimestamp ().getTimeInMillis ();
        final double qualityIndicator = !value.isConnected () || value.isError () ? 0 : 1;
        try
        {
            if ( variant.isLong () || variant.isInteger () || variant.isBoolean () )
            {
                rootStorageChannel.updateLong ( new LongValue ( time, qualityIndicator, variant.asLong ( 0L ) ) );
            }
            else
            {
                rootStorageChannel.updateDouble ( new DoubleValue ( time, qualityIndicator, variant.asDouble ( 0.0 ) ) );
            }
        }
        catch ( Exception e )
        {
            logger.error ( String.format ( "could not process value (%s)", variant ), e );
        }
    }

    public synchronized void addStorageChannel ( ExtendedStorageChannel storageChannel, CalculationMethod calculationMethod )
    {
        storageChannels.put ( calculationMethod, storageChannel );
        if ( calculationMethod == CalculationMethod.NATIVE )
        {
            rootStorageChannel = storageChannel;
        }
    }

    public synchronized void start ()
    {
        started = true;
    }

    public synchronized void stop ()
    {
        started = false;
    }
}
