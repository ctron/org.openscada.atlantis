/**
 * 
 */
package org.openscada.ae.ui.views;

import java.util.Set;

import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.openscada.ae.ui.data.BrowserEntryBean;
import org.openscada.ae.ui.data.ConnectionEntryBean;

final class LabelProvider extends StyledCellLabelProvider
{
    private final IMapChangeListener mapChangeListener = new IMapChangeListener () {
        public void handleMapChange ( final MapChangeEvent event )
        {
            Set<?> affectedElements = event.diff.getChangedKeys ();
            if ( !affectedElements.isEmpty () )
            {
                LabelProviderChangedEvent newEvent = new LabelProviderChangedEvent ( LabelProvider.this, affectedElements.toArray () );
                fireLabelProviderChanged ( newEvent );
            }
        }
    };

    private final IObservableMap[] attributeMaps;

    public LabelProvider ( final IObservableMap... attributeMaps )
    {
        for ( int i = 0; i < attributeMaps.length; i++ )
        {
            attributeMaps[i].addMapChangeListener ( this.mapChangeListener );
        }
        this.attributeMaps = attributeMaps;
    }

    @Override
    public void dispose ()
    {
        for ( int i = 0; i < this.attributeMaps.length; i++ )
        {
            this.attributeMaps[i].removeMapChangeListener ( this.mapChangeListener );
        }
        super.dispose ();
    }

    @Override
    public void update ( final ViewerCell cell )
    {
        if ( cell.getElement () instanceof ConnectionEntryBean )
        {
            ConnectionEntryBean entry = (ConnectionEntryBean)cell.getElement ();

            StyledString text = new StyledString ();
            text.append ( entry.getConnectionInformation ().toString () );
            if ( entry.getConnectionStatus () != null )
            {
                text.append ( " [" + entry.getConnectionStatus ().toString () + "]", StyledString.DECORATIONS_STYLER );
            }

            cell.setText ( text.getString () );
            cell.setStyleRanges ( text.getStyleRanges () );
        }
        else if ( cell.getElement () instanceof BrowserEntryBean )
        {
            BrowserEntryBean entry = (BrowserEntryBean)cell.getElement ();
            cell.setText ( entry.getId () );
        }
        else
        {
            super.update ( cell );
        }
    }
}