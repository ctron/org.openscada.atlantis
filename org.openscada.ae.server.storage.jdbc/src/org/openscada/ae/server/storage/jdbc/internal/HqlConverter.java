/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.server.storage.jdbc.internal;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.openscada.ae.event.FilterUtils;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.utils.filter.Assertion;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.filter.FilterAssertion;
import org.openscada.utils.filter.FilterExpression;
import org.openscada.utils.filter.Operator;

public class HqlConverter
{
    private static final Map<String, Class<?>> properties = new HashMap<String, Class<?>> ();

    static
    {
        for ( final PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors ( MutableEvent.class ) )
        {
            properties.put ( pd.getName (), pd.getPropertyType () );
        }
    }

    public static class HqlResult
    {

        private String hql = "";

        private Object[] parameters = new Object[] {};

        public String getHql ()
        {
            return this.hql;
        }

        public void setHql ( final String hql )
        {
            this.hql = hql;
        }

        public Object[] getParameters ()
        {
            return this.parameters;
        }

        public void setParameters ( final Object[] parameters )
        {
            this.parameters = parameters;
        }
    }

    public static HqlResult toHql ( final String instance, final Filter filter ) throws NotSupportedException
    {
        final HqlResult result = new HqlResult ();
        result.hql = String.format ( "SELECT M from MutableEvent M left join fetch M.attributes as A WHERE M.instance = '%s'", instance );
        if ( filter.isEmpty () )
        {
            // pass
        }
        else if ( filter.isExpression () )
        {
            final HqlResult h = toHql ( (FilterExpression)filter );
            result.hql += " AND " + h.hql;
            result.parameters = combine ( result.parameters, h.parameters );
        }
        else if ( filter.isAssertion () )
        {
            final HqlResult h = toHql ( (FilterAssertion)filter );
            result.hql += " AND " + h.hql;
            result.parameters = combine ( result.parameters, h.parameters );
        }
        else
        {
            //
        }
        result.hql += " ORDER BY M.sourceTimestamp DESC, M.entryTimestamp DESC, M.id DESC";
        return result;
    }

    static HqlResult toHql ( final FilterExpression expression ) throws NotSupportedException
    {
        final HqlResult result = new HqlResult ();
        result.hql = "(";
        int i = 0;
        for ( final Filter term : expression.getFilterSet () )
        {
            if ( i > 0 )
            {
                if ( expression.getOperator () == Operator.AND )
                {
                    result.hql += " AND ";
                }
                else if ( expression.getOperator () == Operator.OR )
                {
                    result.hql += " OR ";
                }
            }
            if ( term.isExpression () )
            {
                final HqlResult r = toHql ( (FilterExpression)term );
                result.hql += r.hql;
                result.parameters = combine ( result.parameters, r.parameters );
            }
            else if ( term.isAssertion () )
            {
                final HqlResult r = toHql ( (FilterAssertion)term );
                result.hql += r.hql;
                result.parameters = combine ( result.parameters, r.parameters );
            }
            i++;
        }
        if ( expression.getOperator () == Operator.NOT )
        {
            result.hql = "NOT " + result.hql;
        }
        result.hql += ")";
        return result;
    }

    static HqlResult toHql ( final FilterAssertion assertion ) throws NotSupportedException
    {
        HqlResult result = null;
        if ( assertion.getAssertion () == Assertion.EQUALITY )
        {
            result = toHql ( assertion.getAttribute (), "=", assertion.getValue () );
        }
        else if ( assertion.getAssertion () == Assertion.GREATEREQ )
        {
            result = toHql ( assertion.getAttribute (), ">=", assertion.getValue () );
        }
        else if ( assertion.getAssertion () == Assertion.GREATERTHAN )
        {
            result = toHql ( assertion.getAttribute (), ">", assertion.getValue () );
        }
        else if ( assertion.getAssertion () == Assertion.LESSEQ )
        {
            result = toHql ( assertion.getAttribute (), "<=", assertion.getValue () );
        }
        else if ( assertion.getAssertion () == Assertion.LESSTHAN )
        {
            result = toHql ( assertion.getAttribute (), "<", assertion.getValue () );
        }
        else if ( assertion.getAssertion () == Assertion.APPROXIMATE )
        {
            result = toHql ( assertion.getAttribute (), "approximate", assertion.getValue () );
        }
        else if ( assertion.getAssertion () == Assertion.SUBSTRING )
        {
            result = toHql ( assertion.getAttribute (), "like", assertion.getValue () );
        }
        else if ( assertion.getAssertion () == Assertion.PRESENCE )
        {
            result = toHql ( assertion.getAttribute (), "presence", assertion.getValue () );
        }
        else
        {
            throw new NotSupportedException ();
        }
        return result;
    }

