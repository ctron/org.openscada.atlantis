package org.openscada.core.server.net;

public interface ManagedConnectionMXBean
{
    public StatisticInformation[] getStatistics ();

    public void close ();
}
