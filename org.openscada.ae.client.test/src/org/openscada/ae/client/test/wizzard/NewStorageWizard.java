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

package org.openscada.ae.client.test.wizzard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.openscada.ae.client.test.Activator;
import org.openscada.ae.client.test.impl.StorageConnection;
import org.openscada.ae.client.test.impl.StorageConnectionInformation;

public class NewStorageWizard extends Wizard implements INewWizard
{

    private NewStorageWizardConnectionPage _page = null;

    @Override
    public boolean performFinish ()
    {
        final String hostName = this._page.getHostName ();
        final int port = this._page.getPort ();

        final IRunnableWithProgress op = new IRunnableWithProgress () {
            public void run ( final IProgressMonitor monitor ) throws InvocationTargetException
            {
                try
                {
                    doFinish ( monitor, hostName, port );
                }
                catch ( final Exception e )
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
        catch ( final InterruptedException e )
        {
            return false;
        }
        catch ( final InvocationTargetException e )
        {
            final Throwable realException = e.getTargetException ();
            MessageDialog.openError ( getShell (), "Error", realException.getMessage () );
            return false;
        }
        return true;
    }

    private void doFinish ( final IProgressMonitor monitor, final String hostName, final int port ) throws Exception
    {

        monitor.beginTask ( "Adding storage connection...", 2 );

        // add the hive
        final StorageConnectionInformation info = new StorageConnectionInformation ();
        info.setHost ( hostName );
        info.setPort ( port );

        final StorageConnection connection = new StorageConnection ( info );
        Activator.getRepository ().addConnection ( connection );
        monitor.worked ( 1 );

        // store all
        monitor.subTask ( "Saving storage configuration" );
        Activator.getRepository ().save ( Activator.getRepostoryFile () );
        monitor.worked ( 1 );
    }

    public void init ( final IWorkbench workbench, final IStructuredSelection selection )
    {
        setNeedsProgressMonitor ( true );
        setDefaultPageImageDescriptor ( Activator.getImageDescriptor ( "icons/48x48/stock_channel.png" ) );
    }

    @Override
    public void addPages ()
    {
        super.addPages ();

        addPage ( this._page = new NewStorageWizardConnectionPage () );
    }

}
