package org.openscada.da.client.signalgenerator;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.openscada.da.client.base.item.DataItemHolder;
import org.openscada.da.client.signalgenerator.page.GeneratorPage;

public class GeneratorView extends ViewPart
{
    public static final String VIEW_ID = "org.openscada.da.client.signalGenerator.GeneratorView"; //$NON-NLS-1$

    private CTabFolder tabFolder;

    private final List<GeneratorPageInformation> pages = new LinkedList<GeneratorPageInformation> ();

    private DataItemHolder item;

    public GeneratorView ()
    {
    }

    @Override
    public void createPartControl ( final Composite parent )
    {
        createPages ();

        this.tabFolder = new CTabFolder ( parent, SWT.BOTTOM );

        for ( final GeneratorPageInformation page : this.pages )
        {
            final CTabItem tabItem = new CTabItem ( this.tabFolder, SWT.NONE );
            final Composite tabComposite = new Composite ( this.tabFolder, SWT.NONE );
            tabComposite.setLayout ( new FillLayout () );
            page.getGeneratorPage ().createPage ( tabComposite );
            tabItem.setText ( page.getLabel () );
            tabItem.setControl ( tabComposite );

            if ( this.item != null )
            {
                page.getGeneratorPage ().setDataItem ( this.item );
            }
        }

        this.tabFolder.setSelection ( 0 );
    }

    private void createPages ()
    {
        try
        {
            for ( final GeneratorPageInformation info : getPageInformation () )
            {
                this.pages.add ( info );
            }
        }
        catch ( final CoreException e )
        {
            ErrorDialog.openError ( this.getSite ().getShell (), Messages.getString("GeneratorView.createPages.error"), Messages.getString("GeneratorView.createPages.errorMessage"), e.getStatus () ); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    @Override
    public void dispose ()
    {
        for ( final GeneratorPageInformation page : this.pages )
        {
            page.getGeneratorPage ().dispose ();
        }
        this.pages.clear ();
        super.dispose ();
    }

    @Override
    public void setFocus ()
    {
        this.tabFolder.setFocus ();
    }

    public void setDataItem ( final DataItemHolder item )
    {
        this.item = item;
        if ( item != null )
        {
            setPartName ( String.format ( Messages.getString("GeneratorView.partName"), item.getItemId (), item.getConnection ().getConnectionInformation () ) ); //$NON-NLS-1$
        }
        else
        {
            setPartName ( Messages.getString("GeneratorView.emptyPartName") ); //$NON-NLS-1$
        }

        for ( final GeneratorPageInformation page : this.pages )
        {
            page.getGeneratorPage ().setDataItem ( item );
        }
    }

    private Collection<GeneratorPageInformation> getPageInformation () throws CoreException
    {
        final List<GeneratorPageInformation> result = new LinkedList<GeneratorPageInformation> ();

        for ( final IConfigurationElement element : Platform.getExtensionRegistry ().getConfigurationElementsFor ( Activator.EXTP_GENERATOR_PAGE ) )
        {
            if ( !"generatorPage".equals ( element.getName () ) ) //$NON-NLS-1$
            {
                continue;
            }
            Object o;
            o = element.createExecutableExtension ( "class" ); //$NON-NLS-1$

            if ( ! ( o instanceof GeneratorPage ) )
            {
                throw new CoreException ( new Status ( Status.ERROR, Activator.PLUGIN_ID, Messages.getString("GeneratorView.classTypeMismatchError") ) ); //$NON-NLS-1$
            }

            final GeneratorPageInformation info = new GeneratorPageInformation ();
            info.setGeneratorPage ( (GeneratorPage)o );
            info.setLabel ( element.getAttribute ( "label" ) ); //$NON-NLS-1$
            info.setSortKey ( element.getAttribute ( "sortKey" ) ); //$NON-NLS-1$
            result.add ( info );
        }

        Collections.sort ( result, new Comparator<GeneratorPageInformation> () {

            public int compare ( final GeneratorPageInformation arg0, final GeneratorPageInformation arg1 )
            {
                String key1 = arg0.getSortKey ();
                String key2 = arg1.getSortKey ();
                if ( key1 == null )
                {
                    key1 = ""; //$NON-NLS-1$
                }
                if ( key2 == null )
                {
                    key2 = ""; //$NON-NLS-1$
                }

                return key1.compareTo ( key2 );
            }
        } );

        return result;
    }

    @Override
    public void init ( final IViewSite site, final IMemento memento ) throws PartInitException
    {
        super.init ( site, memento );
        setDataItem ( DataItemHolder.loadFrom ( memento ) );
    }

    @Override
    public void saveState ( final IMemento memento )
    {
        super.saveState ( memento );
        if ( this.item != null )
        {
            this.item.saveTo ( memento );
        }
    }
}
