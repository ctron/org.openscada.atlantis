package org.openscada.da.core.browser.common.query;

public class IDNameProvider implements NameProvider
{

    public String getName ( ItemDescriptor descriptor )
    {
        try
        {
            return descriptor.getItem ().getInformation ().getName ();
        }
        catch ( Exception e )
        {
            return null;
        }
    }

}
