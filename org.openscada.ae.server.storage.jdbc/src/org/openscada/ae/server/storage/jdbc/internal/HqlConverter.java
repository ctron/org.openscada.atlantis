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
        for ( PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors ( MutableEvent.class ) )
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

    public static HqlResult toHql ( final Filter filter ) throws NotSupportedException
    {
        HqlResult result = new HqlResult ();
        result.hql = "SELECT M from MutableEvent M left join fetch M.attributes as A";
        if ( filter.isEmpty () )
        {
            // pass
        }
        else if ( filter.isExpression () )
        {
            HqlResult h = toHql ( (FilterExpression)filter );
            result.hql += " WHERE " + h.hql;
            result.parameters = combine ( result.parameters, h.parameters );
        }
        else if ( filter.isAssertion () )
        {
            HqlResult h = toHql ( (FilterAssertion)filter );
            result.hql += " WHERE " + h.hql;
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
        HqlResult result = new HqlResult ();
        result.hql = "(";
        int i = 0;
        for ( Filter term : expression.getFilterSet () )
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
                HqlResult r = toHql ( (FilterExpression)term );
                result.hql += r.hql;
                result.parameters = combine ( result.parameters, r.parameters );
            }
            else if ( term.isAssertion () )
            {
                HqlResult r = toHql ( (FilterAssertion)term );
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
        HqlResult term = new HqlResult ();
        term.hql = "(";
        if ( isField ( field ) && ( properties.get ( field ) != Variant.class ) )
        {
            if ( "presence".equals ( op ) )
            {
                term.hql += "M." + field + " IS NOT NULL)";
            }
            else
            {
                if ( ( value != null ) && ( value instanceof Variant ) )
                {
                    PropertyEditor pe = FilterUtils.propertyEditorRegistry.findCustomEditor ( properties.get ( field ) );
                    pe.setAsText ( new Variant ( value ).asString ( "" ) );
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
        else if ( isField ( field ) && ( properties.get ( field ) == Variant.class ) )
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
                        term.parameters = new Object[] { new Variant ( value ).asString ( "" ) };
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
                        term.parameters = new Object[] { new Variant ( value ).asString ( "" ) };
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
                        term.parameters = new Object[] { new Variant ( value ).asString ( null ), new Variant ( value ).asLong ( null ), new Variant ( value ).asDouble ( null ) };
                    }
                    term.hql += "(M." + field + ".string " + op + " ?) OR (M." + field + ".integer " + op + " ?) OR (M." + field + ".double " + op + " ?))";
                }
            }
        }
        else
        {
            Variant v = (Variant)value;
            String strValue = v.asString ( "" );
            Long longValue = null;
            Double doubleValue = null;
            try
            {
                longValue = v.asLong ();
                doubleValue = v.asDouble ();
            }
            catch ( NullValueException e )
            {
                longValue = null;
                doubleValue = null;
            }
            catch ( NotConvertableException e )
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
        List<Object> l = new ArrayList<Object> ();
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
