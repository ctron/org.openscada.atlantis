package org.openscada.da.client.viewer.model.impl;

import java.util.EnumSet;

import org.openscada.da.client.viewer.model.AlreadyConnectedException;
import org.openscada.da.client.viewer.model.Connector;
import org.openscada.da.client.viewer.model.InputDefinition;
import org.openscada.da.client.viewer.model.Type;

public class InputExport implements InputDefinition
{
    private InputDefinition _input = null;
    private String _alias = null;
    
    public InputExport ( InputDefinition input, String alias )
    {
        _input = input;
        _alias = alias;
    }

    public void connect ( Connector connector ) throws AlreadyConnectedException
    {
        _input.connect ( connector );
    }

    public void disconnect ()
    {
        _input.disconnect ();
    }

    public String getName ()
    {
        return _alias;
    }

    public EnumSet<Type> getSupportedTypes ()
    {
        return _input.getSupportedTypes ();
    }

    public void update ( Type type, Object value )
    {
        _input.update ( type, value );
    }

}
