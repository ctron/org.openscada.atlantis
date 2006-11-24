package org.openscada.da.server.simulation.modules;

public interface MOV
{
    public abstract void open ();

    public abstract void close ();

    public abstract void setErrorState ( boolean state );
}
