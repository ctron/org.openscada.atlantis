package org.openscada.hd.ui.connection.views;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.openscada.hd.ui.connection.views.messages"; //$NON-NLS-1$

    public static String ConnectionLabelProvider_Items;

    public static String ConnectionLabelProvider_Queries;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages ( BUNDLE_NAME, Messages.class );
    }

    private Messages ()
    {
    }
}
