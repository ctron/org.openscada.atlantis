/**
 * 
 */
package org.openscada.da.ui.connection.views;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.openscada.core.ui.connection.data.ConnectionHolder;
import org.openscada.da.core.browser.FolderEntry;
import org.openscada.da.ui.connection.internal.FolderEntryWrapper;
import org.openscada.ui.databinding.AdapterHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ConnectionObservableFactory implements IObservableFactory
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionObservableFactory.class );

    public IObservable createObservable ( final Object target )
    {
        logger.info ( "create observable: {}", target );

        if ( target instanceof ConnectionHolder )
        {
            return new RootFolderObserver ( (ConnectionHolder)target );
        }
        else if ( target instanceof FolderEntryWrapper )
        {
            final FolderEntry entry = (FolderEntry)AdapterHelper.adapt ( target, FolderEntry.class );
            if ( entry != null )
            {
                // only return a new observer if the folder entry is of type FolderEntry
                return new SubFolderObserver ( (FolderEntryWrapper)target );
            }
            else
            {
                return null;
            }
        }

        return null;
    }

}