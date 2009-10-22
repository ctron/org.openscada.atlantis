package org.openscada.ui.databinding;

import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetTreeContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonListeningLabelProvider extends ListeningLabelProvider implements ICommonLabelProvider
{

    private final static Logger logger = LoggerFactory.getLogger ( CommonListeningLabelProvider.class );

    private final String contentExtensionId;

    public CommonListeningLabelProvider ( final String contentExtensionId )
    {
        this.contentExtensionId = contentExtensionId;
    }

    public void init ( final ICommonContentExtensionSite config )
    {
        final ITreeContentProvider contentProvider = config.getService ().getContentExtensionById ( this.contentExtensionId ).getContentProvider ();
        if ( contentProvider instanceof ObservableSetTreeContentProvider )
        {
            addSource ( ( (ObservableSetTreeContentProvider)contentProvider ).getKnownElements () );
        }
        else if ( contentProvider instanceof ObservableSetContentProvider )
        {
            addSource ( ( (ObservableSetContentProvider)contentProvider ).getKnownElements () );
        }
        else if ( contentProvider instanceof ObservableListContentProvider )
        {
            addSource ( ( (ObservableListContentProvider)contentProvider ).getKnownElements () );
        }
        else if ( contentProvider instanceof ObservableListTreeContentProvider )
        {
            addSource ( ( (ObservableListTreeContentProvider)contentProvider ).getKnownElements () );
        }
    }

    public void restoreState ( final IMemento aMemento )
    {
    }

    public void saveState ( final IMemento aMemento )
    {
    }

    /**
     * Empty implementation of getDescriptor
     */
    public String getDescription ( final Object anElement )
    {
        logger.debug ( "getDescription: {}", anElement );
        return null;
    }

}
