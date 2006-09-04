package org.openscada.ae.client.test.impl;

import org.openscada.ae.core.EventInformation;
import org.openscada.ae.core.Listener;

public class QueryDataController implements Listener
{
    
    private QueryDataModel _model = null;
    
    public QueryDataController ( QueryDataModel model )
    {
        super ();
        _model = model;
    }

    public void events ( EventInformation[] eventInformations )
    {
        synchronized ( _model )
        {
            for ( EventInformation eventInformation : eventInformations )
            {
                switch ( eventInformation.getAction () )
                {
                case EventInformation.ACTION_ADDED:
                    addEvent ( eventInformation );
                    break;
                case EventInformation.ACTION_REMOVED:
                    removeEvent ( eventInformation );
                    break;
                }
            }
            _model.notifyUpdates ( eventInformations );
        }
    }
    
    protected void removeEvent ( EventInformation eventInformation )
    {
        _model.removeEvent ( eventInformation.getEvent () );
    }

    protected void addEvent ( EventInformation eventInformation )
    {
        _model.addEvent ( eventInformation.getEvent () );
    }

    public void unsubscribed ( String arg0 )
    {
        _model.setUnsubscribed ( arg0 );
    }
    
   
}
