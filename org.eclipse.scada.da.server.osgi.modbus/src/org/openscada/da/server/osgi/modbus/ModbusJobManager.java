package org.openscada.da.server.osgi.modbus;

import org.openscada.da.server.common.io.JobManager;

public class ModbusJobManager extends JobManager
{
    public ModbusJobManager ( final ModbusMaster modbusMaster )
    {
        super ( "ModbusMasterJobs/" + modbusMaster.getId () );
    }
}
