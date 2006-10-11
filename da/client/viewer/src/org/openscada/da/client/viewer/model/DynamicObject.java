package org.openscada.da.client.viewer.model;

public interface DynamicObject
{
    public abstract String getId ();
    
    public abstract InputDefinition [] getInputs ();
    public abstract OutputDefinition [] getOutputs ();
    
    public abstract InputDefinition getInputByName ( String name );
    public abstract OutputDefinition getOutputByName ( String name );
}
