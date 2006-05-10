package org.openscada.da.browser;

import java.util.Collection;


public interface Folder extends Entry {
	public Collection<Entry> list ();
}
