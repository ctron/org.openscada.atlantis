package org.openscada.da.core.browser;

import java.util.Map;

import org.openscada.da.core.data.Variant;

public interface Entry
{
    String getName ();
    Map < String, Variant > getAttributes ();
}
