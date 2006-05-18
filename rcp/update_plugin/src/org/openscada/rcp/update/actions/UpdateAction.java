package org.openscada.rcp.update.actions;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class UpdateAction implements IWorkbenchWindowActionDelegate
{

    private Shell _shell;
    private Display _display;
    private IWorkbenchWindow _window;
    private boolean _restart;
    
    public void dispose ()
    {
        if ( _window == null )
        {
            _shell.close ();
            _display.dispose ();
        }
    }

    public void init ( IWorkbenchWindow window )
    {
        _window = window;
        _shell = window.getShell ();
        _display = _shell.getDisplay (); 
    }

    public void run ( IAction action )
    {
        if ( _shell == null )
        {
            _display = Display.getCurrent ();
            _shell = new Shell ( _display, SWT.NONE );
        }
        
        
    }

    public void selectionChanged ( IAction action, ISelection selection )
    {
    }

}
