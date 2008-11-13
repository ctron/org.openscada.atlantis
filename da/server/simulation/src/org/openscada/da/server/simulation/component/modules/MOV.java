package org.openscada.da.server.simulation.component.modules;

public interface MOV
{
    public abstract void open ();

    public abstract void close ();

    public abstract void setErrorState ( boolean state );
}
