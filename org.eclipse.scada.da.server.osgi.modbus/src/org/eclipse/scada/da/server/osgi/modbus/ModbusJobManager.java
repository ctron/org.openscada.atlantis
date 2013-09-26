package org.eclipse.scada.da.server.osgi.modbus;

import org.eclipse.scada.da.server.common.io.JobManager;

public class ModbusJobManager extends JobManager
{
    public ModbusJobManager ( final ModbusMaster modbusMaster )
    {
        super ( "ModbusMasterJobs/" + modbusMaster.getId () );
    }
}
