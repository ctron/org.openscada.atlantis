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

package org.openscada.da.client.test.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.openscada.core.Variant;
import org.openscada.da.client.test.Activator;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.ui.connection.data.DataItemHolder;
import org.openscada.da.ui.connection.data.Item;
import org.openscada.utils.concurrent.NotifyFuture;

public class WriteAttributesOperationWizard extends Wizard implements INewWizard
{

    private WriteAttributesOperationWizardValuePage page = null;

    private IStructuredSelection selection = null;

    @Override
    public boolean performFinish ()
    {
        final Item item = this.page.getItem ();
        final Map<String, Variant> attributes = this.page.getAttributes ();

        final IRunnableWithProgress op = new IRunnableWithProgress () {
            public void run ( final IProgressMonitor monitor ) throws InvocationTargetException
            {
                try
                {
                    doFinish ( monitor, item, attributes );
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
            getContainer ().run ( true, true, op );
        }
        catch ( final InterruptedException e )
        {
            return false;
        }
        catch ( final InvocationTargetException e )
        {
            final Throwable realException = e.getTargetException ();
            MessageDialog.openError ( getShell (), "Error writing to item", realException.getMessage () );
            return false;
        }
        return true;
    }

    private void doFinish ( final IProgressMonitor monitor, final Item item, final Map<String, Variant> attributes ) throws Exception
    {
        monitor.beginTask ( "Writing attributes to item", 2 );

        monitor.worked ( 1 );

        try
        {

            final DataItemHolder itemHolder = new DataItemHolder ( Activator.getDefault ().getBundle ().getBundleContext (), item, null );
            if ( !itemHolder.waitForConnection ( 5 * 1000 ) )
            {
                handleError ( new RuntimeException ( "No available connection" ).fillInStackTrace () );
                return;
            }

            final NotifyFuture<WriteAttributeResults> future = itemHolder.writeAtrtibutes ( attributes );

            try
            {
                final WriteAttributeResults results = future.get ();
                if ( !results.isSuccess () )
                {
                    handleError ( attributes, results );
                }
            }
            catch ( final Throwable e )
            {
                handleError ( e );
            }

        }
        finally
        {
            monitor.done ();
        }
    }

    public void handleError ( final Throwable e )
    {
        Display.getDefault ().syncExec ( new Runnable () {

            public void run ()
            {
                ErrorDialog.openError ( getShell (), "Failed to write", e.getMessage (), new Status ( Status.ERROR, Activator.PLUGIN_ID, e.getMessage (), e ) );
            }
        } );

    }

    public void handleError ( final Map<String, Variant> attributes, final WriteAttributeResults results )
    {
        final MultiStatus status = new MultiStatus ( Activator.PLUGIN_ID, 0, "Failed to write attributes", null );

        if ( attributes.size () != results.size () )
        {
            status.add ( new OperationStatus ( OperationStatus.WARNING, Activator.PLUGIN_ID, 0, String.format ( "Only %1$d items out of %2$d where processed", results.size (), attributes.size () ), null ) );
        }

        for ( final Map.Entry<String, WriteAttributeResult> entry : results.entrySet () )
        {
            if ( entry.getValue ().isError () )
            {
                status.add ( new OperationStatus ( OperationStatus.ERROR, Activator.PLUGIN_ID, 0, String.format ( "Failed to write attribute '%1$s': %2$s", entry.getKey (), entry.getValue ().getError ().getMessage () ), null ) );
            }
        }

        for ( final String name : attributes.keySet () )
        {
            if ( !results.containsKey ( name ) )
            {
                status.add ( new OperationStatus ( OperationStatus.WARNING, Activator.PLUGIN_ID, 0, String.format ( "Attribute %s is missing in result list", name ), null ) );
            }
        }

        final ErrorDialog dialog = new ErrorDialog ( getShell (), "Failed write attributes", "The write attributes operation did not complete successfully. There may be one ore more attributes that could not be written. Check the status of each attribute operation using the detailed information.", status, OperationStatus.ERROR | OperationStatus.WARNING );

        Display.getDefault ().syncExec ( new Runnable () {

            public void run ()
            {
                dialog.open ();
            }
        } );
    }

    public void init ( final IWorkbench workbench, final IStructuredSelection selection )
    {
        setNeedsProgressMonitor ( true );
        setWindowTitle ( "Write Attributes" );

        this.selection = selection;
    }

    @Override
    public void addPages ()
    {
        super.addPages ();

        addPage ( this.page = new WriteAttributesOperationWizardValuePage () );

        this.page.setSelection ( this.selection );
    }

}
