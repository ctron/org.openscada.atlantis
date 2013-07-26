package org.openscada.mqtt;

public interface TopicListener
{

    void update ( byte[] payload, boolean cached );

    void connectionLost ( Throwable th );

}
