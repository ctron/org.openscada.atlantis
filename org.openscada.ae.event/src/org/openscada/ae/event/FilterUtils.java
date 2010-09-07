/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

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
    public static void toVariant ( final Filter filter )
    {
        if ( filter.isAssertion () )
        {
            final FilterAssertion filterAssertion = (FilterAssertion)filter;
            // first convert String for case of like query * -> %
            if ( filterAssertion.getValue () instanceof List && filterAssertion.getAssertion () == Assertion.SUBSTRING )
            {
                final List<String> values = (List<String>)filterAssertion.getValue ();
                final StringBuilder sb = new StringBuilder ();
                int i = 0;
                for ( final String string : values )
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
                    final PropertyEditor pe = propertyEditorRegistry.findCustomEditor ( UUID.class );
                    pe.setAsText ( (String)filterAssertion.getValue () );
                    filterAssertion.setValue ( pe.getValue () );
                }
                else if ( "sourceTimestamp".equals ( filterAssertion.getAttribute () ) || "entryTimestamp".equals ( filterAssertion.getAttribute () ) )
                {
                    final PropertyEditor pe = propertyEditorRegistry.findCustomEditor ( Date.class );
                    pe.setAsText ( (String)filterAssertion.getValue () );
                    filterAssertion.setValue ( pe.getValue () );
                }
                else
                {
                    final VariantEditor ve = new VariantEditor ();
                    ve.setAsText ( (String)filterAssertion.getValue () );
                    filterAssertion.setValue ( ve.getValue () );
                }
            }
        }
        else if ( filter.isExpression () )
        {
            final FilterExpression filterExpression = (FilterExpression)filter;
            for ( final Filter child : filterExpression.getFilterSet () )
            {
                toVariant ( child );
            }
        }
    }
}
