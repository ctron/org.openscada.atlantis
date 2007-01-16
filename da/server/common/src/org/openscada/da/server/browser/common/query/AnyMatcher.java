package org.openscada.da.server.browser.common.query;

/**
 * A matcher that matches any item descriptor
 * @author Jens Reimann
 *
 */
public class AnyMatcher implements Matcher
{

    public boolean matches ( ItemDescriptor desc )
    {
        return true;
    }

}
