package org.openscada.hd.ui.connection.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.openscada.hd.ui.connection.internal.messages"; //$NON-NLS-1$

    public static String ItemPropertySource_Connection_Category;

    public static String ItemPropertySource_Connection_URI_Label;

    public static String ItemPropertySource_Item_Attributes_Category;

    public static String ItemPropertySource_Item_Category;

    public static String ItemPropertySource_Item_ID_Label;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages ( BUNDLE_NAME, Messages.class );
    }

    private Messages ()
    {
    }
}
