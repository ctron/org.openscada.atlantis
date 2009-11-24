package org.openscada.da.server.exec2.command;

public interface ProcessListener
{
    public void processCreated ( Process process );

    public void processCompleted ();
}
