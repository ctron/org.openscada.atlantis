package org.openscada.da.client.test.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.openscada.da.client.test.impl.DataItemEntry;

public class DataItemEntryPage extends PropertyPage implements
        IWorkbenchPropertyPage
{
    private Label _label = null;

    public DataItemEntryPage ()
    {
    }

    @Override
    protected Control createContents ( Composite parent )
    {
        _label = new Label ( parent, SWT.NONE );
        DataItemEntry item = getItem ();
        if ( item != null )
            _label.setText ( "Hello World: " + item.getId () );
        else
            _label.setText ( "Hello World: <no item>" );
        return _label;
    }

    private DataItemEntry getItem ()
    {
        if ( getElement () instanceof DataItemEntry )
            return (DataItemEntry)getElement ();
        else
            return null;
    }
    
    @Override
    protected void performDefaults ()
    {
        DataItemEntry item = getItem ();
        if ( item != null )
            _label.setText ( "Hello World: " + item.getId () );
        else
            _label.setText ( "Hello World: <no item>" );
        super.performDefaults ();
    }
}
