package org.openscada.mqtt;

import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDefaultFilePersistence;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.openscada.ca.ConfigurationDataHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttBrokerImpl implements MqttBroker, MqttCallback
{
    private static final Logger logger = LoggerFactory.getLogger ( MqttBrokerImpl.class );

    private static final String defaultClientId = MqttClient.generateClientId ();

    private final ExecutorService executor;

    private String clientId;

    private URI uri;

    private File persistencePath;

    private Character delimiter = '.';

    private String prefix;

    private String writeValueSuffix;

    private String username;

    private String password;

    private NameConverter itemToTopicConverter;

    private NameConverter topicToItemConverter;

    private Future<MqttClient> startClientFuture;

    private final ConcurrentMap<String, Set<TopicListener>> topicListeners = new ConcurrentHashMap<String, Set<TopicListener>> ();

    public MqttBrokerImpl ( final ExecutorService executor )
    {
        this.executor = executor;
    }

    public void update ( final Map<String, String> parameters )
    {
        stopClient ();
        parseConfig ( parameters );
        startClient ();

    }

    @Override
    public MqttClient getClient ()
    {
        if ( this.startClientFuture == null )
        {
            return null;
        }
        try
        {
            return this.startClientFuture.get ( 30, TimeUnit.SECONDS );
        }
        catch ( final Exception e )
        {
            logger.error ( "could not get MQTT client", e );
            return null;
        }
    }

    public void dispose ()
    {
        stopClient ();
    }

    public Character getDelimiter ()
    {
        return this.delimiter;
    }

    public String getPrefix ()
    {
        return this.prefix;
    }

    public String getWriteValueSuffix ()
    {
        return this.writeValueSuffix;
    }

    @Override
    public NameConverter getItemToTopicConverter ()
    {
        return this.itemToTopicConverter;
    }

    @Override
    public NameConverter getTopicToItemConverter ()
    {
        return this.topicToItemConverter;
    }

    private void startClient ()
    {
        this.startClientFuture = this.executor.submit ( new Callable<MqttClient> () {
            @Override
            public MqttClient call () throws Exception
            {
                // check parameters
                final String effectiveClientId = MqttBrokerImpl.this.clientId == null ? getDefaultClientId () : MqttBrokerImpl.this.clientId;
                logger.info ( "using clientId {}", effectiveClientId );
                final URI effectiveUri = MqttBrokerImpl.this.uri == null ? getDefaultUri () : MqttBrokerImpl.this.uri;
                logger.info ( "using URI {}", effectiveUri );
                final File effectivePersistencePath = MqttBrokerImpl.this.persistencePath == null ? getDefaultPersistencePath () : MqttBrokerImpl.this.persistencePath;
                logger.info ( "using persistence path {}", effectivePersistencePath );

                // set up setting
                if ( effectivePersistencePath.mkdirs () )
                {
                    logger.info ( "created mqtt persistence directory {}", effectivePersistencePath );
                }
                final MqttConnectOptions options = new MqttConnectOptions ();

                // start client
                logger.debug ( "starting MQTT client" );
                MqttClient client = null;
                try
                {
                    options.setUserName ( MqttBrokerImpl.this.username );
                    options.setPassword ( MqttBrokerImpl.this.password.toCharArray () );
                    client = new MqttClient ( effectiveUri.toString (), effectiveClientId, new MqttDefaultFilePersistence ( effectivePersistencePath.getAbsolutePath () ) );
                    client.setCallback ( MqttBrokerImpl.this );
                    client.connect ( options );
                }
                catch ( final MqttException e )
                {
                    logger.error ( "failed to start MQTT client", e );
                }

                return client;
            }
        } );
    }

    private void stopClient ()
    {
        this.executor.submit ( new Callable<MqttClient> () {
            @Override
            public MqttClient call () throws Exception
            {
                if ( MqttBrokerImpl.this.startClientFuture == null )
                {
                    return null;
                }
                final MqttClient client = MqttBrokerImpl.this.startClientFuture.get ();
                if ( client == null )
                {
                    return null;
                }
                client.disconnect ( TimeUnit.SECONDS.toMillis ( 30 ) );
                MqttBrokerImpl.this.topicListeners.clear ();
                return client;
            };
        } );
    }

    private void parseConfig ( final Map<String, String> parameters )
    {
        this.executor.submit ( new Callable<Void> () {
            @Override
            public Void call () throws Exception
            {
                final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
                if ( cfg.getString ( "clientId" ) != null )
                {
                    MqttBrokerImpl.this.clientId = cfg.getString ( "clientId" );
                }
                if ( cfg.getString ( "uri" ) != null )
                {
                    MqttBrokerImpl.this.uri = URI.create ( cfg.getString ( "uri" ) );
                }
                if ( cfg.getString ( "persistencePath" ) != null )
                {
                    MqttBrokerImpl.this.persistencePath = new File ( cfg.getString ( "persistencePath" ) );
                }
                if ( cfg.getString ( "delimiter" ) != null )
                {
                    MqttBrokerImpl.this.delimiter = cfg.getString ( "delimiter", "." ).charAt ( 0 );
                }
                if ( cfg.getString ( "prefix" ) != null )
                {
                    MqttBrokerImpl.this.prefix = cfg.getString ( "prefix" );
                }
                MqttBrokerImpl.this.writeValueSuffix = cfg.getString ( "writeSuffix", "$write" );
                MqttBrokerImpl.this.username = cfg.getString ( "username", "mqtt" );
                MqttBrokerImpl.this.password = cfg.getString ( "password", "" );

                MqttBrokerImpl.this.itemToTopicConverter = new ItemToTopicConverter ( MqttBrokerImpl.this.delimiter, MqttBrokerImpl.this.prefix, MqttBrokerImpl.this.writeValueSuffix );
                MqttBrokerImpl.this.topicToItemConverter = new TopicToItemConverter ( MqttBrokerImpl.this.delimiter, MqttBrokerImpl.this.prefix, MqttBrokerImpl.this.writeValueSuffix );
                return null;
            };
        } );
    }

    /**
     * @return default clientId given through system property
     */
    private String getDefaultClientId ()
    {
        return System.getProperty ( "org.openscada.mqtt.clientId", defaultClientId );
    }

    /**
     * @return default persistence property given through system property
     */
    private File getDefaultPersistencePath ()
    {
        return new File ( System.getProperty ( "org.openscada.mqtt.persistence", System.getProperty ( "user.home" ) + File.separator + ".openscada" + File.separator + "mqtt" ) );
    }

    /**
     * @return default mqtt uri given through system property
     */
    private URI getDefaultUri ()
    {
        return URI.create ( System.getProperty ( "org.openscada.mqtt.uri", "tcp://localhost:1883" ) );
    }

    @Override
    public void connectionLost ( final Throwable th )
    {
        for ( final Set<TopicListener> listeners : this.topicListeners.values () )
        {
            for ( final TopicListener listener : listeners )
            {
                listener.connectionLost ( th );
            }
        }

    }

    @Override
    public void deliveryComplete ( final MqttDeliveryToken arg0 )
    {
        // ignore for now
    }

    @Override
    public void messageArrived ( final MqttTopic topic, final MqttMessage message ) throws Exception
    {
        final Set<TopicListener> listeners = this.topicListeners.get ( topic.getName () );
        if ( listeners != null )
        {
            for ( final TopicListener listener : listeners )
            {
                listener.update ( message.getPayload (), message.isDuplicate () );
            }
        }

    }

    @Override
    public void addListener ( final String topic, final TopicListener listener )
    {
        this.topicListeners.putIfAbsent ( topic, new ConcurrentSkipListSet<TopicListener> () );
        this.topicListeners.get ( topic ).add ( listener );
    }

    @Override
    public void removeListener ( final String topic, final TopicListener listener )
    {
        final Set<TopicListener> listeners = this.topicListeners.get ( topic );
        if ( listeners != null )
        {
            listeners.remove ( listener );
        }
    }
}
