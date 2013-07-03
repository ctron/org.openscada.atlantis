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

package org.openscada.da.client;

import java.util.concurrent.Future;

import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.utils.concurrent.FutureListener;
import org.openscada.utils.concurrent.NotifyFuture;

public class Helper
{
    public static void transformWrite ( final NotifyFuture<WriteResult> future, final WriteOperationCallback callback )
    {
        if ( callback != null )
        {
            future.addListener ( new FutureListener<WriteResult> () {

                @Override
                public void complete ( final Future<WriteResult> future )
                {
                    try
                    {
                        future.get ();
                        callback.complete ();
                    }
                    catch ( final Exception e )
                    {
                        callback.error ( e );
                    }
                }
            } );
        }
    }

    public static void transformWriteAttributes ( final WriteAttributeOperationCallback callback, final NotifyFuture<WriteAttributeResults> future )
    {
        if ( callback != null )
        {
            future.addListener ( new FutureListener<WriteAttributeResults> () {

                @Override
                public void complete ( final Future<WriteAttributeResults> future )
                {
                    try
                    {
                        callback.complete ( future.get () );
                    }
                    catch ( final Exception e )
                    {
                        callback.error ( e );
                    }
                }
            } );
        }
    }
}
