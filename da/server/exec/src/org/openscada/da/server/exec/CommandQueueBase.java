/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

/**
 * 
 */
package org.openscada.da.server.exec;

public abstract class CommandQueueBase implements CommandQueue
{
    /**
     * Queue name
     */
    private String queueName;

    /* (non-Javadoc)
     * @see org.openscada.da.server.exec.CommandQueue#setQueueName(java.lang.String)
     */
    @Override
    public void setQueueName ( String queueName )
    {
        this.queueName = queueName;
    }

    /**
     * @return the queueName
     */
    public String getQueueName ()
    {
        return this.queueName;
    }
}
