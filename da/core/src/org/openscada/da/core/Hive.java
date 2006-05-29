package org.openscada.da.core;

import java.util.Collection;
import java.util.Properties;

import org.openscada.da.core.data.Variant;

public interface Hive
{
	public Session createSession ( Properties props ) throws UnableToCreateSessionException;
	public void closeSession ( Session session ) throws InvalidSessionException;
	
	public void registerForItem ( Session session, String item, boolean initial ) throws InvalidSessionException, InvalidItemException;
	public void unregisterForItem ( Session session, String item ) throws InvalidSessionException, InvalidItemException;
	
	public void registerForAll ( Session session ) throws InvalidSessionException;
	public void unregisterForAll ( Session session ) throws InvalidSessionException;
	
    public void registerItemList ( Session session ) throws InvalidSessionException;
    public void unregisterItemList ( Session session ) throws InvalidSessionException;
    
	// enumerate
	public Collection<DataItemInformation> listItems ( Session session ) throws InvalidSessionException;
    
    // async DA operations
    public void startWrite ( Session session, String itemName, Variant value, WriteOperationListener listener ) throws InvalidSessionException, InvalidItemException;
    //public void startRead ( Session session, String item, Variant value, ReadOperationListener listener ); 
}
