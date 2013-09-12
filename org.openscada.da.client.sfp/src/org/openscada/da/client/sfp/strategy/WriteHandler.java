/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.client.sfp.strategy;

import org.eclipse.scada.utils.concurrent.AbstractFuture;
import org.openscada.da.core.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteHandler extends AbstractFuture<WriteResult>
{

    private final static Logger logger = LoggerFactory.getLogger ( WriteHandler.class );

    public void complete ( final org.openscada.protocol.sfp.messages.WriteResult writeResult )
    {
        logger.debug ( "Completing write operation - errorCode: {}, errorMessage: {}", writeResult.getErrorCode (), writeResult.getErrorMessage () );

        if ( writeResult.getErrorCode () == 0 )
        {
            setResult ( WriteResult.OK );
        }
        else
        {
            setError ( new RuntimeException ( makeErrorString ( writeResult ) ) );
        }
    }

    public static String makeErrorString ( final org.openscada.protocol.sfp.messages.WriteResult writeResult )
    {
        if ( writeResult.getErrorMessage () == null )
        {
            return String.format ( "%04x", writeResult.getErrorCode () );
        }
        else
        {
            return String.format ( "%04x: %s", writeResult.getErrorCode (), writeResult.getErrorMessage () );
        }
    }
}