package org.openscada.da.core.browser.common.query;

public class NullNameProvider implements NameProvider
{

    public String getName ( ItemDescriptor descriptor )
    {
        return null;
    }

}
