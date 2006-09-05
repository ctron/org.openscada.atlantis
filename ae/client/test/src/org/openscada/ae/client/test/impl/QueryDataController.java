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
        QueryDataModel.UpdateData updateData = _model.new UpdateData ();
        
        synchronized ( _model )
        {
            for ( EventInformation eventInformation : eventInformations )
            {
                switch ( eventInformation.getAction () )
                {
                case EventInformation.ACTION_ADDED:
                    {
                        EventData eventData = new EventData ( eventInformation.getEvent (), _model );
                        addEvent ( eventData );
                        updateData.added.add ( eventData );
                        updateData.removed.remove ( eventData );
                        break;
                    }
                case EventInformation.ACTION_REMOVED:
                    {
                        EventData eventData = new EventData ( eventInformation.getEvent (), _model );
                        removeEvent ( eventData );
                        updateData.removed.add ( eventData );
                        updateData.added.remove ( eventData );
                        break;
                    }
                }
            }
            _model.notifyUpdates ( updateData );
        }
    }
    
    protected void removeEvent ( EventData eventData )
    {
        _model.removeEvent ( eventData );
    }

    protected void addEvent ( EventData eventData )
    {
        _model.addEvent ( eventData );
    }

    public void unsubscribed ( String arg0 )
    {
        _model.setUnsubscribed ( arg0 );
    }
    
   
}
