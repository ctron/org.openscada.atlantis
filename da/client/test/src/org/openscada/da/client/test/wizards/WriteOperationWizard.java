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
import org.openscada.da.client.test.impl.HiveConnection;
import org.openscada.da.core.Variant;
import org.openscada.net.base.LongRunningController;
import org.openscada.net.base.LongRunningOperation;
import org.openscada.net.base.LongRunningController.State;
import org.openscada.net.base.data.Message;

public class WriteOperationWizard extends Wizard implements INewWizard
{
    
    private WriteOperationWizardValuePage _page = null;
    
    private IStructuredSelection _selection = null;
    
    @Override
    public boolean performFinish ()
    {
        final String item = _page.getItem ();
        final Variant value = _page.getValue ();
        final HiveConnection connection = _page.getConnection ();
        
        IRunnableWithProgress op = new IRunnableWithProgress()
        {
            public void run ( IProgressMonitor monitor ) throws InvocationTargetException
            {
                try
                {
                    doFinish ( monitor, connection, item, value );
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
            getContainer().run ( true, true, op );
        }
        catch (InterruptedException e)
        {
            return false;
        }
        catch (InvocationTargetException e)
        {
            Throwable realException = e.getTargetException();
            MessageDialog.openError ( getShell(), "Error writing to item", realException.getMessage () );
            return false;
        }
        return true;
    }
    
    private void doFinish ( final IProgressMonitor monitor, HiveConnection hiveConnection, String item, Variant value ) throws Exception
    {
        monitor.beginTask ( "Writing value to item" , 4 );
        
        monitor.worked ( 1 );
        LongRunningOperation op = hiveConnection.getConnection ().startWrite ( item, value, new LongRunningController.Listener () {

            public void stateChanged ( State arg0, Message arg1, Throwable arg2 )
            {
                switch ( arg0 )
                {
                case REQUESTED:
                    monitor.worked ( 1 );
                    monitor.subTask ( "Requested operation" );
                    break;
                case RUNNING:
                    monitor.worked ( 1 );
                    monitor.subTask ( "Operation running" );
                    break;
                case SUCCESS:
                    monitor.worked ( 1 );
                    monitor.subTask ( "Operation complete" );
                    break;
                case FAILURE:
                    monitor.worked ( 1 );
                    monitor.subTask ( "Operation failed" );
                default:
                    break;
                }
            }} );
        
        boolean waiting = true;
        while ( waiting )
        {
            synchronized ( op )
            {
                if ( op.isComplete () )
                {
                    waiting = false;
                }
                else
                {
                    op.wait ( 100 );
                }
                
                if ( monitor.isCanceled () && (!op.isComplete ()) )
                {
                    op.cancel ();
                    waiting = false;
                }
                else if ( op.isComplete () )
                {
                    waiting = false;
                    hiveConnection.getConnection ().completeWrite ( op );
                }
            }
        }
    }

    public void init ( IWorkbench workbench, IStructuredSelection selection )
    {
        setNeedsProgressMonitor ( true );
        
        _selection = selection;
    }
    
    @Override
    public void addPages ()
    {
        super.addPages ();
        
        addPage ( _page = new WriteOperationWizardValuePage() );
        
        _page.setSelection ( _selection );
    }
    

}
