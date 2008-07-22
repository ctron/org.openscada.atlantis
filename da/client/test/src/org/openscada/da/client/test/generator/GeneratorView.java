package org.openscada.da.client.test.generator;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.openscada.da.client.Connection;

public class GeneratorView extends ViewPart
{
    public static final String VIEW_ID = "org.openscada.da.client.test.GeneratorView";
    private CTabFolder tabFolder;

    private List<IGeneratorPage> pages = new LinkedList<IGeneratorPage> ();

    public GeneratorView ()
    {
        pages.add ( new BooleanGeneratorPage () );
    }

    @Override
    public void createPartControl ( Composite parent )
    {
        this.tabFolder = new CTabFolder ( parent, SWT.BOTTOM );

        for ( IGeneratorPage page : pages )
        {
            CTabItem tabItem = new CTabItem ( this.tabFolder, SWT.NONE );
            Composite tabComposite = new Composite ( this.tabFolder, SWT.NONE );
            tabComposite.setLayout ( new FillLayout () );
            page.createPage ( tabComposite );
            tabItem.setText ( page.getName () );
            tabItem.setControl ( tabComposite );
        }
    }
    
    @Override
    public void dispose ()
    {
        for ( IGeneratorPage page : pages )
        {
            page.dispose ();
        }
        super.dispose ();
    }

    @Override
    public void setFocus ()
    {
        this.tabFolder.setFocus ();
    }

    public void setDataItem ( Connection connection, String itemId )
    {
        for ( IGeneratorPage page : pages )
        {
            page.setDataItem ( connection, itemId );
        }
    }

}
