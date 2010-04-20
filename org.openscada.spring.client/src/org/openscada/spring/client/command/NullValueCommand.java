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

package org.openscada.spring.client.command;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;

/**
 * This is a null value command which simply discards the write request
 * @author Jens Reimann
 *
 */
public class NullValueCommand implements ValueCommand
{
    private static Logger logger = Logger.getLogger ( NullValueCommand.class );

    public void command ( final Variant value ) throws Exception
    {
        logger.debug ( "Discarding write request: " + value );
    }

}
