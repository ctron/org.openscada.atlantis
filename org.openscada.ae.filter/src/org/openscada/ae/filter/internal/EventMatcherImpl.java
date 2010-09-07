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

package org.openscada.ae.filter.internal;

import java.beans.PropertyEditor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

import org.openscada.ae.Event;
import org.openscada.ae.filter.EventMatcher;
import org.openscada.core.Variant;
import org.openscada.core.VariantEditor;
import org.openscada.utils.filter.Assertion;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.filter.FilterAssertion;
import org.openscada.utils.filter.FilterExpression;
import org.openscada.utils.filter.FilterParser;
import org.openscada.utils.filter.Operator;
import org.openscada.utils.lang.Apply;
import org.openscada.utils.propertyeditors.DateEditor;
import org.openscada.utils.propertyeditors.IntegerEditor;
import org.openscada.utils.propertyeditors.PropertyEditorRegistry;
import org.openscada.utils.propertyeditors.StringEditor;
import org.openscada.utils.propertyeditors.UUIDEditor;
import org.openscada.utils.str.StringHelper;

public class EventMatcherImpl implements EventMatcher
{
    private static final DateFormat isoDateFormat = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss.S" );

    private static PropertyEditorRegistry propertyEditorRegistry = new PropertyEditorRegistry ();

    private final Filter filter;

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

    public EventMatcherImpl ( final String filter )
    {
        this.filter = new FilterParser ( filter ).getFilter ();
    }

    public EventMatcherImpl ( final Filter filter )
    {
        this.filter = filter;
    }

    public boolean matches ( final Event event )
    {
        return matches ( this.filter, event );
    }

    private static boolean matches ( final Filter filter, final Event event )
    {
        if ( filter.isEmpty () )
        {
            return true;
        }
        if ( filter.isAssertion () )
        {
            return matches ( (FilterAssertion)filter, event );
        }
        if ( filter.isExpression () )
        {
            return matches ( (FilterExpression)filter, event );
        }
        return false;
    }

    private static boolean matches ( final FilterExpression expression, final Event event )
    {
        if ( expression.getOperator () == Operator.AND )
        {
            boolean result = true;
            for ( final Filter subFilter : expression.getFilterSet () )
            {
                result = result && matches ( subFilter, event );
            }
            return result;
        }
        else if ( expression.getOperator () == Operator.OR )
        {
            boolean result = false;
            for ( final Filter subFilter : expression.getFilterSet () )
            {
                result = result || matches ( subFilter, event );
            }
            return result;
        }
        else if ( expression.getOperator () == Operator.NOT )
        {
            boolean result = true;
            for ( final Filter subFilter : expression.getFilterSet () )
            {
                result = result && matches ( subFilter, event );
            }
            return !result;
        }
        return false;
    }

    private static boolean matches ( final FilterAssertion assertion, final Event event )
    {
        // special case id
        if ( "id".equals ( assertion.getAttribute () ) )
        {
            return compareId ( assertion, event.getId () );
        }
        // special case source/entryTimestamp
        else if ( "sourceTimestamp".equals ( assertion.getAttribute () ) )
        {
            return compareTimestamp ( assertion, event.getSourceTimestamp () );
        }
        else if ( "entryTimestamp".equals ( assertion.getAttribute () ) )
        {
            return compareTimestamp ( assertion, event.getEntryTimestamp () );
        }
        return compareVariant ( assertion, event.getAttributes ().get ( assertion.getAttribute () ) );
    }

    @SuppressWarnings ( "unchecked" )
    private static boolean compareId ( final FilterAssertion assertion, final UUID left )
    {
        if ( assertion.getAssertion () == Assertion.PRESENCE )
        {
            return left != null;
        }
        if ( assertion.getValue () == null )
        {
            return false;
        }
        if ( ! ( ( assertion.getValue () instanceof String ) || ( assertion.getValue () instanceof UUID ) || ( assertion.getValue () instanceof Collection<?> ) ) )
        {
            return false;
        }
        if ( assertion.getAssertion () == Assertion.SUBSTRING )
        {
            return left.toString ().matches ( toRegEx ( (Collection<String>)assertion.getValue () ) );
        }
        final UUID right = UUID.fromString ( assertion.getValue ().toString () );
        switch ( assertion.getAssertion () )
        {
        case LESSTHAN:
            return left.compareTo ( right ) == -1;
        case LESSEQ:
            return ( left.compareTo ( right ) == -1 ) || ( left.compareTo ( right ) == 0 );
        case EQUALITY:
            return left.compareTo ( right ) == 0;
        case GREATEREQ:
            return ( left.compareTo ( right ) == 1 ) || ( left.compareTo ( right ) == 0 );
        case GREATERTHAN:
            return left.compareTo ( right ) == 1;
        case APPROXIMATE:
            throw new IllegalArgumentException ( "Assertion APPROXIMATE is not supported" );
        }
        return false;
    }

