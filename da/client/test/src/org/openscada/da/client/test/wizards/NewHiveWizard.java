package org.openscada.da.client.test.wizards;


import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.openscada.da.client.test.Openscada_da_client_testPlugin;
import org.openscada.da.client.test.config.HiveConnectionInformation;
import org.openscada.da.client.test.impl.HiveConnection;

public class NewHiveWizard extends Wizard implements INewWizard
{
    
    private NewHiveWizardConnectionPage _page = null;
    
    @Override
    public boolean performFinish ()
    {
        final String hostName = _page.getHostName();
        final int port = _page.getPort();
        
        IRunnableWithProgress op = new IRunnableWithProgress()
        {
            public void run ( IProgressMonitor monitor ) throws InvocationTargetException
            {
                try
                {
                    doFinish ( monitor, hostName, port );
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
            getContainer().run(true, false, op);
        }
        catch (InterruptedException e)
        {
            return false;
        }
        catch (InvocationTargetException e)
        {
            Throwable realException = e.getTargetException();
            MessageDialog.openError ( getShell(), "Error", realException.getMessage () );
            return false;
        }
        return true;
    }
    
    private void doFinish ( IProgressMonitor monitor, String hostName, int port ) throws Exception
    {
        
        monitor.beginTask("Adding hive connection..." , 2 );
        
        // add the hive
        HiveConnectionInformation info = new HiveConnectionInformation();
        info.setHost ( hostName );
        info.setPort ( port );
        
        HiveConnection connection = new HiveConnection(info);
        Openscada_da_client_testPlugin.getRepository().addConnection ( connection );
        monitor.worked ( 1 );
        
        // store all
        monitor.subTask("Saving hive configuration");
        Openscada_da_client_testPlugin.getRepository().save(Openscada_da_client_testPlugin.getRepostoryFile());
        monitor.worked ( 1 );
    }

    public void init ( IWorkbench workbench, IStructuredSelection selection )
    {
        setNeedsProgressMonitor ( true );
    }
    
    @Override
    public void addPages ()
    {
        super.addPages ();
        
        addPage ( _page = new NewHiveWizardConnectionPage() );
    }
    

}
