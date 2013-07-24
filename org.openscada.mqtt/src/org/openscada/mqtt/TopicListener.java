package org.openscada.mqtt;

public interface TopicListener
{

    void update ( byte[] payload, boolean duplicate );

    void connectionLost ( Throwable th );

}
