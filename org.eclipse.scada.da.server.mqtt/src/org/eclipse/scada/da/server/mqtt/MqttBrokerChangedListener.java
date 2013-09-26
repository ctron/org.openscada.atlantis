package org.eclipse.scada.da.server.mqtt;

import org.eclipse.scada.mqtt.MqttBroker;

public class MqttBrokerChangedListener
{
    private final MqttDataitem item;

    public MqttBrokerChangedListener ( final MqttDataitem mqttDataitem )
    {
        this.item = mqttDataitem;
    }

    public void unsetBroker ( final String id, final MqttBroker broker )
    {
        if ( ( id != null ) && id.equals ( this.item.getBrokerId () ) )
        {
            this.item.unsetBroker ();
        }
    }

    public void setBroker ( final String id, final MqttBroker broker )
    {
        if ( ( id != null ) && id.equals ( this.item.getBrokerId () ) )
        {
            this.item.unsetBroker ();
            this.item.setBroker ( broker );
        }
    }
}
