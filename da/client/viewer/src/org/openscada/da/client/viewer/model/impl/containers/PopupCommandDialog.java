package org.openscada.da.client.viewer.model.impl.containers;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public class PopupCommandDialog extends PopupDialog
{
    private Point _initialLocation;

    public PopupCommandDialog ( Shell parent, Point initialLocation, String titleText, String infoText )
    {
        super ( parent, PopupDialog.INFOPOPUP_SHELLSTYLE, true, false, true, false, titleText, infoText );
        _initialLocation = initialLocation;
    }

    @Override
    protected Point getInitialLocation ( Point initialSize )
    {
        return _initialLocation;
    }

}