package org.openscada.ae.client.test.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.openscada.ae.core.Event;
import org.openscada.core.Variant;

public class EventData implements IPropertySource
{
    private enum Properties {
        ID,
        TIMESTAMP;
    }
    
    private Event _event = null;
    private QueryDataModel _query = null;
    
    public EventData ( Event event, QueryDataModel query )
    {
        super ();
        _event = event;
        _query = query;
    }

    public Event getEvent ()
    {
        return _event;
    }

    public QueryDataModel getQuery ()
    {
        return _query;
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( _event == null ) ? 0 : _event.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass () != obj.getClass () )
            return false;
        final EventData other = (EventData)obj;
        if ( _event == null )
        {
            if ( other._event != null )
                return false;
        }
        else
            if ( !_event.equals ( other._event ) )
                return false;
        return true;
    }

    public Object getEditableValue ()
    {
        return _event.getId ();
    }

    protected void fillPropertyDescriptors ( List<IPropertyDescriptor> list )
    {
        {
            PropertyDescriptor pd = new PropertyDescriptor ( Properties.ID, "ID" );
            pd.setCategory ( "Event Information" );
            list.add ( pd );
        }
        {
            PropertyDescriptor pd = new PropertyDescriptor ( Properties.TIMESTAMP, "Timestamp" );
            pd.setCategory ( "Event Information" );
            list.add ( pd );
        }
        
        for ( Map.Entry<String, Variant> entry : _event.getAttributes ().entrySet () )
        {
            PropertyDescriptor pd = new PropertyDescriptor ( entry.getKey (), entry.getKey() );
            pd.setAlwaysIncompatible ( true );
            pd.setCategory ( "Event Data" );
            
            list.add ( pd );
        }
    }
    
    public IPropertyDescriptor[] getPropertyDescriptors ()
    {
        List<IPropertyDescriptor> list = new ArrayList<IPropertyDescriptor> ();
        fillPropertyDescriptors ( list );
        return list.toArray ( new IPropertyDescriptor [ list.size () ] );
    }

    public Object getPropertyValue ( Object id )
    {
        if ( id.equals ( Properties.ID ))
            return _event.getId ();
        
        if ( id.equals ( Properties.TIMESTAMP ) )
            return String.format ( "%1$TF %1$TT %1$TN", _event.getTimestamp () );
        
        if ( !(id instanceof String) )
            return null;
        
        String name = (String)id;
        
        return _event.getAttributes ().get ( name ).asString ( null );
    }

    public boolean isPropertySet ( Object id )
    {
        return false;
    }

    public void resetPropertyValue ( Object id )
    {
    }

    public void setPropertyValue ( Object id, Object value )
    {
    }
    
    
}
