package org.openscada.da.core.common.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.common.SuspendableItem;
import org.openscada.da.core.data.AttributesHelper;
import org.openscada.da.core.data.Variant;

public class DataItemInfo {
	private DataItem _item = null;
	private Set<SessionCommon> _sessions = new HashSet<SessionCommon>();
    
    private Variant _cachedValue = new Variant();
    private Map<String,Variant> _cachedAttributes = new HashMap<String,Variant>();

	public DataItemInfo ( DataItem item )
	{
		_item = item;
	}
	
	public Set<SessionCommon> getSessions()
    {
		return _sessions;
	}
	
    /**
     * Add a session to the items listeners
     * <p>
     * Also if the item implements SuspendableItem the {@link SuspendableItem#wakeup()}
     * method is called.
     * @param session the session to add
     */
	public void addSession ( SessionCommon session )
	{
		synchronized ( _sessions )
		{
            if ( _sessions.size () == 0 )
            {
                if ( _item instanceof SuspendableItem )
                    ((SuspendableItem)_item).wakeup ();
            }
            
			_sessions.add ( session );
        }
	}
    
	/**
	 * Remove a session from the items listeners
	 * <p>
	 * Also if the item implements SuspendableItem the {@link SuspendableItem#suspend()}
	 * method is called.
	 * @param session the session to remove
	 */
	public void removeSession ( SessionCommon session )
	{
		synchronized ( _sessions )
		{
			_sessions.remove ( session );
            
            if ( _sessions.size () == 0 )
            {
                if ( _item instanceof SuspendableItem )
                    ((SuspendableItem)_item).suspend ();
            }
		}		
	}
	
	public boolean containsSession ( SessionCommon session )
	{
		synchronized ( _sessions )
		{
			return _sessions.contains(session);
		}		
	}
	
	public void dispose ()
	{
		for ( SessionCommon session : _sessions )
		{
			session.getData().removeItem(_item);
		}
	}

    public Variant getCachedValue ()
    {
        synchronized ( _cachedValue )
        {
            return _cachedValue;
        }
    }

    public void setCachedValue ( Variant cachedValue )
    {
        synchronized ( _cachedValue )
        {
            _cachedValue = new Variant(cachedValue);
        }
    }

    public Map<String, Variant> getCachedAttributes ()
    {
        synchronized ( _cachedAttributes )
        {
            return _cachedAttributes;
        }
    }

    /**
     * merge in the attribute change set into the cached attributes
     * @param attributes the changed attributes
     */
    public void mergeAttributes ( Map<String, Variant> attributes )
    {
        synchronized ( _cachedAttributes )
        {
           AttributesHelper.mergeAttributes ( _cachedAttributes, attributes );
        }
    }
	
}
