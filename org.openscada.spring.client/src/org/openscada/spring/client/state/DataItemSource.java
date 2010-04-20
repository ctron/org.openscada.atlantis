/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.spring.client.state;

import org.apache.log4j.Logger;
import org.openscada.da.client.DataItemValue;
import org.openscada.spring.client.DataItem;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * A state source based on a boolean data item
 * @author Jens Reimann
 * @since 0.12.0
 */
public class DataItemSource implements StateSource, InitializingBean
{
    private static Logger logger = Logger.getLogger ( DataItemSource.class );

    private DataItem dataItem;

    private boolean invert = false;

    public void setInvert ( final boolean invert )
    {
        this.invert = invert;
    }

    public void setDataItem ( final DataItem dataItem )
    {
        this.dataItem = dataItem;
    }

    public void afterPropertiesSet () throws Exception
    {
        Assert.notNull ( this.dataItem, "'dataItem' must not be null" );
    }

    public Boolean getCurrentState ()
    {
        final DataItemValue value = this.dataItem.getSnapshotValue ();

        logger.debug ( "Got current state value: " + value );

        if ( value == null )
        {
            return null;
        }
        if ( !value.isConnected () )
        {
            return null;
        }
        if ( value.isError () )
        {
            return null;
        }
        if ( value.isAlarm () )
        {
            return false;
        }

        return this.invert ? !value.getValue ().asBoolean () : value.getValue ().asBoolean ();
    }
}