    @SuppressWarnings ( "unchecked" )
    private static boolean compareTimestamp ( final FilterAssertion assertion, final Date left )
    {
        if ( assertion.getAssertion () == Assertion.PRESENCE )
        {
            return left != null;
        }
        if ( assertion.getValue () == null )
        {
            return false;
        }
        if ( ! ( ( assertion.getValue () instanceof String ) || ( assertion.getValue () instanceof Date ) || ( assertion.getValue () instanceof Collection<?> ) ) )
        {
            return false;
        }
        if ( assertion.getAssertion () == Assertion.SUBSTRING )
        {
            return isoDateFormat.format ( left ).matches ( toRegEx ( (Collection<String>)assertion.getValue () ) );
        }
        Date right = null;
        if ( assertion.getValue () instanceof String )
        {
            final PropertyEditor pe = propertyEditorRegistry.findCustomEditor ( Date.class );
            pe.setAsText ( (String)assertion.getValue () );
            right = (Date)pe.getValue ();
        }
        else if ( assertion.getValue () instanceof Date )
        {
            right = (Date)assertion.getValue ();
        }
        else
        {
            throw new IllegalArgumentException ( "Assertion value type is not supported" );
        }
        switch ( assertion.getAssertion () )
        {
        case LESSTHAN:
            return left.compareTo ( right ) == -1;
        case LESSEQ:
            return ( left.compareTo ( right ) == -1 ) || ( left.compareTo ( right ) == 0 );
        case EQUALITY:
            return left.compareTo ( right ) == 0;
        case GREATEREQ:
            return ( left.compareTo ( right ) == 1 ) || ( left.compareTo ( right ) == 0 );
        case GREATERTHAN:
            return left.compareTo ( right ) == 1;
        case APPROXIMATE:
            throw new IllegalArgumentException ( "Assertion APPROXIMATE is not supported" );
        }
        return false;
    }

    @SuppressWarnings ( "unchecked" )
    private static boolean compareVariant ( final FilterAssertion assertion, final Variant left )
    {
        if ( assertion.getAssertion () == Assertion.PRESENCE )
        {
            return left != null;
        }
        if ( assertion.getValue () == null )
        {
            return false;
        }
        if ( left == null )
        {
            return false;
        }
        if ( ! ( ( assertion.getValue () instanceof String ) || ( assertion.getValue () instanceof Date ) || ( assertion.getValue () instanceof Collection<?> ) ) )
        {
            return false;
        }
        if ( assertion.getAssertion () == Assertion.SUBSTRING )
        {
            return left.asString ( "" ).matches ( toRegEx ( (Collection<String>)assertion.getValue () ) );
        }
        Variant right = null;
        if ( assertion.getValue () instanceof String )
        {
            final PropertyEditor pe = propertyEditorRegistry.findCustomEditor ( Variant.class );
            pe.setAsText ( (String)assertion.getValue () );
            right = (Variant)pe.getValue ();
        }
        else if ( assertion.getValue () instanceof Variant )
        {
            right = (Variant)assertion.getValue ();
        }
        else
        {
            throw new IllegalArgumentException ( "Assertion value type is not supported" );
        }
        switch ( assertion.getAssertion () )
        {
        case LESSTHAN:
            return left.compareTo ( right ) == -1;
        case LESSEQ:
            return ( left.compareTo ( right ) == -1 ) || ( left.compareTo ( right ) == 0 );
        case EQUALITY:
            return left.compareTo ( right ) == 0;
        case GREATEREQ:
            return ( left.compareTo ( right ) == 1 ) || ( left.compareTo ( right ) == 0 );
        case GREATERTHAN:
            return left.compareTo ( right ) == 1;
        case APPROXIMATE:
            throw new IllegalArgumentException ( "Assertion APPROXIMATE is not supported" );
        }
        return false;
    }

    private static String toRegEx ( final Collection<String> parts )
    {
        return StringHelper.join ( parts, ".*", new Apply<String> () {
            public String apply ( final String parameter )
            {
                return Pattern.quote ( parameter );
            }
        } );
    }
}
