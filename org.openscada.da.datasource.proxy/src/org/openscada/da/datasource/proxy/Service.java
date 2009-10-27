package org.openscada.da.datasource.proxy;

import java.util.Map;

import org.openscada.utils.lang.Disposable;

public interface Service extends Disposable
{
    public void update ( Map<String, String> properties ) throws Exception;
}
