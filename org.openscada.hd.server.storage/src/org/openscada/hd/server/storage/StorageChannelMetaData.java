package org.openscada.hd.server.storage;

import org.openscada.hd.server.storage.calculation.CalculationMethod;

/**
 * This class provides methods for accessing meta information of storage channels.
 * @author Ludwig Straub
 */
public class StorageChannelMetaData
{
    /** Name of data source of which data is stored in the channel. */
    private String dataItemId;

    /** Method that is used to calculate the data that is stored in the channel. */
    private CalculationMethod calculationMethod;

    /** Parameters that are used in combination with the calculation method to specify the algorithm that has to be applied. */
    private long[] calculationMethodParameters;

    /** Detail level of the stored data. */
    private long detailLevelId;

    /** Time stamp of first possible entry of the channel. */
    private long startTime;

    /** Time stamp of first entry that will not be stored in the channel. */
    private long endTime;

    /** Age of the data in milliseconds a stored data should be kept available. */
    private long proposedDataAge;

    /**
     * Standard constructor
     */
    public StorageChannelMetaData ()
    {
        this ( null, CalculationMethod.NATIVE, new long[0], 0L, Long.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE );
    }

    /**
     * Copy constructor
     * @param storageChannelMetaData instance to copy data from
     */
    public StorageChannelMetaData ( StorageChannelMetaData storageChannelMetaData )
    {
        this ( storageChannelMetaData.getDataItemId (), storageChannelMetaData.getCalculationMethod (), storageChannelMetaData.getCalculationMethodParameters (), storageChannelMetaData.getDetailLevelId (), storageChannelMetaData.getStartTime (), storageChannelMetaData.getEndTime (), storageChannelMetaData.getProposedDataAge () );
    }

    /**
     * Fully initializing constructor
     * @param dataItemId name of data source of which data is stored in the channel
     * @param calculationMethod method that is used to calculate the data that is stored in the channel
     * @param calculationMethodParameters parameters that are used in combination with the calculation method to specify the algorithm that has to be applied
     * @param detailLevelId detail level of the stored data
     * @param startTime time stamp of first possible entry of the channel
     * @param endTime time stamp of first entry that will not be stored in the channel
     * @param proposedDataAge age of the data in milliseconds a stored data should be kept available
     */
    public StorageChannelMetaData ( final String dataItemId, final CalculationMethod calculationMethod, final long[] calculationMethodParameters, final long detailLevelId, final long startTime, final long endTime, final long proposedDataAge )
    {
        this.dataItemId = dataItemId;
        this.calculationMethod = calculationMethod;
        this.calculationMethodParameters = calculationMethodParameters != null ? calculationMethodParameters : new long[0];
        this.detailLevelId = detailLevelId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.proposedDataAge = proposedDataAge;
    }

    /**
     * This method returns the name of data source of which data is stored in the channel.
     * @return name of data source of which data is stored in the channel
     */
    public String getDataItemId ()
    {
        return dataItemId;
    }

    /**
     * This method sets the name of data source of which data is stored in the channel.
     * @param dataItemId name of data source of which data is stored in the channel
     */
    public void setDataItemId ( String dataItemId )
    {
        this.dataItemId = dataItemId;
    }

    /**
     * This method returns the method that is used to calculate the data that is stored in the channel.
     * @return method that is used to calculate the data that is stored in the channel
     */
    public CalculationMethod getCalculationMethod ()
    {
        return calculationMethod;
    }

    /**
     * This method sets the method that is used to calculate the data that is stored in the channel.
     * @param calculationMethod method that is used to calculate the data that is stored in the channel
     */
    public void setCalculationMethod ( CalculationMethod calculationMethod )
    {
        this.calculationMethod = calculationMethod;
    }

    /**
     * This method returns the parameters that are used in combination with the calculation method to specify the algorithm that has to be applied.
     * @return parameters that are used in combination with the calculation method to specify the algorithm that has to be applied. If no parameter is returned, an empty array is returned
     */
    public long[] getCalculationMethodParameters ()
    {
        return calculationMethodParameters;
    }

    /**
     * This method sets the parameters that are used in combination with the calculation method to specify the algorithm that has to be applied.
     * @param calculationMethodParameters parameters that are used in combination with the calculation method to specify the algorithm that has to be applied
     */
    public void setCalculationMethodParameters ( long[] calculationMethodParameters )
    {
        this.calculationMethodParameters = calculationMethodParameters != null ? calculationMethodParameters : new long[0];
    }

    /**
     * This method returns the detail level of the stored data.
     * @return detail level of the stored data
     */
    public long getDetailLevelId ()
    {
        return detailLevelId;
    }

    /**
     * This method sets the detail level of the stored data.
     * @param detailLevelId detail level of the stored data
     */
    public void setDetailLevelId ( long detailLevelId )
    {
        this.detailLevelId = detailLevelId;
    }

    /**
     * This method returns the time stamp of first possible entry of the channel
     * @return time stamp of first possible entry of the channel
     */
    public long getStartTime ()
    {
        return startTime;
    }

    /**
     * This method sets the time stamp of first possible entry of the channel.
     * @param startTime time stamp of first possible entry of the channel
     */
    public void setStartTime ( long startTime )
    {
        this.startTime = startTime;
    }

    /**
     * This method returns the time stamp of first entry that will not be stored in the channel.
     * @return time stamp of first entry that will not be stored in the channel
     */
    public long getEndTime ()
    {
        return endTime;
    }

    /**
     * This method sets the time stamp of first entry that will not be stored in the channel.
     * @param endTime time stamp of first entry that will not be stored in the channel
     */
    public void setEndTime ( long endTime )
    {
        this.endTime = endTime;
    }

    /**
     * This method returns the age of the data in milliseconds a stored data should be kept available.
     * @return age of the data in milliseconds a stored data should be kept available
     */
    public long getProposedDataAge ()
    {
        return proposedDataAge;
    }

    /**
     * This method sets the age of the data in milliseconds a stored data should be kept available.
     * @param proposedDataAge age of the data in milliseconds a stored data should be kept available
     */
    public void setProposedDataAge ( long proposedDataAge )
    {
        this.proposedDataAge = proposedDataAge;
    }

    /**
     * This method transform the data to a String and provides output for debugging.
     * @return data transformed to a String
     */
    @Override
    public String toString ()
    {
        return String.format ( "dataItemId: %s; calculationMethod: %s; detailLevel: %s; startTime: %s; endTime: %s; proposedDataAge: %s", dataItemId, CalculationMethod.convertCalculationMethodToString ( calculationMethod ), detailLevelId, startTime, endTime, proposedDataAge );
    }
}
