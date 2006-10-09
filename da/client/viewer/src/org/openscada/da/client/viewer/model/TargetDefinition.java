package org.openscada.da.client.viewer.model;

import java.util.EnumSet;

public interface TargetDefinition
{
    public abstract String getName ();
    public abstract EnumSet<Type> getSupportedTypes ();
}
