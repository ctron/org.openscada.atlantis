package org.openscada.da.server.arduino;

import java.net.InetSocketAddress;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.protocols.arduino.ArduinoDeviceListener;
import org.openscada.protocols.arduino.DeviceState;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.osgi.framework.BundleContext;

public class ArduinoDevice
{
    private final BundleContext context;

    private final String configurationId;

    private final org.openscada.protocols.arduino.ArduinoDevice device;

    private final ObjectPoolImpl itemPool;

    private final Executor executor;

    private final String alias;

    private final DataItemInputChained deviceStateItem;

    private final DataItemInputChained stateItem;

    private ArduinoDataItem[] items;

    public ArduinoDevice ( final BundleContext context, final String configurationId, final Map<String, String> parameters, final ObjectPoolImpl itemPool, final Executor executor )
    {
        this.context = context;
        this.configurationId = configurationId;

        this.itemPool = itemPool;
        this.executor = executor;

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        this.alias = cfg.getString ( "alias", configurationId );

        this.deviceStateItem = new DataItemInputChained ( new DataItemInformationBase ( makeId ( "deviceState" ), EnumSet.of ( IODirection.INPUT ) ), executor );
        itemPool.addService ( this.deviceStateItem.getInformation ().getName (), this.deviceStateItem, null );

        this.stateItem = new DataItemInputChained ( new DataItemInformationBase ( makeId ( "state" ), EnumSet.of ( IODirection.INPUT ) ), executor );
        itemPool.addService ( this.stateItem.getInformation ().getName (), this.stateItem, null );

        this.device = new org.openscada.protocols.arduino.ArduinoDevice ( new InetSocketAddress ( cfg.getStringChecked ( "host", "'host' not set" ), cfg.getIntegerChecked ( "port", "'port' not set" ) ), new ArduinoDeviceListener () {

            @Override
            public void stateChange ( final DeviceState deviceState )
            {
                handleStateChange ( deviceState );
            }

            @Override
            public void deviceDisconnected ()
            {
                handleDisconnect ();
            }

            @Override
            public void deviceConnected ( final Map<Integer, Object>[] parameters )
            {
                handleConnect ( parameters );
            }

            @Override
            public void dataChange ( final Object[] data )
            {
                handleData ( data );
            }
        }, false );
        this.device.start ();
    }

    private String makeId ( final String localId )
    {
        return String.format ( "%s.%s", this.alias, localId );
    }

    protected void handleStateChange ( final DeviceState deviceState )
    {
        this.deviceStateItem.updateData ( Variant.valueOf ( deviceState.toString () ), null, null );
    }

    protected void handleData ( final Object[] data )
    {
        for ( int i = 0; i < data.length; i++ )
        {
            this.items[i].updateData ( Variant.valueOf ( data[i] ), null, null );
        }
    }

    protected void handleConnect ( final Map<Integer, Object>[] parameters )
    {
        this.stateItem.updateData ( Variant.TRUE, null, null );
        createItems ( parameters );
    }

    protected void handleDisconnect ()
    {
        this.stateItem.updateData ( Variant.FALSE, null, null );
        disposeItems ();
    }

    private void disposeItems ()
    {
        for ( final ArduinoDataItem item : this.items )
        {
            this.itemPool.removeService ( item.getInformation ().getName (), item );
        }
        this.items = null;
    }

    private void createItems ( final Map<Integer, Object>[] parameters )
    {
        this.items = new ArduinoDataItem[parameters.length];

        for ( int i = 0; i < parameters.length; i++ )
        {
            this.items[i] = new ArduinoDataItem ( new DataItemInformationBase ( makeId ( String.format ( "data.%s", getItemName ( i, parameters[i] ) ) ), getIODirection ( parameters[i] ) ), this.executor );
            this.itemPool.addService ( this.items[i].getInformation ().getName (), this.items[i], null );
        }
    }

    private Set<IODirection> getIODirection ( final Map<Integer, Object> map )
    {
        final Set<IODirection> result = EnumSet.noneOf ( IODirection.class );
        final Object o = map.get ( 1 );
        if ( o instanceof Number )
        {
            final byte b = ( (Number)o ).byteValue ();
            if ( ( b & 0x01 ) == 0x01 )
            {
                result.add ( IODirection.INPUT );
            }
            if ( ( b & 0x02 ) == 0x02 )
            {
                result.add ( IODirection.OUTPUT );
            }
        }
        return result;
    }

    private Object getItemName ( final int i, final Map<Integer, Object> map )
    {
        final Object o = map.get ( 2 );
        if ( o == null )
        {
            return String.format ( "unnamed-item-%s", i );
        }
        return o.toString ();
    }

    public void dispose ()
    {
        disposeItems ();

        this.itemPool.removeService ( this.stateItem.getInformation ().getName (), this.stateItem );
        this.itemPool.removeService ( this.deviceStateItem.getInformation ().getName (), this.deviceStateItem );

        this.device.stop ();
    }

}
