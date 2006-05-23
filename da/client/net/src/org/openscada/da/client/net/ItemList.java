package org.openscada.da.client.net;

import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import org.openscada.da.core.DataItemInformation;

public class ItemList extends Observable implements ItemListListener
{
    private Set<DataItemInformation> _items = new HashSet<DataItemInformation>();
    
    public ItemList ()
    {
    }
    
    public void changed ( Collection<DataItemInformation> added, Collection<String> removed, boolean initial )
    {
        int changes = 0;
        
        synchronized ( _items )
        {
            if ( initial )
            {
                _items.clear ();
                _items = new HashSet<DataItemInformation> ( added );
                changes = _items.size ();
            }
            else
            {
                
                for ( DataItemInformation item : added )
                {
                    if ( _items.add(item) )
                        changes++;
                }
                for ( String item : removed )
                {
                    if ( _items.remove(item) )
                        changes++;
                }
            }
        }
        
        // perform notifaction
        if ( changes > 0 )
        {
            setChanged();
            notifyObservers();
        }
        
    }
    
    public Collection<DataItemInformation> getItemList()
    {
        synchronized ( _items )
        {
            return new HashSet<DataItemInformation> ( _items );
        }
    }
}
