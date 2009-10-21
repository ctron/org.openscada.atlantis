package org.openscada.da.client.signalgenerator.action;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.openscada.da.client.base.action.AbstractItemAction;
import org.openscada.da.client.signalgenerator.GeneratorView;
import org.openscada.da.ui.connection.data.Item;

public class ShowGenerator extends AbstractItemAction
{

    public ShowGenerator ()
    {
        super ( Messages.getString ( "ShowGenerator.actionName" ) ); //$NON-NLS-1$
    }

    @Override
    protected void processItem ( final Item item ) throws PartInitException
    {
        final GeneratorView view = (GeneratorView)this.page.showView ( GeneratorView.VIEW_ID, asSecondardId ( item ), IWorkbenchPage.VIEW_ACTIVATE );
        view.setDataItem ( item );
    }
}
