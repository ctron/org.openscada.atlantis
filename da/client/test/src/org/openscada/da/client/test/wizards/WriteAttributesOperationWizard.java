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
import java.util.Map;

import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.openscada.core.Variant;
import org.openscada.da.client.WriteAttributeOperationCallback;
import org.openscada.da.client.test.Openscada_da_client_testPlugin;
import org.openscada.da.client.test.impl.HiveConnection;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningOperation;
import org.openscada.utils.exec.LongRunningState;

public class WriteAttributesOperationWizard extends Wizard implements INewWizard
{

    private WriteAttributesOperationWizardValuePage _page = null;

    private IStructuredSelection _selection = null;
    private boolean _complete = false;
    private Throwable _error = null;

    @Override
    public boolean performFinish ()
    {
        final String item = _page.getItem ();
        final Map<String, Variant> attributes = _page.getAttributes ();
        final HiveConnection connection = _page.getConnection ();

        IRunnableWithProgress op = new IRunnableWithProgress () {
            public void run ( IProgressMonitor monitor ) throws InvocationTargetException
            {
                try
                {
                    doFinish ( monitor, connection, item, attributes );
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
            getContainer ().run ( true, true, op );
        }
        catch ( InterruptedException e )
        {
            return false;
        }
        catch ( InvocationTargetException e )
        {
            Throwable realException = e.getTargetException ();
            MessageDialog.openError ( getShell (), "Error writing to item", realException.getMessage () );
            return false;
        }
        return true;
    }

    private void doFinish ( final IProgressMonitor monitor, HiveConnection hiveConnection, String item, final Map<String, Variant> attributes ) throws Exception
    {
        monitor.beginTask ( "Writing attributes to item", 2 );

        monitor.worked ( 1 );

        final WriteAttributesOperationWizard _this = this;
        
        _complete = false;
        hiveConnection.getConnection ().writeAttributes ( item, attributes, new WriteAttributeOperationCallback () {

            public void complete ( WriteAttributeResults results )
            {
                if ( !results.isSuccess () )
                {
                    handleError ( attributes, results );
                }
                endWait ();
            }

            public void error ( Throwable e )
            {
                handleError ( e );
                endWait ();
            }

            public void failed ( String message )
            {
                handleError ( new Exception ( message ) );
                endWait ();
            }

            private void endWait ()
            {
                _complete = true;
                synchronized ( _this )
                {
                    _this.notifyAll ();
                }
            }
        } );
        
        synchronized ( this )
        {
            wait ( 100 );
            
            if ( _complete || monitor.isCanceled () )
            {
                if ( _error != null )
                {
                    throw new Exception ( _error );
                }
            }
        }
        monitor.worked ( 1 );
    }

    public void handleError ( Throwable e )
    {
        _error = e;
    }

    public void handleError ( Map<String, Variant> attributes, WriteAttributeResults results )
    {
        MultiStatus status = new MultiStatus ( Openscada_da_client_testPlugin.PLUGIN_ID, 0,
                "Failed to write attributes", null );

        if ( attributes.size () != results.size () )
        {
            status.add ( new OperationStatus (
                    OperationStatus.WARNING,
                    Openscada_da_client_testPlugin.PLUGIN_ID,
                    0,
                    String.format ( "Only %1$d items out of %2$d where processed", results.size (), attributes.size () ),
                    null ) );
        }

        for ( Map.Entry<String, WriteAttributeResult> entry : results.entrySet () )
        {
            if ( entry.getValue ().isError () )
            {
                status.add ( new OperationStatus ( OperationStatus.ERROR, Openscada_da_client_testPlugin.PLUGIN_ID, 0,
                        String.format ( "Failed to write attribute '%1$s': %2$s", entry.getKey (),
                                entry.getValue ().getError ().getMessage () ), null ) );
            }
        }

        for ( String name : attributes.keySet () )
        {
            if ( !results.containsKey ( name ) )
            {
                status.add ( new OperationStatus ( OperationStatus.WARNING, Openscada_da_client_testPlugin.PLUGIN_ID,
                        0, String.format ( "Attribute %s is missing in result list", name ), null ) );
            }
        }

        final ErrorDialog dialog = new ErrorDialog (
                getShell (),
                "Failed write attributes",
                "The write attributes operation did not complete successfully. There may be one ore more attributes that could not be written. Check the status of each attribute operation using the detailed information.",
                status, OperationStatus.ERROR | OperationStatus.WARNING );

        getShell ().getDisplay ().syncExec ( new Runnable () {

            public void run ()
            {
                dialog.open ();
            }
        } );
    }

    public void init ( IWorkbench workbench, IStructuredSelection selection )
    {
        setNeedsProgressMonitor ( true );
        setWindowTitle ( "Write Attributes" );

        _selection = selection;
    }

    @Override
    public void addPages ()
    {
        super.addPages ();

        addPage ( _page = new WriteAttributesOperationWizardValuePage () );

        _page.setSelection ( _selection );
    }

}
