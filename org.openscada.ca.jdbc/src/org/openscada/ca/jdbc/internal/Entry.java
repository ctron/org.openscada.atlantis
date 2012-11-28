/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ca.jdbc.internal;

import java.io.Serializable;

public class Entry implements Serializable, Cloneable
{
    private static final long serialVersionUID = -4409482881200779138L;

    private String factoryId;

    private String configurationId;

    private String key;

    private String instance;

    private String value;

    private Integer seq = 0;

    public Entry ()
    {
    }
 
    public Entry(Entry entry) {
        this.setInstance ( entry.instance );
        this.setFactoryId ( entry.factoryId );
        this.setConfigurationId ( entry.configurationId );
        this.setKey ( entry.key );
        this.setValue ( entry.value );
        this.setSeq ( entry.seq );
    }
    
    public String getInstance ()
    {
        return this.instance;
    }

    public void setInstance ( final String instance )
    {
        this.instance = instance;
    }

    public String getFactoryId ()
    {
        return this.factoryId;
    }

    public void setFactoryId ( final String factoryId )
    {
        this.factoryId = factoryId;
    }

    public String getConfigurationId ()
    {
        return this.configurationId;
    }

    public void setConfigurationId ( final String configurationId )
    {
        this.configurationId = configurationId;
    }

    public String getKey ()
    {
        return this.key;
    }

    public void setKey ( final String entry )
    {
        this.key = entry;
    }

    public String getValue ()
    {
        return this.value;
    }

    public void setValue ( final String data )
    {
        this.value = data;
    }

    public Integer getSeq ()
    {
        return seq;
    }

    public void setSeq ( Integer seq )
    {
        this.seq = seq;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( configurationId == null ) ? 0 : configurationId.hashCode () );
        result = prime * result + ( ( factoryId == null ) ? 0 : factoryId.hashCode () );
        result = prime * result + ( ( instance == null ) ? 0 : instance.hashCode () );
        result = prime * result + ( ( key == null ) ? 0 : key.hashCode () );
        result = prime * result + ( ( seq == null ) ? 0 : seq.hashCode () );
        result = prime * result + ( ( value == null ) ? 0 : value.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass () != obj.getClass () )
            return false;
        Entry other = (Entry)obj;
        if ( configurationId == null )
        {
            if ( other.configurationId != null )
                return false;
        }
        else if ( !configurationId.equals ( other.configurationId ) )
            return false;
        if ( factoryId == null )
        {
            if ( other.factoryId != null )
                return false;
        }
        else if ( !factoryId.equals ( other.factoryId ) )
            return false;
        if ( instance == null )
        {
            if ( other.instance != null )
                return false;
        }
        else if ( !instance.equals ( other.instance ) )
            return false;
        if ( key == null )
        {
            if ( other.key != null )
                return false;
        }
        else if ( !key.equals ( other.key ) )
            return false;
        if ( seq == null )
        {
            if ( other.seq != null )
                return false;
        }
        else if ( !seq.equals ( other.seq ) )
            return false;
        if ( value == null )
        {
            if ( other.value != null )
                return false;
        }
        else if ( !value.equals ( other.value ) )
            return false;
        return true;
    }
    
    @Override
    protected Object clone () throws CloneNotSupportedException
    {
        return new Entry(this);
    }
}
