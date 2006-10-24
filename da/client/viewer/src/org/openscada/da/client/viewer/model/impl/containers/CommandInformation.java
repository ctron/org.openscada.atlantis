/**
 * 
 */
package org.openscada.da.client.viewer.model.impl.containers;

import org.openscada.da.client.viewer.model.impl.BooleanSetterOutput;

class CommandInformation
{
    private String _name = null;
    private String _label = null;
    private BooleanSetterOutput _output = null;

    public String getLabel ()
    {
        return _label;
    }

    public void setLabel ( String label )
    {
        _label = label;
    }

    public String getName ()
    {
        return _name;
    }

    public void setName ( String name )
    {
        _name = name;
    }

    public BooleanSetterOutput getOutput ()
    {
        return _output;
    }

    public void setOutput ( BooleanSetterOutput output )
    {
        _output = output;
    }
}