/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.utils.exec;

import org.junit.Assert;
import org.junit.Before;
import org.openscada.utils.exec.Operation;
import org.openscada.utils.exec.OperationResult;
import org.openscada.utils.exec.SyncBasedOperation;

public class SyncOperationTests
{
    Operation<String, String> _opSyncSuccess = null;

    @Before
    public void setUp () throws Exception
    {
        _opSyncSuccess = new SyncBasedOperation<String, String> () {

            public String execute ( String arg0 ) throws Exception
            {
                Thread.sleep ( 1000 );
                System.out.println ( "Say hello: " + arg0 );
                Thread.sleep ( 1000 );
                return "Hello to: " + arg0;
            }
        };
    }

    public void testSync () throws Exception
    {
        Assert.assertEquals ( _opSyncSuccess.execute ( "Alice" ), "Hello to: Alice" );
    }

    public void testAsync () throws Exception
    {
        OperationResult<String> or = _opSyncSuccess.startExecute ( "Bob" );
        System.out.println ( "Started execution" );

        or.complete ();

        Assert.assertTrue ( or.isComplete () );
        Assert.assertTrue ( or.isSuccess () );
    }

    public void testAsyncHandler () throws Exception
    {
        OperationHandlerTestImpl<String> handler = new OperationHandlerTestImpl<String> ();

        OperationResult<String> or = _opSyncSuccess.startExecute ( handler, "Bob" );
        System.out.println ( "Started execution" );

        or.complete ();

        Assert.assertTrue ( or.isComplete () );
        Assert.assertTrue ( or.isSuccess () );

        Assert.assertTrue ( handler.isSuccess () );
        Assert.assertFalse ( handler.isFailure () );

        Assert.assertNull ( handler.getException () );
        Assert.assertEquals ( handler.getResult (), "Hello to: Bob" );
    }
}
