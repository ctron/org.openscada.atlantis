package org.openscada.ae.event;

import java.beans.PropertyEditor;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.openscada.core.Variant;
import org.openscada.core.VariantEditor;
import org.openscada.utils.filter.Assertion;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.filter.FilterAssertion;
import org.openscada.utils.filter.FilterExpression;
import org.openscada.utils.propertyeditors.DateEditor;
import org.openscada.utils.propertyeditors.IntegerEditor;
import org.openscada.utils.propertyeditors.PropertyEditorRegistry;
import org.openscada.utils.propertyeditors.StringEditor;
import org.openscada.utils.propertyeditors.UUIDEditor;

public class FilterUtils
{
    public static PropertyEditorRegistry propertyEditorRegistry = new PropertyEditorRegistry ();

    static
    {
        propertyEditorRegistry.registerCustomEditor ( String.class, new StringEditor () );
        propertyEditorRegistry.registerCustomEditor ( Integer.class, new IntegerEditor () );
        propertyEditorRegistry.registerCustomEditor ( Date.class, new DateEditor () );
        propertyEditorRegistry.registerCustomEditor ( java.sql.Date.class, new DateEditor () );
        propertyEditorRegistry.registerCustomEditor ( Calendar.class, new DateEditor () );
        propertyEditorRegistry.registerCustomEditor ( UUID.class, new UUIDEditor () );
        propertyEditorRegistry.registerCustomEditor ( Variant.class, new VariantEditor () );
    }

    /**
     * converts string values in filter to actual Variant Values
     * 
     * @param filter
     * @return
     */
    @SuppressWarnings ( "unchecked" )
    public static void toVariant ( Filter filter )
    {
        if ( filter.isAssertion () )
        {
            FilterAssertion filterAssertion = (FilterAssertion)filter;
            // first convert String for case of like query * -> %
            if ( ( filterAssertion.getValue () instanceof List ) && filterAssertion.getAssertion () == Assertion.SUBSTRING )
            {
                List<String> values = (List<String>)filterAssertion.getValue ();
                StringBuilder sb = new StringBuilder ();
                int i = 0;
                for ( String string : values )
                {
                    if ( i > 0 && i < values.size () )
                    {
                        sb.append ( "%" );
                    }
                    sb.append ( string );
                    i += 1;
                }
                filterAssertion.setValue ( sb.toString () );
            }
            if ( filterAssertion.getValue () instanceof String )
            {
                if ( "id".equals ( filterAssertion.getAttribute () ) )
                {
                    PropertyEditor pe = propertyEditorRegistry.findCustomEditor ( UUID.class );
                    pe.setAsText ( (String)filterAssertion.getValue () );
                    filterAssertion.setValue ( pe.getValue () );
                }
                else if ( "sourceTimestamp".equals ( filterAssertion.getAttribute () ) || "entryTimestamp".equals ( filterAssertion.getAttribute () ) )
                {
                    PropertyEditor pe = propertyEditorRegistry.findCustomEditor ( Date.class );
                    pe.setAsText ( (String)filterAssertion.getValue () );
                    filterAssertion.setValue ( pe.getValue () );
                }
                else if ( "type".equals ( filterAssertion.getAttribute () ) || "source".equals ( filterAssertion.getAttribute () ) )
                {
                    PropertyEditor pe = propertyEditorRegistry.findCustomEditor ( String.class );
                    pe.setAsText ( (String)filterAssertion.getValue () );
                    filterAssertion.setValue ( pe.getValue () );
                }
                else if ( "priority".equals ( filterAssertion.getAttribute () ) )
                {
                    PropertyEditor pe = propertyEditorRegistry.findCustomEditor ( Integer.class );
                    pe.setAsText ( (String)filterAssertion.getValue () );
                    filterAssertion.setValue ( pe.getValue () );
                }
                else
                {
                    VariantEditor ve = new VariantEditor ();
                    ve.setAsText ( (String)filterAssertion.getValue () );
                    filterAssertion.setValue ( ve.getValue () );
                }
            }
        }
        else if ( filter.isExpression () )
        {
            FilterExpression filterExpression = (FilterExpression)filter;
            for ( Filter child : filterExpression.getFilterSet () )
            {
                toVariant ( child );
            }
        }
    }
}
