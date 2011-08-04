package org.openscada.ae.server.storage.jdbc;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.openscada.ae.Event.Fields;
import org.openscada.core.Variant;
import org.openscada.core.VariantEditor;
import org.openscada.utils.filter.Assertion;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.filter.FilterAssertion;
import org.openscada.utils.filter.FilterExpression;
import org.openscada.utils.filter.Operator;
import org.openscada.utils.str.StringHelper;

public class SqlConverter
{
    private static final DateFormat isoDateFormat = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss.S" );

    private static final Map<String, String> fieldToColumn = new HashMap<String, String> ();

    public static final List<String> fixedAttributes = Arrays.asList ( "id", "sourceTimestamp", "entryTimestamp" );

    public static final List<String> inlinedAttributes = Arrays.asList ( Fields.MONITOR_TYPE.getName (), Fields.EVENT_TYPE.getName (), Fields.VALUE.getName () //
    , Fields.MESSAGE.getName (), Fields.MESSAGE_CODE.getName (), Fields.PRIORITY.getName () //
    , Fields.SOURCE.getName (), Fields.ACTOR_NAME.getName (), Fields.ACTOR_TYPE.getName () );

    static
    {
        fieldToColumn.put ( "id", "E.ID" );
        fieldToColumn.put ( "sourceTimestamp", "E.SOURCE_TIMESTAMP" );
        fieldToColumn.put ( "entryTimestamp", "E.ENTRY_TIMESTAMP" );
        fieldToColumn.put ( Fields.MONITOR_TYPE.getName (), "E.MONITOR_TYPE" );
        fieldToColumn.put ( Fields.EVENT_TYPE.getName (), "E.EVENT_TYPE" );
        fieldToColumn.put ( Fields.MESSAGE.getName (), "E.MESSAGE" );
        fieldToColumn.put ( Fields.MESSAGE_CODE.getName (), "E.MESSAGE_CODE" );
        fieldToColumn.put ( Fields.PRIORITY.getName (), "E.PRIORITY" );
        fieldToColumn.put ( Fields.SOURCE.getName (), "E.SOURCE" );
        fieldToColumn.put ( Fields.ACTOR_NAME.getName (), "E.ACTOR_NAME" );
        fieldToColumn.put ( Fields.ACTOR_TYPE.getName (), "E.ACTOR_TYPE" );
    }

    public static SqlCondition toSql ( String schema, Filter filter ) throws NotSupportedException
    {
        AtomicInteger j = new AtomicInteger ( 0 );
        SqlCondition condition = new SqlCondition ();
        if ( filter.isEmpty () )
        {
            // pass
        }
        else if ( filter.isExpression () )
        {
            SqlCondition expression = toSql ( schema, (FilterExpression)filter, j );
            condition.condition += " AND " + expression.condition;
            condition.joins.addAll ( expression.joins );
            condition.joinParameters.addAll ( expression.joinParameters );
            condition.parameters.addAll ( expression.parameters );
        }
        else if ( filter.isAssertion () )
        {
            SqlCondition assertion = toSql ( schema, (FilterAssertion)filter, j );
            condition.condition += " AND " + assertion.condition;
            condition.joins.addAll ( assertion.joins );
            condition.joinParameters.addAll ( assertion.joinParameters );
            condition.parameters.addAll ( assertion.parameters );
        }
        else
        {
            // pass
        }
        return condition;
    }

    private static SqlCondition toSql ( String schema, FilterAssertion assertion, AtomicInteger j ) throws NotSupportedException
    {
        SqlCondition result = null;
        if ( assertion.getAssertion () == Assertion.EQUALITY )
        {
            result = toSql ( schema, assertion.getAttribute (), "=", assertion.getValue (), j );
        }
        else if ( assertion.getAssertion () == Assertion.GREATEREQ )
        {
            result = toSql ( schema, assertion.getAttribute (), ">=", assertion.getValue (), j );
        }
        else if ( assertion.getAssertion () == Assertion.GREATERTHAN )
        {
            result = toSql ( schema, assertion.getAttribute (), ">", assertion.getValue (), j );
        }
        else if ( assertion.getAssertion () == Assertion.LESSEQ )
        {
            result = toSql ( schema, assertion.getAttribute (), "<=", assertion.getValue (), j );
        }
        else if ( assertion.getAssertion () == Assertion.LESSTHAN )
        {
            result = toSql ( schema, assertion.getAttribute (), "<", assertion.getValue (), j );
        }
        else if ( assertion.getAssertion () == Assertion.APPROXIMATE )
        {
            result = toSql ( schema, assertion.getAttribute (), "approximate", assertion.getValue (), j );
        }
        else if ( assertion.getAssertion () == Assertion.SUBSTRING )
        {
            result = toSql ( schema, assertion.getAttribute (), "like", assertion.getValue (), j );
        }
        else if ( assertion.getAssertion () == Assertion.PRESENCE )
        {
            result = toSql ( schema, assertion.getAttribute (), "presence", assertion.getValue (), j );
        }
        else
        {
            throw new NotSupportedException ();
        }
        return result;
    }

