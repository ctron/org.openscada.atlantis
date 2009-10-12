package org.openscada.hd.server.storage.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.hd.server.storage.StorageChannelMetaData;
import org.openscada.hd.server.storage.backend.comparator.InverseTimeOrderComparator;
import org.openscada.hd.server.storage.calculation.CalculationMethod;
import org.openscada.hd.server.storage.datatypes.LongValue;
import org.openscada.hd.server.storage.relict.RelictCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This backend implementation is able to handle multiple storage channel backend objects.
 * It is arranged that each such backend object is responsible for its own exclusive time span.
 * @author Ludwig Straub
 */
public class BackEndMultiplexor implements BackEnd, RelictCleaner
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( BackEndMultiplexor.class );

    /** Reprieve time in milliseconds old data is not deleted althrough the proposed data age is reached. */
    private final static long REPRIEVE = 3000;

    /** Metadata of the storage channel. */
    private StorageChannelMetaData metaData;

    /** Factory that is used to create new fractal backend objects. */
    private final BackEndFactory backEndFactory;

    /** List of currently available backend objects. The elements are sorted by time span. The latest element is placed first. */
    private final List<BackEnd> backEnds;

    /** Amount of milliseconds that can be contained by any newly created storage channel backend. */
    private final long newBackendTimespan;

    /** Flag indicating whether the instance has been initialized or not. */
    private boolean initialized;

    /**
     * Constructor.
     * @param backEndFactory factory that is used to create new fractal backend objects
     * @param newBackendTimespan timespan that is used when a new backend fragment has to be created
     */
    public BackEndMultiplexor ( final BackEndFactory backEndFactory, final long newBackendTimespan )
    {
        this.backEndFactory = backEndFactory;
        this.newBackendTimespan = newBackendTimespan < 1 ? 1 : newBackendTimespan;
        backEnds = new LinkedList<BackEnd> ();
        initialized = false;
    }

    /**
     * @see org.openscada.hd.server.storage.backend.BackEnd#create
     */
    public synchronized void create ( final StorageChannelMetaData storageChannelMetaData ) throws Exception
    {
        // assure that no old data exists
        if ( backEndFactory.getExistingBackEnds ( storageChannelMetaData.getConfigurationId (), storageChannelMetaData.getDetailLevelId (), storageChannelMetaData.getCalculationMethod () ).length > 0 )
        {
            String message = String.format ( "data already exists for combination! (configuration id: '%s'; detail level: '%d'; calculation method: '%s')", storageChannelMetaData.getConfigurationId (), storageChannelMetaData.getDetailLevelId (), CalculationMethod.convertCalculationMethodToString ( storageChannelMetaData.getCalculationMethod () ) );
            logger.error ( message );
            throw new Exception ( message );
        }

        // create new backend
        getBackEnd ( storageChannelMetaData.getStartTime () );
    }

    /**
     * @see org.openscada.hd.server.storage.backend.BackEnd#initialize
     */
    public synchronized void initialize ( final StorageChannelMetaData storageChannelMetaData ) throws Exception
    {
        deinitialize ();
        backEnds.clear ();
        BackEnd[] backEndArray = backEndFactory.getExistingBackEnds ( storageChannelMetaData.getConfigurationId (), storageChannelMetaData.getDetailLevelId (), storageChannelMetaData.getCalculationMethod () );
        initialized = true;
        Arrays.sort ( backEndArray, new InverseTimeOrderComparator () );
        backEnds.addAll ( Arrays.asList ( backEndArray ) );
        for ( BackEnd backEnd : backEnds )
        {
            backEnd.initialize ( storageChannelMetaData );
        }
        metaData = new StorageChannelMetaData ( storageChannelMetaData );
    }

    /**
     * @see org.openscada.hd.server.storage.relict.RelictCleaner#cleanupRelicts
     */
    public synchronized void cleanupRelicts ()
    {
        logger.info ( "deleting old data... start" );
        if ( metaData == null )
        {
            return;
        }
        final long proposedDataAge = System.currentTimeMillis () - metaData.getProposedDataAge () - REPRIEVE;
        for ( int i = backEnds.size () - 1; i >= 0; i-- )
        {
            BackEnd backEnd = backEnds.get ( i );
            if ( backEnd != null )
            {
                StorageChannelMetaData subMetaData = null;
                try
                {
                    subMetaData = backEnd.getMetaData ();
                    if ( ( subMetaData == null ) || ( subMetaData.getEndTime () <= proposedDataAge ) )
                    {
                        try
                        {
                            backEnd.delete ();
                        }
                        catch ( Exception e1 )
                        {
                            logger.warn ( String.format ( "relict data (%s) could not be deleted by BackEndMultiplexor (%s)! ", subMetaData, metaData ), e1 );
                        }
                        backEnds.remove ( i );
                    }
                    else
                    {
                        // since the array of back ends is sorted, no older entries will be found during further iteration steps
                        break;
                    }
                }
                catch ( Exception e )
                {
                    logger.warn ( String.format ( "metadata of sub backend could not be accessed! (%s)", metaData ), e );
                }
            }
        }
        backEnds.clear ();
        logger.info ( "deleting old data... end" );
    }

    /**
     * @see org.openscada.hd.server.storage.backend.BackEnd#getMetaData
     */
    public synchronized StorageChannelMetaData getMetaData () throws Exception
    {
        if ( metaData == null )
        {
            String message = "method getMetaData must not be called when instance is not initialized";
            logger.error ( message );
            throw new Exception ( message );
        }
        return metaData;
    }

    /**
     * @see org.openscada.hd.server.storage.backend.BackEnd#isTimeSpanConstant
     */
    public boolean isTimeSpanConstant ()
    {
        return false;
    }

    /**
     * @see org.openscada.hd.server.storage.backend.BackEnd#deinitialize
     */
    public synchronized void deinitialize () throws Exception
    {
        initialized = false;
        for ( BackEnd backEnd : backEnds )
        {
            try
            {
                backEnd.deinitialize ();
            }
            catch ( Exception e )
            {
                logger.warn ( String.format ( "sub back end of '%s' could not be deinitialized", metaData ), e );
            }
        }
        metaData = null;
    }

    /**
     * @see org.openscada.hd.server.storage.backend.BackEnd#delete
     */
    public synchronized void delete () throws Exception
    {
        for ( BackEnd backEnd : backEnds )
        {
            backEnd.delete ();
        }
        backEnds.clear ();
    }

    /**
     * This method assures that the instance is initialized.
     * @throws Exception if the instance is not initialized
     */
    private void assureInitialized () throws Exception
    {
        if ( !initialized )
        {
            String message = String.format ( "back end (%s) is not properly initialized!", metaData );
            logger.error ( message );
            throw new Exception ( message );
        }
    }

    /**
     * This method creates a new back end object using the back end factory, creates and initializes the object's data, adds the object to the back end list and returns a reference to the newly created object.
     * @param startTime start time of the newly created back end object
     * @param endTime end time of the newly created back end object
     * @param index insertion index of the new object in the list of back end objects
     * @return reference to the newly created object
     * @throws Exception in case of any problem
     */
    private BackEnd createAndAddNewBackEnd ( final long startTime, final long endTime, final int index ) throws Exception
    {
        final StorageChannelMetaData storageChannelMetaData = new StorageChannelMetaData ( metaData );
        storageChannelMetaData.setStartTime ( startTime );
        storageChannelMetaData.setEndTime ( endTime );
        final BackEnd backEnd = backEndFactory.createNewBackEnd ( storageChannelMetaData );
        backEnd.create ( storageChannelMetaData );
        backEnd.initialize ( storageChannelMetaData );
        backEnds.add ( index, backEnd );
        metaData.setStartTime ( Math.min ( metaData.getStartTime (), storageChannelMetaData.getStartTime () ) );
        metaData.setEndTime ( Math.min ( metaData.getEndTime (), storageChannelMetaData.getEndTime () ) );
        return backEnd;
    }

    /**
     * This method returns the backend that is able to process data with the passed timestamp.
     * If no suitable backend currently exists, a new one will be created using the backend factory.
     * @param timestamp timestam for which a storage channel backend has to be retrieved
     * @return backend that is able to process data with the passed timestamp
     * @throws Exception in case of any problem
     */
    private BackEnd getBackEnd ( final long timestamp ) throws Exception
    {
        // search within the array of existing storage channel backends for a suitable channel
        long maxEndTime = Long.MAX_VALUE;
        final long size = backEnds.size ();
        BackEnd backEnd = null;
        for ( int i = 0; i < size; i++ )
        {
            backEnd = backEnds.get ( i );
            final StorageChannelMetaData metaData = backEnd.getMetaData ();
            final long startTime = metaData.getStartTime ();
            if ( startTime <= timestamp )
            {
                // check if an existing backend can be used
                long endTime = metaData.getEndTime ();
                if ( endTime > timestamp )
                {
                    return backEnd;
                }

                // calculate start time for the new storage channel backend fragment
                while ( ( endTime + this.newBackendTimespan ) <= timestamp )
                {
                    endTime += this.newBackendTimespan;
                    if ( endTime > maxEndTime )
                    {
                        String message = "logic error! end time cannot be before start time when creating a new storage channel backend fragment";
                        logger.error ( message );
                        throw new Exception ( message );
                    }
                }

                // a new backend has to be created
                return createAndAddNewBackEnd ( endTime, Math.min ( endTime + this.newBackendTimespan, maxEndTime ), i );
            }
            maxEndTime = startTime;
        }

        // create a new backend channel with a completely independent timespan, since no channel exists
        // as start time, a time not too far in the past is chosen, since older data might be processed in the future
        final long startTime = timestamp - this.newBackendTimespan / 10;
        return createAndAddNewBackEnd ( startTime, startTime + this.newBackendTimespan, backEnds.size () );
    }

    /**
     * This method removes backend objects from the internal list.
     * @param backEndsToRemove backend objects that have to be removed
     */
    private void removeBackEnds ( List<BackEnd> backEndsToRemove )
    {
        for ( BackEnd backEnd : backEndsToRemove )
        {
            try
            {
                backEnds.remove ( backEnd );
                backEnd.deinitialize ();
            }
            catch ( Exception e )
            {
            }
        }
    }

    /**
     * @see org.openscada.hd.server.storage.StorageChannel#updateLong
     */
    public synchronized void updateLong ( final LongValue longValue ) throws Exception
    {
        assureInitialized ();
        if ( longValue != null )
        {
            try
            {
                getBackEnd ( longValue.getTime () ).updateLong ( longValue );
            }
            catch ( Exception e )
            {
                logger.error ( String.format ( "backend (%s): could not write to sub backend (startTime: %s)", metaData, longValue.getTime () ), e );
            }
        }
    }

    /**
     * @see org.openscada.hd.server.storage.StorageChannel#updateLongs
     */
    public synchronized void updateLongs ( final LongValue[] longValues ) throws Exception
    {
        assureInitialized ();
        if ( longValues != null )
        {
            // assign all long values to the backend that is responsible for their processing
            final Map<Long, List<LongValue>> backends = new HashMap<Long, List<LongValue>> ();
            for ( LongValue longValue : longValues )
            {
                long startTime = 0L;
                try
                {
                    startTime = getBackEnd ( longValue.getTime () ).getMetaData ().getStartTime ();
                }
                catch ( Exception e )
                {
                    logger.error ( String.format ( "backend (%s): could not access sub backend (startTime: %s)", metaData, longValue.getTime () ), e );
                }
                List<LongValue> longValuesToProcess = backends.get ( startTime );
                if ( longValuesToProcess == null )
                {
                    longValuesToProcess = new ArrayList<LongValue> ();
                    backends.put ( startTime, longValuesToProcess );
                }
                longValuesToProcess.add ( longValue );
            }

            // process the ordered long values as bulk
            for ( Map.Entry<Long, List<LongValue>> entry : backends.entrySet () )
            {
                try
                {
                    getBackEnd ( entry.getKey () ).updateLongs ( entry.getValue ().toArray ( EMPTY_LONGVALUE_ARRAY ) );
                }
                catch ( Exception e )
                {
                    logger.error ( String.format ( "backend (%s): could not write to sub backend (startTime: %s)", metaData, entry.getKey () ), e );
                }
            }
        }
    }

    /**
     * @see org.openscada.hd.server.storage.StorageChannel#getLongValues
     */
    public synchronized LongValue[] getLongValues ( final long startTime, final long endTime ) throws Exception
    {
        // assure that the current state is valid
        assureInitialized ();

        // collect result data
        final List<LongValue> longValues = new LinkedList<LongValue> ();
        List<BackEnd> backEndsToRemove = new ArrayList<BackEnd> ();
        for ( BackEnd backEnd : backEnds )
        {
            try
            {
                final StorageChannelMetaData metaData = backEnd.getMetaData ();
                if ( ( metaData.getStartTime () < endTime ) && ( metaData.getEndTime () > startTime ) )
                {
                    // process values that match the time span
                    longValues.addAll ( 0, Arrays.asList ( backEnd.getLongValues ( startTime, endTime ) ) );
                }
                else
                {
                    LongValue firstLongValue = longValues.isEmpty () ? null : longValues.get ( 0 );
                    if ( ( firstLongValue != null ) && ( firstLongValue.getTime () > startTime ) )
                    {
                        // add value
                        List<LongValue> array = Arrays.asList ( backEnd.getLongValues ( startTime, endTime ) );
                        if ( !array.isEmpty () )
                        {
                            longValues.addAll ( 0, array );
                            break;
                        }
                    }
                }
            }
            catch ( Exception e )
            {
                backEndsToRemove.add ( backEnd );
                String message = String.format ( "backend (%s): could not read from sub backend (startTime: %s; endTime: %s)", metaData, startTime, endTime );
                if ( startTime < ( System.currentTimeMillis () - metaData.getProposedDataAge () ) )
                {
                    logger.info ( message + " - backend is probably outdated", e );
                }
                else
                {
                    logger.error ( message, e );
                }
            }
        }

        // remove problematic backends
        removeBackEnds ( backEndsToRemove );

        // return final result
        return longValues.toArray ( EMPTY_LONGVALUE_ARRAY );
    }
}
