package org.openscada.da.client.viewer.model.impl.containers;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public class PopupCommandDialog extends PopupDialog
{
    private final Point _initialLocation;

    public PopupCommandDialog ( final Shell parent, final Point initialLocation, final String titleText, final String infoText )
    {
        super ( parent, PopupDialog.INFOPOPUP_SHELLSTYLE, true, false, false, true, true, titleText, infoText );
        this._initialLocation = initialLocation;
    }

    @Override
    protected Point getInitialLocation ( final Point initialSize )
    {
        return this._initialLocation;
    }

}