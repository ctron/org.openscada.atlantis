package org.openscada.da.browser;

import java.util.EnumSet;

import org.openscada.da.core.IODirection;

public interface Item extends Entry {
	public EnumSet<IODirection> getIODirection();
}
