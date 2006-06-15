package org.openscada.da.core.browser.common.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class InvisibleStorage implements ItemStorage
{
    private Set<ItemDescriptor> _items = new HashSet<ItemDescriptor> ();
    private Collection<ItemStorage> _childs = new LinkedList <ItemStorage> ();
    
    public void added ( ItemDescriptor descriptor )
    {
        synchronized ( this )
        {
            if ( _items.contains ( descriptor ) )
                return;

            _items.add ( descriptor );
            notifyAdd ( descriptor );
        }
    }

    public void removed ( ItemDescriptor descriptor )
    {
        synchronized ( this )
        {
            if ( !_items.contains ( descriptor ) )
                return;

            _items.remove ( descriptor );
            notifyRemove ( descriptor );
        }
    }
    
    public void addChild ( ItemStorage child )
    {
        synchronized ( this )
        {
            _childs.add ( child );

            // now push all possible descriptors
            for ( ItemDescriptor desc : _items )
            {
                child.added ( desc );
            }
        }
    }
    
    public void removeChild ( ItemStorage child )
    {
        synchronized ( this )
        {
            _childs.remove ( child );
        }
    }
    
    private void notifyAdd ( ItemDescriptor desc )
    {
        // notify childs
        for ( ItemStorage child : _childs )
        {
            child.added ( desc );
        }
    }
    
    private void notifyRemove ( ItemDescriptor desc )
    {
        // notify childs
        for ( ItemStorage child : _childs )
        {
            child.removed ( desc );
        }
    }

}
