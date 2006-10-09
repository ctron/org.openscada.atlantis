package org.openscada.da.client.viewer.model;

public interface OutputDefinition extends TargetDefinition
{
    void addListener ( OutputListener listener );
    void removeListener ( OutputListener listener );
}