    private static SqlCondition toSql ( String schema, String attribute, String op, Object value, AtomicInteger j )
    {
        SqlCondition condition = new SqlCondition ();
        Variant v = toVariant ( value );
        if ( fixedAttributes.contains ( attribute ) || inlinedAttributes.contains ( attribute ) )
        {
            if ( "value".equals ( attribute ) )
            {
                if ( "approximate".equals ( op ) )
                {
                    condition.condition = String.format ( "soundex(E.VALUE_STRING) = soundex(?)", schema );
                    condition.parameters.add ( v.asString ( "" ) );
                }
                else if ( "like".equals ( op ) )
                {
                    condition.condition = String.format ( "lower(E.VALUE_STRING) like lower(?)", schema );
                    condition.parameters.add ( v.asString ( "" ) );
                }
                else if ( "presence".equals ( op ) )
                {
                    condition.condition = String.format ( "(E.VALUE_STRING IS NOT NULL OR E.VALUE_INTEGER IS NOT NULL OR E.VALUE_DOUBLE IS NOT NULL)", schema );
                }
                else if ( v.isInteger () || v.isLong () )
                {
                    condition.condition = String.format ( "E.VALUE_INTEGER " + op + " ?", schema );
                    condition.parameters.add ( v.asLong ( 0L ) );
                }
                else if ( v.isDouble () )
                {
                    condition.condition = String.format ( "E.VALUE_DOUBLE " + op + " ?", schema );
                    condition.parameters.add ( v.asDouble ( 0.0 ) );
                }
                else if ( v.isBoolean () )
                {
                    condition.condition = String.format ( "E.VALUE_INTEGER " + op + " ?", schema );
                    condition.parameters.add ( v.asLong ( 0L ) );
                }
                else if ( v.isNull () )
                {
                    condition.condition = String.format ( "(E.VALUE_STRING IS NULL OR E.VALUE_INTEGER IS NULL OR E.VALUE_DOUBLE IS NULL)", schema );
                    condition.parameters.add ( attribute );
                }
                else
                {
                    condition.condition = String.format ( "E.VALUE_STRING " + op + " ?", schema );
                    condition.parameters.add ( v.asString ( "" ) );
                }
            }
            else
            {
                Object param = null;
                if ( "sourceTimestamp".equals ( attribute ) || "entryTimestamp".equals ( attribute ) )
                {
                    try
                    {
                        param = new Timestamp ( isoDateFormat.parse ( v.asString ( "" ) ).getTime () );
                    }
                    catch ( ParseException e )
                    {
                        param = e.getMessage ();
                    }
                }
                else if ( "priority".equals ( attribute ) )
                {
                    param = v.asInteger ( 0 );
                }
                else
                {
                    param = v.asString ( "" );
                }
                if ( "approximate".equals ( op ) && param instanceof String )
                {
                    condition.condition = String.format ( "soundex(" + getColumn ( attribute ) + ") = soundex(?)", schema );
                    condition.parameters.add ( v.asString ( "" ) );
                }
                else if ( "like".equals ( op ) )
                {
                    if ( v.isString () )
                    {
                        condition.condition = String.format ( "lower(" + getColumn ( attribute ) + ") like lower(?)", schema );
                    }
                    else
                    {
                        condition.condition = String.format ( "lower('' || " + getColumn ( attribute ) + ") like lower('' || ?)", schema );
                    }
                    condition.parameters.add ( (Serializable)param );
                }
                else if ( "presence".equals ( op ) )
                {
                    condition.condition = String.format ( getColumn ( attribute ) + " IS NOT NULL", schema );
                }
                else if ( v.isInteger () || v.isLong () )
                {
                    condition.condition = String.format ( getColumn ( attribute ) + " " + op + " ?", schema );
                    condition.parameters.add ( v.asLong ( 0L ) );
                }
                else if ( v.isDouble () )
                {
                    condition.condition = String.format ( getColumn ( attribute ) + " " + op + " ?", schema );
                    condition.parameters.add ( v.asDouble ( 0.0 ) );
                }
                else if ( v.isBoolean () )
                {
                    condition.condition = String.format ( getColumn ( attribute ) + " " + op + " ?", schema );
                    condition.parameters.add ( v.asLong ( 0L ) );
                }
                else if ( v.isNull () )
                {
                    condition.condition = String.format ( getColumn ( attribute ) + " IS NULL)", schema );
                    condition.parameters.add ( attribute );
                }
                else
                {
                    condition.condition = String.format ( getColumn ( attribute ) + " " + op + " ?", schema );
                    condition.parameters.add ( (Serializable)param );
                }
            }
        }
        else
        {
            int k = j.get ();
            condition.joins.add ( "LEFT JOIN %1$sOPENSCADA_AE_EVENTS_ATTR field_" + k + " ON (E.ID = field_" + k + ".ID AND field_" + k + ".KEY = ?)" );
            condition.joinParameters.add ( attribute );
            if ( "approximate".equals ( op ) )
            {
                condition.condition = String.format ( " soundex(field_" + k + ".VALUE_STRING) = soundex(?)", schema );
                condition.parameters.add ( v.asString ( "" ) );
            }
            else if ( "like".equals ( op ) )
            {
                condition.condition = String.format ( " lower(field_" + k + ".VALUE_STRING) like lower(?)", schema );
                condition.parameters.add ( v.asString ( "" ) );
            }
            else if ( "presence".equals ( op ) )
            {
                condition.condition = String.format ( " (field_" + k + ".VALUE_STRING IS NOT NULL OR field_" + k + ".VALUE_INTEGER IS NOT NULL OR field_" + k + ".VALUE_DOUBLE IS NOT NULL)", schema );
            }
            else if ( v.isInteger () || v.isLong () )
            {
                condition.condition = String.format ( " field_" + k + ".VALUE_INTEGER " + op + " ?", schema );
                condition.parameters.add ( v.asLong ( 0L ) );
            }
            else if ( v.isDouble () )
            {
                condition.condition = String.format ( " field_" + k + ".VALUE_DOUBLE " + op + " ?", schema );
                condition.parameters.add ( v.asDouble ( 0.0 ) );
            }
            else if ( v.isBoolean () )
            {
                condition.condition = String.format ( " field_" + k + ".VALUE_INTEGER " + op + " ?", schema );
                condition.parameters.add ( v.asLong ( 0L ) );
            }
            else if ( v.isNull () )
            {
                condition.condition = String.format ( " (field_" + k + ".VALUE_STRING IS NULL OR field_" + k + ".VALUE_INTEGER IS NULL OR field_" + k + ".VALUE_DOUBLE IS NULL)", schema );
            }
            else
            {
                condition.condition = String.format ( " field_" + k + ".VALUE_STRING " + op + " ?", schema );
                condition.parameters.add ( v.asString ( "" ) );
            }
            j.incrementAndGet ();
        }
        return condition;
    }

