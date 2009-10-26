package org.openscada.hd.server.storage.internal;

import java.util.Map;

import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;

/**
 * This object is used to transport calculated data from one method to the other.
 * @author Ludwig Straub
 */
public class CalculatedData
{
    /** Calculated array of value information objects. */
    private ValueInformation[] valueInformations;

    /** Calculated data. */
    private Map<String, Value[]> data;

    /**
     * Standard constructor.
     */
    public CalculatedData ()
    {
        this ( null, null );
    }

    /**
     * Constructor.
     * @param valueInformations calculated array of value information objects.
     * @param data calculated data
     */
    public CalculatedData ( final ValueInformation[] valueInformations, final Map<String, Value[]> data )
    {
        this.valueInformations = valueInformations;
        this.data = data;
    }

    /**
     * This method returns the calculated array of value information objects.
     * @return calculated array of value information objects
     */
    public ValueInformation[] getValueInformations ()
    {
        return valueInformations;
    }

    /**
     * This method sets the calculated array of value information objects.
     * @param valueInformations calculated array of value information objects
     */
    public void setValueInformations ( final ValueInformation[] valueInformations )
    {
        this.valueInformations = valueInformations;
    }

    /**
     * This method returns the map of calculated data.
     * @return map of calculated data
     */
    public Map<String, Value[]> getData ()
    {
        return data;
    }

    /**
     * This method sets the map of calculated data.
     * @param data map of calculated data
     */
    public void setData ( final Map<String, Value[]> data )
    {
        this.data = data;
    }
}
