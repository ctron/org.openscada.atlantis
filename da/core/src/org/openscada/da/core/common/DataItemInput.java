package org.openscada.da.core.common;

import java.util.EnumSet;
import java.util.Map;

import org.openscada.da.core.IODirection;
import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;

public abstract class DataItemInput extends DataItemBase {

	public DataItemInput(String name) {
		super(name);
	}

	public EnumSet<IODirection> getIODirection() {
		return EnumSet.of(IODirection.INPUT);
	}

	public void setValue(Variant value) throws InvalidOperationException, NullValueException, NotConvertableException {
		throw new InvalidOperationException();
	}
	
}
