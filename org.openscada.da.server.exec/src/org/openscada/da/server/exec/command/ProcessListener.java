package org.openscada.da.server.exec.command;

public interface ProcessListener
{
    public void processCreated ( Process process );

    public void processCompleted ();
}
