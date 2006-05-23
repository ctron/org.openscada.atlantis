package org.openscada.da.core.common;

import java.util.EnumSet;

import org.openscada.da.core.IODirection;
import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.data.Variant;

public abstract class DataItemOutput extends DataItemBase {

	public DataItemOutput ( String name )
    {
		super ( new DataItemInformationBase ( name, EnumSet.of ( IODirection.OUTPUT ) ) );
	}

	public Variant getValue() throws InvalidOperationException
    {
		throw new InvalidOperationException ();
	}

}
