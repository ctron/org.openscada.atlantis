package org.openscada.hd.server.storage.backend;

import org.openscada.hd.server.storage.StorageChannelMetaData;
import org.openscada.hd.server.storage.calculation.CalculationMethod;

/**
 * Interface for accessing storage channel backend objects that are suitable for specific circumstances.
 * These circumstances and constraints have to be passed as input parameters.
 * @author Ludwig Straub
 */
public interface BackEndFactory
{
    /**
     * This method returns the metadata objects of all existing back end objects.
     * If more than one metadata object exists for the same configuration, calculation
     * method and detail level then the additional information is merged into one single meta data object.
     * The time span is hereby widened so that the earliest start time is used and the latest end time.
     * All other information is taken from the sub meta data object with the latest end time.
     * @return metadata objects of all existing back end objects
     * @throws Exception in case of any problems
     */
    public abstract StorageChannelMetaData[] getExistingBackEndsMetaData () throws Exception;

    /**
     * This method returns all currently available and previously created backends matching the specified constraints.
     * @param configurationId id of the configuration for which the backends should be retrieved
     * @param detailLevelId detail level of the stored data
     * @param calculationMethod calculation method of the data source for which the backends should be retrieved
     * @return all currently available and previously created backends matching the specified constraints. If no backend objects are returned, an empty array is returned.
     * @throws Exception in case of any problems
     */
    public abstract BackEnd[] getExistingBackEnds ( final String configurationId, final long detailLevelId, final CalculationMethod calculationMethod ) throws Exception;

    /**
     * This method creates and returns a new storage channel backend object that can be used to store data matching the specified metadata.
     * The create method of the created object is not called by this method. This call has to be performed manually.
     * @param storageChannelMetaData meta data that has to be used as input when creating a new storage channel backend
     * @return new storage channel backend object that can be used to store data matching the specified metadata
     * @throws Exception in case of any problems
     */
    public BackEnd createNewBackEnd ( final StorageChannelMetaData storageChannelMetaData ) throws Exception;
}
