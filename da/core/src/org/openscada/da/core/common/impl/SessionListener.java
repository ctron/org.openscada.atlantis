package org.openscada.da.core.common.impl;

public interface SessionListener
{
    void create ( SessionCommon session );
    void destroy ( SessionCommon session );
}
