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

package org.openscada.da.server.dave;

import org.eclipse.scada.utils.lang.Immutable;

@Immutable
public class BlockConfiguration
{
    private String id;

    private String name;

    private int area;

    private int block;

    private int start;

    private int count;

    private String daveDevice;

    private String type;

    private boolean enableStatistics = false;

    private long period;

    public BlockConfiguration ()
    {
    }

    public BlockConfiguration ( final String daveDevice, final String id, final String name, final String type, final int area, final int block, final int start, final int count, final boolean enableStatistics, final long period )
    {
        this.daveDevice = daveDevice;
        this.type = type;
        this.id = id;
        this.name = name;
        this.area = area;
        this.block = block;
        this.start = start;
        this.count = count;
        this.enableStatistics = enableStatistics;
        this.period = period;
    }

    public String getId ()
    {
        return this.id;
    }

    public void setId ( final String id )
    {
        this.id = id;
    }

    public String getType ()
    {
        return this.type;
    }

    public void setType ( final String type )
    {
        this.type = type;
    }

    public String getName ()
    {
        return this.name;
    }

    public void setName ( final String name )
    {
        this.name = name;
    }

    public int getArea ()
    {
        return this.area;
    }

    public void setArea ( final int area )
    {
        this.area = area;
    }

    public int getBlock ()
    {
        return this.block;
    }

    public void setBlock ( final int block )
    {
        this.block = block;
    }

    public int getStart ()
    {
        return this.start;
    }

    public void setStart ( final int start )
    {
        this.start = start;
    }

    public int getCount ()
    {
        return this.count;
    }

    public void setCount ( final int count )
    {
        this.count = count;
    }

    public String getDaveDevice ()
    {
        return this.daveDevice;
    }

    public void setDaveDevice ( final String daveDevice )
    {
        this.daveDevice = daveDevice;
    }

    @Override
    public String toString ()
    {
        return String.format ( "{device: %s, id: %s, name: %s, area: 0x%02x, block: %s, start: %s, count: %s}", this.daveDevice, this.id, this.name, this.area, this.block, this.start, this.count );
    }

    public boolean isEnableStatistics ()
    {
        return this.enableStatistics;
    }

    public void setEnableStatistics ( final boolean enableStatistics )
    {
        this.enableStatistics = enableStatistics;
    }

    public long getPeriod ()
    {
        return this.period;
    }

    public void setPeriod ( final long period )
    {
        this.period = period;
    }

}
