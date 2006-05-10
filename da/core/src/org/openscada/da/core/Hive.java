package org.openscada.da.core;

import java.util.Collection;
import java.util.List;
import java.util.Properties;


public interface Hive {
	public Session createSession ( Properties props );
	public void closeSession ( Session session ) throws InvalidSessionException;
	
	public void registerForItem ( Session session, String item ) throws InvalidSessionException, InvalidItemException;
	public void unregisterForItem ( Session session, String item ) throws InvalidSessionException, InvalidItemException;
	
	public void registerForAll ( Session session ) throws InvalidSessionException;
	public void unregisterForAll ( Session session ) throws InvalidSessionException;
	
	// enumerate
	Collection<String> listItems ( Session session ) throws InvalidSessionException;
}
