package org.openscada.da.server.osgi.modbus;

public enum RequestType
{
    DISCRETE ( (byte)0x02, (byte)0x00 ),
    COIL ( (byte)0x01, (byte)0x05 ),
    INPUT ( (byte)0x04, (byte)0x00 ),
    HOLDING ( (byte)0x03, (byte)0x06 );

    private final byte readFunctionCode;

    private final byte writeFunctionCode;

    RequestType ( final byte readFunctionCode, final byte writeFunctionCode )
    {
        this.readFunctionCode = readFunctionCode;
        this.writeFunctionCode = writeFunctionCode;
    }

    public byte getReadFunctionCode ()
    {
        return this.readFunctionCode;
    }

    public byte getWriteFunctionCode ()
    {
        return this.writeFunctionCode;
    }
}
