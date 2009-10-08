package org.openscada.core.ui.connection;

import org.eclipse.core.runtime.CoreException;
import org.openscada.core.ConnectionInformation;

public interface ConnectionStore
{

    public abstract void remove ( final ConnectionInformation connectionInformation ) throws CoreException;

    public abstract void add ( final ConnectionInformation connectionInformation ) throws CoreException;

}
