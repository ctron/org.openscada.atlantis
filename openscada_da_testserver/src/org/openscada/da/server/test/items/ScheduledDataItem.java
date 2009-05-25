/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.test.items;

import java.util.Timer;
import java.util.TimerTask;

import org.openscada.da.server.common.DataItemInputCommon;

public abstract class ScheduledDataItem extends DataItemInputCommon implements Runnable
{
    private final Timer timer;

    public ScheduledDataItem ( final String name, final Timer timer, final int period )
    {
        super ( name );
        this.timer = timer;
        this.timer.scheduleAtFixedRate ( new TimerTask () {

            @Override
            public void run ()
            {
                ScheduledDataItem.this.run ();
            }
        }, 0, period );
    }
}
