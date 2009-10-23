package org.openscada.ae.ui.testing.views;

import java.text.DateFormat;
import java.util.Set;

import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.ViewerCell;
import org.openscada.ae.ui.connection.data.ConditionStatusBean;
import org.openscada.core.Variant;

public class LabelProvider extends CellLabelProvider
{

    private final IMapChangeListener mapChangeListener = new IMapChangeListener () {
        public void handleMapChange ( final MapChangeEvent event )
        {
            final Set<?> affectedElements = event.diff.getChangedKeys ();
            if ( !affectedElements.isEmpty () )
            {
                final LabelProviderChangedEvent newEvent = new LabelProviderChangedEvent ( LabelProvider.this, affectedElements.toArray () );
                fireLabelProviderChanged ( newEvent );
            }
        }
    };

    private final IObservableMap[] attributeMaps;

    private final DateFormat dateFormat;

    public LabelProvider ( final IObservableMap... attributeMaps )
    {
        super ();

        for ( int i = 0; i < attributeMaps.length; i++ )
        {
            attributeMaps[i].addMapChangeListener ( this.mapChangeListener );
        }
        this.attributeMaps = attributeMaps;

        this.dateFormat = DateFormat.getDateTimeInstance ( DateFormat.LONG, DateFormat.LONG );
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
        final Object o = cell.getElement ();
        if ( o instanceof ConditionStatusBean )
        {
            final ConditionStatusBean info = (ConditionStatusBean)o;
            switch ( cell.getColumnIndex () )
            {
            case 0:
                cell.setText ( info.getId () );
                break;
            case 1:
                cell.setText ( info.getStatus ().toString () );
                break;
            case 2:
                if ( info.getStatusTimestamp () != null )
                {
                    cell.setText ( this.dateFormat.format ( info.getStatusTimestamp () ) );
                }
                else
                {
                    cell.setText ( "<none>" );
                }
                break;
            case 3:
                final Variant value = info.getValue ();
                cell.setText ( value != null ? value.asString ( "<none>" ) : "<none>" );
                break;
            case 4:
                cell.setText ( info.getLastAknUser () != null ? info.getLastAknUser () : "<unknown>" );
                break;
            case 5:
                if ( info.getLastAknTimestamp () != null )
                {
                    cell.setText ( this.dateFormat.format ( info.getLastAknTimestamp () ) );
                }
                else
                {
                    cell.setText ( "<none>" );
                }
                break;
            }
        }
    }
}