package org.openscada.da.core.common;

import java.util.EnumSet;
import java.util.Map;

import org.openscada.da.core.IODirection;
import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.data.Variant;

public abstract class DataItemOutput extends DataItemBase {

	public DataItemOutput(String name) {
		super(name);
	}

	public EnumSet<IODirection> getIODirection() {
		return EnumSet.of(IODirection.OUTPUT);
	}

	public Variant getValue() throws InvalidOperationException {
		throw new InvalidOperationException();
	}

}
