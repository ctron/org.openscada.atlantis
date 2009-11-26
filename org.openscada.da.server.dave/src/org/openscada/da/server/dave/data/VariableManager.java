package org.openscada.da.server.dave.data;

public interface VariableManager
{
    public void addVariableListener ( String type, VariableListener listener );

    public void removeVariableListener ( String type, VariableListener listener );
}
