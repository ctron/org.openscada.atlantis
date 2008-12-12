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

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.openscada.core.Variant;
import org.openscada.da.client.Connection;
import org.openscada.da.client.WriteOperationCallback;

public class WriteOperationWizard extends Wizard implements INewWizard
{

    private static Logger _log = Logger.getLogger ( WriteOperationWizard.class );

    private WriteOperationWizardValuePage _page = null;

    private IStructuredSelection _selection = null;

    private boolean _complete = false;

    private Throwable _error = null;

    public WriteOperationWizard ()
    {
        setWindowTitle ( "Write to data item" );
        setNeedsProgressMonitor ( true );
    }

    @Override
    public boolean performFinish ()
    {
        final String item = this._page.getItem ();
        final Variant value = this._page.getValue ();
        final Connection connection = this._page.getConnection ();

        final IRunnableWithProgress op = new IRunnableWithProgress () {
            public void run ( final IProgressMonitor monitor ) throws InvocationTargetException
            {
                try
                {
                    doFinish ( monitor, connection, item, value );
                }
                catch ( final Throwable e )
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
            _log.warn ( "Failed to perform write operation", e );
            final Throwable realException = e.getTargetException ();
            MessageDialog.openError ( getShell (), "Error writing to item", realException.getMessage () );
            return false;
        }
        return true;
    }

    private void doFinish ( final IProgressMonitor monitor, final Connection connection, final String item, final Variant value ) throws Exception
    {
        monitor.beginTask ( "Writing value to item", 2 );

        monitor.worked ( 1 );
        final WriteOperationWizard _this = this;

        this._error = null;
        this._complete = false;
        connection.write ( item, value, new WriteOperationCallback () {

            public void complete ()
            {
                endWait ();
            }

            public void error ( final Throwable e )
            {
                handleError ( e );
                endWait ();
            }

            public void failed ( final String message )
            {
                handleError ( new Exception ( message ) );
                endWait ();
            }

            private void endWait ()
            {
                WriteOperationWizard.this._complete = true;
                synchronized ( _this )
                {
                    _this.notifyAll ();
                }
            }
        } );

        synchronized ( this )
        {
            while ( ! ( this._complete || monitor.isCanceled () ) )
            {
                wait ( 100 );
            }
            if ( this._error != null )
            {
                throw new Exception ( this._error );
            }
        }
        monitor.worked ( 1 );
    }

    public void handleError ( final Throwable e )
    {
        this._error = e;
    }

    public void init ( final IWorkbench workbench, final IStructuredSelection selection )
    {
        setNeedsProgressMonitor ( true );

        this._selection = selection;
    }

    @Override
    public void addPages ()
    {
        super.addPages ();

        addPage ( this._page = new WriteOperationWizardValuePage () );

        this._page.setSelection ( this._selection );
    }

}