    static HqlResult toHql ( final String field, final String op, final Object value )
    {
        final HqlResult term = new HqlResult ();
        term.hql = "(";
        if ( isField ( field ) && properties.get ( field ) != Variant.class )
        {
            if ( "presence".equals ( op ) )
            {
                term.hql += "M." + field + " IS NOT NULL)";
            }
            else
            {
                if ( value != null && value instanceof Variant )
                {
                    final PropertyEditor pe = FilterUtils.propertyEditorRegistry.findCustomEditor ( properties.get ( field ) );
                    pe.setAsText ( Variant.valueOf ( value ).asString ( "" ) );
                    term.parameters = new Object[] { pe.getValue () };
                }
                else
                {
                    term.parameters = new Object[] { value };
                }
                if ( "like".equals ( op ) )
                {
                    term.hql += "lower(M." + field + ") like lower(?))";
                }
                else if ( "approximate".equals ( op ) )
                {
                    term.hql += "soundex(M." + field + ") = soundex(?))";
                }
                else
                {
                    term.hql += "M." + field + " " + op + " ?)";
                }
            }
        }
        else if ( isField ( field ) && properties.get ( field ) == Variant.class )
        {
            if ( "presence".equals ( op ) )
            {
                term.hql += "M." + field + ".string IS NOT NULL OR M." + field + ".integer IS NOT NULL OR M." + field + ".double IS NOT NULL)";
            }
            else
            {
                if ( "like".equals ( op ) )
                {
                    term.hql += "lower(M." + field + ".string) like lower(?))";
                    if ( value == null )
                    {
                        term.parameters = new Object[] { null };
                    }
                    else
                    {
                        term.parameters = new Object[] { Variant.valueOf ( value ).asString ( "" ) };
                    }
                }
                else if ( "approximate".equals ( op ) )
                {
                    term.hql += "soundex(M." + field + ".string) = soundex(?))";
                    if ( value == null )
                    {
                        term.parameters = new Object[] { null };
                    }
                    else
                    {
                        term.parameters = new Object[] { Variant.valueOf ( value ).asString ( "" ) };
                    }
                }
                else
                {
                    if ( value == null )
                    {
                        term.parameters = new Object[] { null, null, null };
                    }
                    else
                    {
                        term.parameters = new Object[] { Variant.valueOf ( value ).asString ( null ), Variant.valueOf ( value ).asLong ( null ), Variant.valueOf ( value ).asDouble ( null ) };
                    }
                    term.hql += "(M." + field + ".string " + op + " ?) OR (M." + field + ".integer " + op + " ?) OR (M." + field + ".double " + op + " ?))";
                }
            }
        }
        else
        {
            final Variant v = (Variant)value;
            final String strValue = v.asString ( "" );
            Long longValue = null;
            Double doubleValue = null;
            try
            {
                longValue = v.asLong ();
                doubleValue = v.asDouble ();
            }
            catch ( final NullValueException e )
            {
                longValue = null;
                doubleValue = null;
            }
            catch ( final NotConvertableException e )
            {
                longValue = null;
                doubleValue = null;
            }
            if ( "like".equals ( op ) )
            {
                term.hql += "index(A) = '" + field + "' AND (lower(A.string) " + op + " lower(?)))";
                term.parameters = new Object[] { strValue };
            }
            else if ( "approximate".equals ( op ) )
            {
                term.hql += "index(A) = '" + field + "' AND (soundex(A.string) = soundex(?)))";
                term.parameters = new Object[] { strValue };
            }
            else if ( "presence".equals ( op ) )
            {
                term.hql += "index(A) = '" + field + "')";
                term.parameters = new Object[] {};
            }
            else
            {
                // special case for string
                if ( strValue != null && longValue == null && doubleValue == null )
                {
                    term.hql += "index(A) = '" + field + "' AND (A.string " + op + " ?))";
                    term.parameters = new Object[] { strValue };
                }
                else
                {
                    term.hql += "index(A) = '" + field + "' AND (A.string " + op + " ? OR A.integer " + op + " ? OR A.double " + op + " ?))";
                    term.parameters = new Object[] { strValue, longValue, doubleValue };
                }
            }
        }
        return term;
    }

    static boolean isField ( final String field )
    {
        return properties.keySet ().contains ( field );
    }

    static Object[] combine ( final Object[] a, final Object[] b )
    {
        final List<Object> l = new ArrayList<Object> ();
        if ( a != null )
        {
            l.addAll ( Arrays.asList ( a ) );
        }
        if ( b != null )
        {
            l.addAll ( Arrays.asList ( b ) );
        }
        return l.toArray ();
    }
}
