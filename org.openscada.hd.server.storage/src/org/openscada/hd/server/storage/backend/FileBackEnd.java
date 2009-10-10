package org.openscada.hd.server.storage.backend;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;

import org.openscada.hd.server.storage.StorageChannelMetaData;
import org.openscada.hd.server.storage.calculation.CalculationMethod;
import org.openscada.hd.server.storage.datatypes.DataType;
import org.openscada.hd.server.storage.datatypes.LongValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides methods for storing and retrieving data in a file using java.io.RandomAccessFile.
 * @author Ludwig Straub
 */
public class FileBackEnd implements BackEnd
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( FileBackEnd.class );

    /** Empty byte array. */
    private final static byte[] emptyByteArray = new byte[0];

    /** Unique marker identifying file types that can be handled via this class. */
    private final static long FILE_MARKER = 0x0a2d04b20b580ca9L;

    /** Size of one data record in the file. */
    private final static long SHORT_BORDER = Short.MAX_VALUE + 1;

    /** Size of one data record in the file. */
    private final static long RECORD_BLOCK_SIZE = 8 + 8 + 8 + 8 + 2;

    /** Maximum size of buffer when copying data within a file. */
    private final static int MAX_COPY_BUFFER_FILL_SIZE = 1024 * 1024;

    /** Version of file format. */
    private final static long FILE_VERSION = 1L;

    /** Encoder that will be used to store the configuration id within the file header. */
    private final CharsetEncoder charEncoder = Charset.forName ( "utf-8" ).newEncoder ();

    /** Decoder that will be used to extract the configuration id from the file header. */
    private final CharsetDecoder charDecoder = Charset.forName ( "utf-8" ).newDecoder ();

    /** Name of the file that is used to store data. */
    private final String fileName;

    /** Metadata of the storage channel. */
    private StorageChannelMetaData metaData;

    /** Open file or null, if currently no file is open. */
    private RandomAccessFile randomAccessFile;

    /** Flag indicating whether the file currently is open in write more or in read only more. */
    private boolean openInWriteMode;

    /** Offset within the file where the header has ended and real data starts. */
    private long dataOffset;

    /** Flag indicating whether the instance has been initialized or not. */
    private boolean initialized;

    /**
     * Constructor expecting the configuration of the file backend.
     * @param fileName name of the existing file that is used to store data
     */
    public FileBackEnd ( final String fileName )
    {
        this.fileName = fileName;
        metaData = null;
        openInWriteMode = false;
        initialized = false;
        if ( ( fileName == null ) || ( fileName.trim ().length () == 0 ) )
        {
            throw new IllegalArgumentException ( "invalid filename passed via configuration" );
        }
    }

    /**
     * @see org.openscada.hd.server.storage.backend.BackEnd#create
     */
    public synchronized void create ( final StorageChannelMetaData storageChannelMetaData ) throws Exception
    {
        // assure that a valid object has been passed
        if ( storageChannelMetaData == null )
        {
            String message = String.format ( "invalid StorageChannelMetaData object passed for file '%s'!", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }

        // extract configuration values
        final String configurationId = storageChannelMetaData.getConfigurationId ();
        final byte[] configurationIdBytes = encodeToBytes ( configurationId );
        final long calculationMethodId = CalculationMethod.convertCalculationMethodToLong ( storageChannelMetaData.getCalculationMethod () );
        final long[] calculationMethodParameters = storageChannelMetaData.getCalculationMethodParameters ();
        final long detailLevelId = storageChannelMetaData.getDetailLevelId ();
        final long startTime = storageChannelMetaData.getStartTime ();
        final long endTime = storageChannelMetaData.getEndTime ();
        final long proposedDataAge = storageChannelMetaData.getProposedDataAge ();
        final long dataType = DataType.convertDataTypeToLong ( storageChannelMetaData.getDataType () );

        // validate input data
        if ( configurationId == null )
        {
            String message = String.format ( "invalid configuration id specified for file '%s'!", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }
        if ( startTime >= endTime )
        {
            String message = String.format ( "invalid timespan specified for file '%s'! (startTime >= endTime)", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }

        // create new file including folder
        File file = new File ( fileName );
        File parent = file.getParentFile ();
        if ( parent != null )
        {
            parent.mkdirs ();
        }
        if ( !file.createNewFile () )
        {
            String message = String.format ( "file '%s' could not be created. please verify the access rights and make sure that no file with the given name already exists.", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }

        // write standardized file header to file
        openConnection ( true );
        randomAccessFile.seek ( 0L );
        final long dataOffset = ( 11 + calculationMethodParameters.length ) * 8 + configurationIdBytes.length + 2;
        randomAccessFile.writeLong ( FILE_MARKER );
        randomAccessFile.writeLong ( dataOffset );
        randomAccessFile.writeLong ( FILE_VERSION );
        randomAccessFile.writeLong ( detailLevelId );
        randomAccessFile.writeLong ( startTime );
        randomAccessFile.writeLong ( endTime );
        randomAccessFile.writeLong ( proposedDataAge );
        randomAccessFile.writeLong ( dataType );
        randomAccessFile.writeLong ( calculationMethodId );
        randomAccessFile.writeLong ( calculationMethodParameters.length );
        randomAccessFile.writeLong ( configurationIdBytes.length );
        for ( int i = 0; i < calculationMethodParameters.length; i++ )
        {
            randomAccessFile.writeLong ( calculationMethodParameters[i] );
        }
        randomAccessFile.write ( configurationIdBytes );
        long parity = 0;
        parity = ( parity + FILE_VERSION ) % SHORT_BORDER;
        parity = ( parity + dataOffset ) % SHORT_BORDER;
        parity = ( parity + ( configurationId == null ? 0 : configurationId.hashCode () ) ) % SHORT_BORDER;
        parity = ( parity + detailLevelId ) % SHORT_BORDER;
        parity = ( parity + startTime ) % SHORT_BORDER;
        parity = ( parity + endTime ) % SHORT_BORDER;
        parity = ( parity + proposedDataAge ) % SHORT_BORDER;
        parity = ( parity + dataType ) % SHORT_BORDER;
        parity = ( parity + calculationMethodId ) % SHORT_BORDER;
        parity = ( parity + calculationMethodParameters.length ) % SHORT_BORDER;
        for ( int i = 0; i < calculationMethodParameters.length; i++ )
        {
            parity = ( parity + calculationMethodParameters[i] ) % SHORT_BORDER;
        }
        randomAccessFile.writeShort ( (short)parity );
    }

    /**
     * @see org.openscada.hd.server.storage.backend.BackEnd#initialize
     */
    public synchronized void initialize ( final StorageChannelMetaData storageChannelMetaData ) throws Exception
    {
        metaData = null;
        getMetaData ();
        initialized = true;
    }

    /**
     * @see org.openscada.hd.server.storage.backend.BackEnd#cleanupRelicts
     */
    public synchronized void cleanupRelicts () throws Exception
    {
        assureInitialized ();
    }

    /**
     * @see org.openscada.hd.server.storage.backend.BackEnd#getMetaData
     */
    public synchronized StorageChannelMetaData getMetaData () throws Exception
    {
        if ( metaData == null )
        {
            openConnection ( false );
            metaData = extractMetaData ();
            closeConnection ();
        }
        return metaData;
    }

    /**
     * @see org.openscada.hd.server.storage.backend.BackEnd#isTimeSpanConstant
     */
    public synchronized boolean isTimeSpanConstant ()
    {
        return true;
    }

    /**
     * @see org.openscada.hd.server.storage.backend.BackEnd#deinitialize
     */
    public synchronized void deinitialize () throws Exception
    {
        initialized = false;
        closeConnection ();
        metaData = null;
    }

    /**
     * @see org.openscada.hd.server.storage.backend.BackEnd#delete
     */
    public synchronized void delete () throws Exception
    {
        // assure that any previous open connection is closed
        deinitialize ();

        // delete old file if any exists
        File file = new File ( fileName );
        if ( file.exists () )
        {
            logger.info ( String.format ( "deleting existing file '%s'...", fileName ) );
            if ( file.delete () )
            {
                logger.info ( String.format ( "deletion of file '%s' successful", fileName ) );
            }
            else
            {
                logger.warn ( String.format ( "deletion of file '%s' failed", fileName ) );
            }
        }
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
     * This method extracts the metadata from the file.
     * It is assumed that the file is already open.
     * @return extracted metadata
     * @throws Exception if the file cannot be read or if the file version or format is invalid
     */
    private StorageChannelMetaData extractMetaData () throws Exception
    {
        randomAccessFile.seek ( 0L );
        final long fileSize = randomAccessFile.length ();
        if ( fileSize < 16 )
        {
            String message = String.format ( "file '%s' is of invalid format! (too small)", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }
        final long fileMarker = randomAccessFile.readLong ();
        if ( fileMarker != FILE_MARKER )
        {
            String message = String.format ( "file '%s' is of invalid format! (invalid marker)", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }
        dataOffset = randomAccessFile.readLong ();
        if ( fileSize < dataOffset )
        {
            String message = String.format ( "file '%s' is of invalid format! (invalid header)", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }
        final long version = randomAccessFile.readLong ();
        if ( version != FILE_VERSION )
        {
            String message = String.format ( "file '%s' is of invalid format! (wrong version)", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }
        final long detailLevelId = randomAccessFile.readLong ();
        final long startTime = randomAccessFile.readLong ();
        final long endTime = randomAccessFile.readLong ();
        if ( startTime >= endTime )
        {
            String message = String.format ( "file '%s' has invalid timespan specified! (startTime >= endTime)", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }
        final long proposedDataAge = randomAccessFile.readLong ();
        final long dataType = randomAccessFile.readLong ();
        final long calculationMethodId = randomAccessFile.readLong ();
        final long calculationMethodParameterCountSize = randomAccessFile.readLong ();
        final long configurationIdSize = randomAccessFile.readLong ();
        if ( ( dataOffset - randomAccessFile.getFilePointer () - 2 - configurationIdSize ) != ( calculationMethodParameterCountSize * 8 ) )
        {
            String message = String.format ( "file '%s' is of invalid format! (invalid count of calculation method parameters)", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }
        final long[] calculationMethodParameters = new long[(int)calculationMethodParameterCountSize];
        for ( int i = 0; i < calculationMethodParameters.length; i++ )
        {
            calculationMethodParameters[i] = randomAccessFile.readLong ();
        }
        if ( ( dataOffset - randomAccessFile.getFilePointer () - 2 ) != configurationIdSize )
        {
            String message = String.format ( "file '%s' is of invalid format! (invalid configuration id)", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }
        final byte[] configurationIdBytes = new byte[(int)configurationIdSize];
        randomAccessFile.readFully ( configurationIdBytes );
        final String configurationId = decodeStringFromBytes ( configurationIdBytes );
        long parity = 0;
        parity = ( parity + version ) % SHORT_BORDER;
        parity = ( parity + dataOffset ) % SHORT_BORDER;
        parity = ( parity + configurationId.hashCode () ) % SHORT_BORDER;
        parity = ( parity + detailLevelId ) % SHORT_BORDER;
        parity = ( parity + startTime ) % SHORT_BORDER;
        parity = ( parity + endTime ) % SHORT_BORDER;
        parity = ( parity + proposedDataAge ) % SHORT_BORDER;
        parity = ( parity + dataType ) % SHORT_BORDER;
        parity = ( parity + calculationMethodId ) % SHORT_BORDER;
        parity = ( parity + calculationMethodParameters.length ) % SHORT_BORDER;
        for ( int i = 0; i < calculationMethodParameters.length; i++ )
        {
            parity = ( parity + calculationMethodParameters[i] ) % SHORT_BORDER;
        }
        if ( randomAccessFile.readShort () != parity )
        {
            String message = String.format ( "file '%s' has a corrupt header!", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }

        // create a wrapper object for returning the retrieved data
        return new StorageChannelMetaData ( configurationId, CalculationMethod.convertLongToCalculationMethod ( calculationMethodId ), calculationMethodParameters, detailLevelId, startTime, endTime, proposedDataAge, DataType.convertLongToDataType ( dataType ) );
    }

    /**
     * This method assures that a valid connection to the file exists.
     * The position of the file pointer is not defined.
     * @param allowWrite flag indicating whether the connection should have write privileges or not
     * @throws Exception in case of problems
     */
    private void openConnection ( final boolean allowWrite ) throws Exception
    {
        // close connection if a writable file is required and the current connection only supports reading
        if ( ( randomAccessFile != null ) && allowWrite && !openInWriteMode )
        {
            closeConnection ();
        }

        // if file already is open, nothing has to be done
        if ( randomAccessFile == null )
        {
            try
            {
                // open new connection
                File file = new File ( fileName );
                randomAccessFile = new RandomAccessFile ( file, allowWrite ? "rw" : "r" );
                openInWriteMode = allowWrite;
            }
            catch ( IOException e )
            {
                // close connection in case of problems
                String message = String.format ( "file '%s' could not be opened", fileName );
                logger.error ( message, e );
                closeConnection ();
                throw new Exception ( message, e );
            }
        }
    }

    /**
     * This method closes any existing connections.
     */
    private void closeConnection ()
    {
        if ( randomAccessFile != null )
        {
            try
            {
                randomAccessFile.close ();
            }
            catch ( IOException e )
            {
                logger.warn ( String.format ( "file '%s' could not be closed", fileName ) );
            }
            randomAccessFile = null;
        }
    }

    /**
     * This method reads a long value from the file.
     * It is assumed that an open connection exists.
     * @param position position within the file where the data has to be read
     * @return read long value
     * @throws Exception in case of read problems or file corruption
     */
    private LongValue readLongValue ( final long position ) throws Exception
    {
        if ( randomAccessFile.getFilePointer () != position )
        {
            randomAccessFile.seek ( position );
        }
        final long time = randomAccessFile.readLong ();
        final long qualityIndicatorAsLong = randomAccessFile.readLong ();
        final double qualityIndicator = Double.longBitsToDouble ( qualityIndicatorAsLong );
        final long baseValueCount = randomAccessFile.readLong ();
        final long value = randomAccessFile.readLong ();
        long parity = 0;
        parity = ( parity + time ) % SHORT_BORDER;
        parity = ( parity + qualityIndicatorAsLong ) % SHORT_BORDER;
        parity = ( parity + baseValueCount ) % SHORT_BORDER;
        parity = ( parity + value ) % SHORT_BORDER;
        if ( randomAccessFile.readShort () != parity )
        {
            String message = String.format ( "file '%s' is corrupt! invalid timestamp at %x", fileName, time );
            logger.error ( message );
            throw new Exception ( message );
        }
        return new LongValue ( time, qualityIndicator, baseValueCount, value );
    }

    /**
     * This method returns the offset within the file where the new data should be stored.
     * It is assumed that an open connection exists.
     * Since the data most likely has to be appended to the file, the search will be performed starting at the end of the file.
     * @param time time for which the perfect storing position has to be retrieved
     * @return perfect storing position of the passed long value
     * @throws Exception in case of read problems or file corruption
     */
    private long getInsertionPoint ( final long time ) throws Exception
    {
        long endSearch = randomAccessFile.length () - RECORD_BLOCK_SIZE;
        while ( endSearch >= dataOffset )
        {
            LongValue existingLongValue = readLongValue ( endSearch );
            long existingTime = existingLongValue.getTime ();
            if ( time > existingTime )
            {
                return endSearch + RECORD_BLOCK_SIZE;
            }
            else if ( time == existingTime )
            {
                return endSearch;
            }
            endSearch -= RECORD_BLOCK_SIZE;
        }
        return dataOffset;
    }

    /**
     * This method returns the offset within the file where the new data should be stored.
     * It is assumed that an open connection exists.
     * A binary search is applied in order to find the correct position within the file.
     * @param startTime time for which the perfect storing position has to be retrieved
     * @return perfect storing position of the passed long value
     * @throws Exception in case of read problems or file corruption
     */
    private long getFirstEntryPosition ( final long startTime ) throws Exception
    {
        // ignore incomplete data at file end
        long fileSize = randomAccessFile.length ();
        long incompleteData = ( fileSize - dataOffset ) % RECORD_BLOCK_SIZE;
        if ( incompleteData > 0 )
        {
            fileSize -= incompleteData;
        }

        // check for bounds to optimize search
        if ( metaData.getEndTime () < startTime )
        {
            return fileSize > dataOffset ? fileSize - RECORD_BLOCK_SIZE : fileSize;
        }
        if ( metaData.getStartTime () > startTime )
        {
            return dataOffset;
        }

        // prepare data for real binary search
        long startSearch = 0;
        long endSearch = ( fileSize - dataOffset ) / RECORD_BLOCK_SIZE;

        // perform real binary search
        while ( startSearch < endSearch )
        {
            long midSearch = ( startSearch + endSearch ) / 2;
            long filePointer = ( midSearch * RECORD_BLOCK_SIZE ) + dataOffset;
            long midTime = readLongValue ( filePointer ).getTime ();
            if ( midTime < startTime )
            {
                startSearch = midSearch + 1;
            }
            else if ( midTime > startTime )
            {
                endSearch = midSearch - 1;
            }
            else
            {
                return filePointer;
            }
        }
        final long result = ( Math.max ( 0, Math.min ( startSearch, endSearch ) ) * RECORD_BLOCK_SIZE ) + dataOffset;
        return ( result > dataOffset ) && ( result == fileSize ) ? result - RECORD_BLOCK_SIZE : result;
    }

    /**
     * This method stores the passed data in the file.
     * It is assumed that a valid connection exists.
     * Only data that matches the specified time span will be processed.
     * @param longValue data that has to be stored.
     * @throws Exception in case of problems
     */
    private void writeLongValue ( final LongValue longValue ) throws Exception
    {
        // assure that the passed value matches the timespan of the metadata
        final long time = longValue.getTime ();
        if ( ( time < metaData.getStartTime () ) || ( time >= metaData.getEndTime () ) )
        {
            return;
        }

        // calculate insertion point of new data
        long insertionPoint = getInsertionPoint ( longValue.getTime () );
        long endCopy = randomAccessFile.length ();

        // make room for new data if data cannot be appended at the end or existing data has to be overwritten
        if ( ( insertionPoint != endCopy ) && ( readLongValue ( insertionPoint ).getTime () != time ) )
        {
            // move file content to create cap for new data
            byte[] buffer = new byte[(int)Math.min ( MAX_COPY_BUFFER_FILL_SIZE, endCopy - insertionPoint )];
            long startCopy = Math.max ( endCopy - buffer.length, insertionPoint );
            while ( startCopy < endCopy )
            {
                int bufferFillSize = (int) ( endCopy - startCopy );
                randomAccessFile.seek ( startCopy );
                randomAccessFile.read ( buffer, 0, bufferFillSize );
                randomAccessFile.seek ( startCopy + RECORD_BLOCK_SIZE );
                randomAccessFile.write ( buffer, 0, bufferFillSize );
                endCopy = startCopy;
                startCopy = Math.max ( insertionPoint, startCopy - bufferFillSize );
            }
        }

        // set file pointer to correct insertion position
        randomAccessFile.seek ( insertionPoint );

        // prepare values to write
        final long qualityIndicator = Double.doubleToLongBits ( longValue.getQualityIndicator () );
        final long baseValueCount = longValue.getBaseValueCount ();
        final long value = longValue.getValue ();
        long parity = 0;
        parity = ( parity + time ) % SHORT_BORDER;
        parity = ( parity + qualityIndicator ) % SHORT_BORDER;
        parity = ( parity + baseValueCount ) % SHORT_BORDER;
        parity = ( parity + value ) % SHORT_BORDER;

        // write values
        randomAccessFile.writeLong ( time );
        randomAccessFile.writeLong ( qualityIndicator );
        randomAccessFile.writeLong ( baseValueCount );
        randomAccessFile.writeLong ( value );
        randomAccessFile.writeShort ( (short)parity );
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
                // assure that write operation can be performed
                openConnection ( true );

                // write data to file
                writeLongValue ( longValue );
            }
            finally
            {
                // close connection
                closeConnection ();
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
            try
            {
                // assure that write operation can be performed
                openConnection ( true );

                // write data to file
                for ( int i = 0; i < longValues.length; i++ )
                {
                    writeLongValue ( longValues[i] );
                }
            }
            finally
            {
                // close connection
                closeConnection ();
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

        // assure that a valid timespan is passed
        if ( startTime >= endTime )
        {
            return EMPTY_LONGVALUE_ARRAY;
        }

        // perform search
        try
        {
            // assure that read operation can be performed
            openConnection ( false );

            // get data from file
            final long fileSize = randomAccessFile.length ();
            long startingPosition = getFirstEntryPosition ( startTime );
            List<LongValue> longValues = new ArrayList<LongValue> ();
            while ( startingPosition + RECORD_BLOCK_SIZE <= fileSize )
            {
                LongValue longValue = readLongValue ( startingPosition );
                if ( longValue.getTime () >= endTime )
                {
                    break;
                }
                longValues.add ( longValue );
                startingPosition += RECORD_BLOCK_SIZE;
            }
            return longValues.toArray ( EMPTY_LONGVALUE_ARRAY );
        }
        finally
        {
            // close connection
            closeConnection ();
        }
    }

    /**
     * This method encodes text so that it can be stored within a file.
     * @param data text to be encoded
     * @return encoded text as byte array
     */
    private byte[] encodeToBytes ( final String data )
    {
        if ( data == null )
        {
            return emptyByteArray;
        }
        synchronized ( this.charEncoder )
        {
            try
            {
                return charEncoder.encode ( CharBuffer.wrap ( data ) ).array ();
            }
            catch ( final CharacterCodingException e )
            {
                return data.getBytes ();
            }
        }
    }

    /**
     * This method decodes previously encoded text.
     * @param bytes text to be decoded
     * @return decoded text
     */
    private String decodeStringFromBytes ( final byte[] bytes )
    {
        if ( bytes == null )
        {
            return "";
        }

        try
        {
            return charDecoder.decode ( ByteBuffer.wrap ( bytes ) ).toString ().replaceAll ( "\u0000", "" );
        }
        catch ( final CharacterCodingException e )
        {
            return new String ( bytes );
        }
    }
}
