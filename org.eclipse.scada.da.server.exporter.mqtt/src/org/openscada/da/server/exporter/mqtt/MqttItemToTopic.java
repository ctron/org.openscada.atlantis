/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jürgen Rose (cptmauli@googlemail.com)
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

package org.openscada.da.server.exporter.mqtt;

import java.io.Serializable;
import java.util.Map;

import org.openscada.ca.ConfigurationDataHelper;

public class MqttItemToTopic implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String itemId;

    private String readTopic;

    private String writeValueTopic;

    private boolean isReadable = true;

    private boolean isWritable = false;

    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        this.itemId = cfg.getStringChecked ( "item.id", "'item.id' has to be set" );
        this.readTopic = cfg.getString ( "readTopic" );
        this.writeValueTopic = cfg.getString ( "writeValueTopic" );
        this.isReadable = cfg.getBoolean ( "readable", true );
        this.isWritable = cfg.getBoolean ( "writable", false );
    }

    public String getItemId ()
    {
        return this.itemId;
    }

    public String getReadTopic ()
    {
        return this.readTopic;
    }

    public String getWriteValueTopic ()
    {
        return this.writeValueTopic;
    }

    public boolean isReadable ()
    {
        return this.isReadable;
    }

    public boolean isWritable ()
    {
        return this.isWritable;
    }

    @Override
    public String toString ()
    {
        return "MqttItemToTopic [itemId=" + this.itemId + ", readTopic=" + this.readTopic + ", writeTopic=" + this.writeValueTopic + ", isReadable=" + this.isReadable + ", isWritable=" + this.isWritable + "]";
    }
}
