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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.openscada.da.client.test.impl.HiveConnection;
import org.openscada.da.core.WriteAttributesOperationListener.Results;
import org.openscada.da.core.data.Variant;
import org.openscada.net.base.LongRunningController;
import org.openscada.net.base.LongRunningOperation;
import org.openscada.net.base.LongRunningController.State;
import org.openscada.net.base.data.Message;

public class WriteAttributesOperationWizard extends Wizard implements INewWizard
{
    
    private WriteAttributesOperationWizardValuePage _page = null;
    
    private IStructuredSelection _selection = null;
    
    @Override
    public boolean performFinish ()
    {
        final String item = _page.getItem ();
        final Map<String,Variant> attributes = _page.getAttributes ();
        final HiveConnection connection = _page.getConnection ();
        
        IRunnableWithProgress op = new IRunnableWithProgress()
        {
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
    
    private void doFinish ( final IProgressMonitor monitor, HiveConnection hiveConnection, String item, Map<String,Variant> attributes ) throws Exception
    {
        monitor.beginTask ( "Writing attributes to item" , 4 );
        
        monitor.worked ( 1 );
        LongRunningOperation op = hiveConnection.getConnection ().startWriteAttributes ( item, attributes, new LongRunningController.Listener () {

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
                    Results result = hiveConnection.getConnection ().completeWriteAttributes ( op );
                    
                    if ( attributes.size () != result.size () )
                    {
                        throw new Exception ( String.format ( "Only %1$d items out of %2$d where processed", result.size (), attributes.size () ) );
                    }
                }
            }
        }
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
        
        addPage ( _page = new WriteAttributesOperationWizardValuePage() );
        
        _page.setSelection ( _selection );
    }
    
}
