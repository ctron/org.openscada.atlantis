package org.openscada.hd.ui.data;

import java.util.Calendar;
import java.util.Map;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryBean extends AbstractPropertyChange implements QueryListener
{

    private final static Logger logger = LoggerFactory.getLogger ( QueryBean.class );

    public static final String PROP_STATE = "state";

    private final ConnectionEntryBean parent;

    private final HistoricalItemEntryBean item;

    private final QueryParameters parameters;

    private QueryState state;

    private final Query query;

    public QueryBean ( final ConnectionEntryBean parent, final HistoricalItemEntryBean item )
    {
        this.parent = parent;
        this.item = item;

        final Calendar start = Calendar.getInstance ();
        final Calendar end = (Calendar)start.clone ();
        start.add ( Calendar.MINUTE, -30 );
        end.add ( Calendar.MINUTE, 30 );

        this.parameters = new QueryParameters ( start, end, 25 );

        this.query = parent.getConnection ().createQuery ( item.getId (), this.parameters, this );
    }

    public ConnectionEntryBean getParent ()
    {
        return this.parent;
    }

    public HistoricalItemEntryBean getItem ()
    {
        return this.item;
    }

    public QueryParameters getParameters ()
    {
        return this.parameters;
    }

    public QueryState getState ()
    {
        return this.state;
    }

    public void updateData ( final int index, final Map<String, Value[]> values, final ValueInformation[] valueInformation )
    {
        // TODO Auto-generated method stub
    }

    public void updateState ( final QueryState state )
    {
        setState ( state );
    }

    protected void setState ( final QueryState state )
    {
        logger.info ( "Switch state: {}", state );

        final QueryState oldState = this.state;
        this.state = state;
        firePropertyChange ( PROP_STATE, oldState, state );
    }

    public void close ()
    {
        this.query.close ();
        this.parent.removeQuery ( this );
    }
}
