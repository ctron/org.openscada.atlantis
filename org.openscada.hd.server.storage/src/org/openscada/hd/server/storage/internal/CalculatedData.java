package org.openscada.hd.server.storage.internal;

import java.util.Map;

import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;

/**
 * This object is used to transport calculated data from one method to the other
 * @author Ludwig Straub
 */
public class CalculatedData
{
    /** This attribute is set by the method calculateValues and read by the method sendCalculatedValues. It contains the value information objects that were created during the calculation process. */
    public ValueInformation[] calculatedResultValueInformations;

    /** This attribute is set by the method calculateValues and read by the method sendCalculatedValues. It contains the value objects that were created during the calculation process. */
    public Map<String, Value[]> calculatedResultMap;
}
