package org.openscada.da.client.test.views.realtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.core.utils.AttributesHelper;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.client.test.impl.HiveItem;

public class ListEntry extends Observable implements ItemUpdateListener
{
    public class AttributePair
    {
        public String key;

        public Variant value;

        public AttributePair ( String key, Variant value )
        {
            super ();
            this.key = key;
            this.value = value;
        }

        @Override
        public int hashCode ()
        {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ( ( key == null ) ? 0 : key.hashCode () );
            result = PRIME * result + ( ( value == null ) ? 0 : value.hashCode () );
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
            final AttributePair other = (AttributePair)obj;
            if ( key == null )
            {
                if ( other.key != null )
                    return false;
            }
            else if ( !key.equals ( other.key ) )
                return false;
            if ( value == null )
            {
                if ( other.value != null )
                    return false;
            }
            else if ( !value.equals ( other.value ) )
                return false;
            return true;
        }
    }

    private static Logger _log = Logger.getLogger ( ListEntry.class );

    private HiveItem _dataItem = null;

    private Variant _value = null;

    private Map<String, Variant> _attributes = new HashMap<String, Variant> ();

    private boolean _subscribed = false;

    public HiveItem getDataItem ()
    {
        return _dataItem;
    }

    public synchronized void setDataItem ( HiveItem dataItem )
    {
        clear ();
        _dataItem = dataItem;
        checkObservers ();
    }

    public synchronized void clear ()
    {
        if ( _dataItem != null )
        {
            unsubscribe ();
            _dataItem = null;
        }
    }

    public Variant getValue ()
    {
        return _value;
    }

    public synchronized List<AttributePair> getAttributes ()
    {
        List<AttributePair> pairs = new ArrayList<AttributePair> ( _attributes.size () );
        for ( Map.Entry<String, Variant> entry : _attributes.entrySet () )
        {
            pairs.add ( new AttributePair ( entry.getKey (), entry.getValue () ) );
        }
        return pairs;
    }

    public void notifyAttributeChange ( Map<String, Variant> attributes, boolean initial )
    {
        _log.debug ( "Attribute change: " + attributes.size () );

        AttributesHelper.mergeAttributes ( _attributes, attributes, initial );

        setChanged ();
        notifyObservers ();
    }

    public synchronized void notifyValueChange ( Variant value, boolean cache )
    {
        _log.debug ( "Value change: " + value );

        _value = value;
        setChanged ();
        notifyObservers ();
    }

    protected synchronized void subscribe ()
    {
        if ( ( _dataItem != null ) && ( !_subscribed ) )
        {
            _log.debug ( "Subscribe: " + _dataItem.getId () );
            _dataItem.getConnection ().getItemManager ().addItemUpdateListener ( _dataItem.getId (), true, this );
            _subscribed = true;
        }
    }

    protected synchronized void unsubscribe ()
    {
        if ( ( _dataItem != null ) && _subscribed )
        {
            _log.debug ( "Unsubscribe: " + _dataItem.getId () );
            _dataItem.getConnection ().getItemManager ().removeItemUpdateListener ( _dataItem.getId (), this );
            _subscribed = false;
        }
    }

    @Override
    public synchronized void addObserver ( Observer o )
    {
        super.addObserver ( o );
        checkObservers ();
    }

    @Override
    public synchronized void deleteObserver ( Observer o )
    {
        super.deleteObserver ( o );
        checkObservers ();
    }

    /**
     * check if there are any observers registered an subscribe if so
     *
     */
    private synchronized void checkObservers ()
    {
        if ( ( countObservers () > 0 ) )
        {
            subscribe ();
        }
        else
        {
            unsubscribe ();
        }
    }

    /**
     * check if attributes are in the list
     * @return <code>true</code> if the attributes list is not empty
     */
    public synchronized boolean hasAttributes ()
    {
        return !_attributes.isEmpty ();
    }
}
