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

package org.openscada.da.client.test.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.openscada.da.client.test.Activator;
import org.openscada.rcp.da.client.browser.HiveConnection;
import org.openscada.rcp.da.client.browser.HiveConnectionInformation;

public class NewHiveWizard extends Wizard implements INewWizard
{

    private NewHiveWizardConnectionPage _page = null;

    @Override
    public boolean performFinish ()
    {
        final String connectionString = _page.getConnectionString ();

        IRunnableWithProgress op = new IRunnableWithProgress () {
            public void run ( IProgressMonitor monitor ) throws InvocationTargetException
            {
                try
                {
                    doFinish ( monitor, connectionString );
                }
                catch ( Exception e )
                {
                    throw new InvocationTargetException ( e );
                }
                finally
                {
                    monitor.done ();
                }
            }
        };
        try
        {
            getContainer ().run ( true, false, op );
        }
        catch ( InterruptedException e )
        {
            return false;
        }
        catch ( InvocationTargetException e )
        {
            Throwable realException = e.getTargetException ();
            MessageDialog.openError ( getShell (), "Error", realException.getMessage () );
            return false;
        }
        return true;
    }

    private void doFinish ( IProgressMonitor monitor, String connectionString ) throws Exception
    {

        monitor.beginTask ( "Adding hive connection...", 2 );

        // add the hive
        HiveConnectionInformation info = new HiveConnectionInformation ( connectionString );

        HiveConnection connection = new HiveConnection ( info );
        Activator.getRepository ().addConnection ( connection );
        monitor.worked ( 1 );

        // store all
        monitor.subTask ( "Saving hive configuration" );
        Activator.getRepository ().save ( Activator.getRepostoryFile () );
        monitor.worked ( 1 );
    }

    public void init ( IWorkbench workbench, IStructuredSelection selection )
    {
        setNeedsProgressMonitor ( true );
        setDefaultPageImageDescriptor ( Activator.getImageDescriptor ( "icons/48x48/stock_channel.png" ) );
    }

    @Override
    public void addPages ()
    {
        super.addPages ();

        addPage ( _page = new NewHiveWizardConnectionPage () );
    }

}
