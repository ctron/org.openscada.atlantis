package org.openscada.da.client.viewer.model.impl;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.InputDefinition;
import org.openscada.da.client.viewer.model.OutputDefinition;

public class BaseDynamicObject implements DynamicObject
{
    private String _id = null;
    
    private Map<String, InputDefinition> _inputs = new HashMap<String, InputDefinition> ();
    private Map<String, OutputDefinition> _outputs = new HashMap<String, OutputDefinition> ();
    
    public BaseDynamicObject ( String id )
    {
        super ();
        _id = id;
    }
    
    public String getId ()
    {
        return _id;
    }
    
    public InputDefinition getInputByName ( String name )
    {
        return _inputs.get ( name );
    }

    public InputDefinition[] getInputs ()
    {
       return _inputs.values ().toArray ( new InputDefinition [0] );
    }

    public OutputDefinition getOutputByName ( String name )
    {
        return _outputs.get ( name );
    }

    public OutputDefinition[] getOutputs ()
    {
        return _outputs.values ().toArray ( new OutputDefinition [0] );
    }
    
    protected void addInput ( InputDefinition input )
    {
        _inputs.put ( input.getName (), input );
    }
    
    protected void removeInput ( String name )
    {
        _inputs.remove ( name );
    }
    
    protected void addOutput ( OutputDefinition output )
    {
        _outputs.put ( output.getName (), output );
    }
    
    protected void removeOutput ( String name )
    {
        _outputs.remove ( name );
    }

}
