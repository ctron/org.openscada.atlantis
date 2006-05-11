package org.openscada.da.core;

public interface Session {
	public void setListener ( ItemChangeListener listener );
    public void setListener ( ItemListListener listener );
}
