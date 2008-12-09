package org.openscada.da.project.wizards;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.openscada.da.dataItemList.ItemsDocument;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "rtl". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class RealtimeListNewWizard extends Wizard implements INewWizard
{
    private RealtimeListNewWizardPage page;

    private ISelection selection;

    /**
     * Constructor for RealtimeListNewWizard.
     */
    public RealtimeListNewWizard ()
    {
        super ();
        setNeedsProgressMonitor ( true );
    }

    /**
     * Adding the page to the wizard.
     */

    public void addPages ()
    {
        this.page = new RealtimeListNewWizardPage ( this.selection );
        addPage ( this.page );
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish ()
    {
        final String containerName = this.page.getContainerName ();
        final String fileName = this.page.getFileName ();
        final IRunnableWithProgress op = new IRunnableWithProgress () {
            public void run ( final IProgressMonitor monitor ) throws InvocationTargetException
            {
                try
                {
                    doFinish ( containerName, fileName, monitor );
                }
                catch ( final CoreException e )
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

    /**
     * The worker method. It will find the container, create the
     * file if missing or just replace its contents, and open
     * the editor on the newly created file.
     */

    private void doFinish ( final String containerName, final String fileName, final IProgressMonitor monitor ) throws CoreException
    {
        // create a sample file
        monitor.beginTask ( "Creating " + fileName, 2 );
        final IWorkspaceRoot root = ResourcesPlugin.getWorkspace ().getRoot ();
        final IResource resource = root.findMember ( new Path ( containerName ) );
        if ( !resource.exists () || ! ( resource instanceof IContainer ) )
        {
            throwCoreException ( "Container \"" + containerName + "\" does not exist." );
        }
        final IContainer container = (IContainer)resource;
        final IFile file = container.getFile ( new Path ( fileName ) );
        try
        {
            final InputStream stream = openContentStream ();
            if ( file.exists () )
            {
                file.setContents ( stream, true, true, monitor );
            }
            else
            {
                file.create ( stream, true, monitor );
            }
            stream.close ();
        }
        catch ( final IOException e )
        {
        }
        monitor.worked ( 1 );
        monitor.setTaskName ( "Opening file for editing..." );
        getShell ().getDisplay ().asyncExec ( new Runnable () {
            public void run ()
            {
                final IWorkbenchPage page = PlatformUI.getWorkbench ().getActiveWorkbenchWindow ().getActivePage ();
                try
                {
                    IDE.openEditor ( page, file, true );
                }
                catch ( final PartInitException e )
                {
                }
            }
        } );
        monitor.worked ( 1 );
    }

    /**
     * We will initialize file contents with a sample text.
     */

    private InputStream openContentStream ()
    {
        final ItemsDocument doc = ItemsDocument.Factory.newInstance ();

        // add root node
        doc.addNewItems ();

        return doc.newInputStream ();
    }

    private void throwCoreException ( final String message ) throws CoreException
    {
        final IStatus status = new Status ( IStatus.ERROR, "org.openscada.da.project", IStatus.OK, message, null );
        throw new CoreException ( status );
    }

    /**
     * We will accept the selection in the workbench to see if
     * we can initialize from it.
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init ( final IWorkbench workbench, final IStructuredSelection selection )
    {
        this.selection = selection;
    }
}