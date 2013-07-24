package org.openscada.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;

public interface MqttBroker
{
    public MqttClient getClient ();

    public NameConverter getTopicToItemConverter ();

    public NameConverter getItemToTopicConverter ();

    public void addListener ( String topic, TopicListener listener );

    public void removeListener ( String topic, TopicListener listener );
}
