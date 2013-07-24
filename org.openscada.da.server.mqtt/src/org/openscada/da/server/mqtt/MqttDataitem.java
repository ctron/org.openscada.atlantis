/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.mqtt;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executor;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.core.server.OperationParameters;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;
import org.openscada.mqtt.MqttBroker;
import org.openscada.mqtt.TopicListener;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttDataitem extends DataItemInputOutputChained implements TopicListener
{
    private final static Logger logger = LoggerFactory.getLogger ( MqttDataitem.class );

    private String itemId;

    private String readTopic;

    private String writeTopic;

    private boolean isReadable = true;

    private boolean isWritable = false;

    private String format = "org.openscada.da.client.DataItemValue";

    private String brokerId;

    private MqttBroker broker;

    public MqttDataitem ( final String id, final Executor executor )
    {
        super ( id, executor );
    }

    @Override
    protected NotifyFuture<WriteResult> startWriteCalculatedValue ( final Variant value, final OperationParameters operationParameters )
    {
        if ( this.broker == null )
        {
            return new InstantErrorFuture<WriteResult> ( new RuntimeException ( "broker is not set" ) );
        }
        if ( this.broker.getClient () == null )
        {
            return new InstantErrorFuture<WriteResult> ( new RuntimeException ( "MQTT client is null" ) );
        }
        if ( !this.broker.getClient ().isConnected () )
        {
            return new InstantErrorFuture<WriteResult> ( new RuntimeException ( "MQTT client is not connected" ) );
        }
        if ( !this.isWritable )
        {
            return new InstantErrorFuture<WriteResult> ( new RuntimeException ( "item is not defined as writable" ) );
        }
        try
        {
            getWriteTopic ().publish ( toMessage ( value ), 0, true );
            return new InstantFuture<WriteResult> ( WriteResult.OK );
        }
        catch ( final MqttException e )
        {
            return new InstantErrorFuture<WriteResult> ( e );
        }
        catch ( final UnsupportedEncodingException e )
        {
            return new InstantErrorFuture<WriteResult> ( e );
        }
    }

    private byte[] toMessage ( final Variant value ) throws UnsupportedEncodingException
    {
        if ( this.format.equals ( "org.openscada.da.client.DataItemValue" ) )
        {
            // FIXME: implement me
        }
        else if ( this.format.equals ( "mihini" ) )
        {
            // FIXME: implement me
        }
        return value.asString ( "" ).getBytes ( "UTF-8" );
    }

    private Variant toValue ( final byte[] message ) throws UnsupportedEncodingException
    {
        if ( this.format.equals ( "org.openscada.da.client.DataItemValue" ) )
        {
            // FIXME: implement me
        }
        else if ( this.format.equals ( "mihini" ) )
        {
            // FIXME: implement me
        }
        return Variant.valueOf ( new String ( message, "UTF-8" ) );
    }

    // FIXME: implement me
    @Override
    public NotifyFuture<WriteAttributeResults> startSetAttributes ( final Map<String, Variant> attributes, final OperationParameters operationParameters )
    {
        return super.startSetAttributes ( attributes, operationParameters );
    }

    private MqttTopic getWriteTopic ()
    {
        if ( this.writeTopic != null )
        {
            return this.broker.getClient ().getTopic ( this.writeTopic );
        }
        final String topic = this.broker.getItemToTopicConverter ().convert ( this.itemId, true );
        return this.broker.getClient ().getTopic ( topic );
    }

    private MqttTopic getReadTopic ()
    {
        if ( this.readTopic != null )
        {
            return this.broker.getClient ().getTopic ( this.readTopic );
        }
        final String topic = this.broker.getItemToTopicConverter ().convert ( this.itemId, false );
        return this.broker.getClient ().getTopic ( topic );
    }

    public void setBroker ( final MqttBroker broker )
    {
        this.broker = broker;
        this.broker.addListener ( getReadTopic ().getName (), this );
    }

    public void unsetBroker ()
    {
        this.broker = null;
    }

    public String getBrokerId ()
    {
        return this.brokerId;
    }

    public boolean isBrokerSet ()
    {
        return this.broker != null;
    }

    public void update ( final Map<String, String> parameters )
    {
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        this.itemId = cfg.getStringChecked ( "item.id", "'item.id' has to be set" );
        this.brokerId = cfg.getStringChecked ( "broker.id", "'broker.id' has to be set" );
        this.format = cfg.getString ( "org.openscada.da.client.DataItemValue" );
        this.readTopic = cfg.getString ( "readTopic" );
        this.writeTopic = cfg.getString ( "writeTopic" );
        this.isReadable = cfg.getBoolean ( "readable", true );
        this.isWritable = cfg.getBoolean ( "writable", false );
    }

    @Override
    public void update ( final byte[] payload, final boolean duplicate )
    {
        if ( this.isReadable )
        {
            try
            {
                this.updateData ( toValue ( payload ), Collections.<String, Variant> emptyMap (), AttributeMode.SET );
            }
            catch ( final UnsupportedEncodingException e )
            {
                // FIXME: implement proper error handling
                this.updateData ( null, Collections.<String, Variant> emptyMap (), AttributeMode.SET );
            }
        }
        else
        {
            logger.warn ( "got message {} for topic {}, but item {} is not defined as readable", new Object[] { payload, getReadTopic (), this.itemId } );
        }
    }

    @Override
    public void connectionLost ( final Throwable th )
    {
        this.updateData ( Variant.NULL, Collections.<String, Variant> emptyMap (), AttributeMode.SET );
    }
}