    @SuppressWarnings ( "unchecked" )
    private static Variant toVariant ( Object value )
    {
        if ( value instanceof List<?> )
        {
            value = StringHelper.join ( (List<String>)value, "%" ).replaceAll ( "\\?", "_" );
        }
        if ( value instanceof String && ( (String)value ).contains ( "#" ) )
        {
            VariantEditor ed = new VariantEditor ();
            try
            {
                ed.setAsText ( (String)value );
                return (Variant)ed.getValue ();
            }
            catch ( IllegalArgumentException e )
            {
                // pass
            }
        }
        return Variant.valueOf ( value );
    }

    private static String getColumn ( String attribute )
    {
        return fieldToColumn.get ( attribute );
    }

    static SqlCondition toSql ( String schema, FilterExpression expression, AtomicInteger j ) throws NotSupportedException
    {
        final SqlCondition result = new SqlCondition ();
        result.condition = "(";
        int i = 0;
        for ( final Filter term : expression.getFilterSet () )
        {
            if ( i > 0 )
            {
                if ( expression.getOperator () == Operator.AND )
                {
                    result.condition += " AND ";
                }
                else if ( expression.getOperator () == Operator.OR )
                {
                    result.condition += " OR ";
                }
            }
            if ( term.isExpression () )
            {
                final SqlCondition r = toSql ( schema, (FilterExpression)term, j );
                result.condition += r.condition;
                result.joins.addAll ( r.joins );
                result.joinParameters.addAll ( r.joinParameters );
                result.parameters.addAll ( r.parameters );
            }
            else if ( term.isAssertion () )
            {
                final SqlCondition r = toSql ( schema, (FilterAssertion)term, j );
                result.condition += r.condition;
                result.joins.addAll ( r.joins );
                result.joinParameters.addAll ( r.joinParameters );
                result.parameters.addAll ( r.parameters );
            }
            i++;
        }
        if ( expression.getOperator () == Operator.NOT )
        {
            result.condition = "NOT " + result.condition;
        }
        result.condition += ")";
        return result;
    }
}
