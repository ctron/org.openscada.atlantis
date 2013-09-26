/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.ca.common;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.scada.ca.Configuration;
import org.eclipse.scada.ca.data.ConfigurationState;

public class ConfigurationImpl implements Configuration
{
    private final String id;

    private Map<String, String> data;

    private final String factoryId;

    private ConfigurationState state;

    private Throwable error;

    public ConfigurationImpl ( final String id, final String factoryId, final Map<String, String> data )
    {
        this.id = id;
        this.factoryId = factoryId;
        this.data = new HashMap<String, String> ( data );
    }

    @Override
    public String getFactoryId ()
    {
        return this.factoryId;
    }

    @Override
    public Map<String, String> getData ()
    {
        return this.data;
    }

    @Override
    public Throwable getErrorInformation ()
    {
        return this.error;
    }

    @Override
    public String getId ()
    {
        return this.id;
    }

    @Override
    public ConfigurationState getState ()
    {
        return this.state;
    }

    public void setData ( final Map<String, String> data )
    {
        this.data = data;
    }

    public void setState ( final ConfigurationState state, final Throwable e )
    {
        this.state = state;
        this.error = e;
    }

    @Override
    public String toString ()
    {
        final StringBuilder sb = new StringBuilder ();

        sb.append ( "[" ).append ( this.factoryId ).append ( "/" ).append ( this.id ).append ( "]=" );
        sb.append ( this.data );

        return sb.toString ();
    }
}
