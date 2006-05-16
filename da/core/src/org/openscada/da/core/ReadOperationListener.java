package org.openscada.da.core;

import org.openscada.da.core.data.Variant;

public interface ReadOperationListener
{
    void success ( Variant value );
    void failure ( String errorMessage );
}
