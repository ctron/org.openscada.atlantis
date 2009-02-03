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
package org.openscada.da.client.test;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication
{

    /* (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
     */
    public Object start ( final IApplicationContext context ) throws Exception
    {
        final Display display = PlatformUI.createDisplay ();
        try
        {
            final int returnCode = PlatformUI.createAndRunWorkbench ( display, new ApplicationWorkbenchAdvisor () );
            if ( returnCode == PlatformUI.RETURN_RESTART )
            {
                return IApplication.EXIT_RESTART;
            }
            else
            {
                return IApplication.EXIT_OK;
            }
        }
        finally
        {
            display.dispose ();
        }

    }

    /* (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    public void stop ()
    {
        final IWorkbench workbench = PlatformUI.getWorkbench ();
        if ( workbench == null )
        {
            return;
        }
        final Display display = workbench.getDisplay ();
        display.syncExec ( new Runnable () {
            public void run ()
            {
                if ( !display.isDisposed () )
                {
                    workbench.close ();
                }
            }
        } );
    }
}
