package org.openscada.ae.server.common.event.pool.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.regex.Pattern;

import org.openscada.ae.Event;
import org.openscada.ae.event.FilterUtils;
import org.openscada.ae.server.common.event.EventMatcher;
import org.openscada.utils.filter.Assertion;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.filter.FilterAssertion;
import org.openscada.utils.filter.FilterExpression;
import org.openscada.utils.filter.FilterParser;
import org.openscada.utils.filter.Operator;
import org.openscada.utils.lang.Apply;
import org.openscada.utils.str.StringHelper;

;

public class EventMatcherImpl implements EventMatcher
{
    public static enum HandleMissing
    {
        MISSING_EVALUATES_TO_FALSE,
        MISSING_EVALUATES_TO_TRUE;
    }

    private final Filter filter;

    private final DateFormat isoDateFormat = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss.S" );

    private HandleMissing handleMissing = HandleMissing.MISSING_EVALUATES_TO_FALSE;

    public HandleMissing getHandleMissing ()
    {
        return this.handleMissing;
    }

    public void setHandleMissing ( final HandleMissing handleMissing )
    {
        this.handleMissing = handleMissing;
    }

    public EventMatcherImpl ( final String filter )
    {
        this.filter = new FilterParser ( filter ).getFilter ();
        FilterUtils.toVariant ( this.filter );
    }

    public boolean matches ( final Event event )
    {
        return matches ( this.filter, event );
    }

    private boolean matches ( final Filter filter, final Event event )
    {
        if ( filter.isAssertion () )
        {
            return matchAssertion ( (FilterAssertion)filter, event );
        }
        else if ( filter.isExpression () )
        {
            return matchExpression ( (FilterExpression)filter, event );
        }
        else if ( filter.isEmpty () )
        {
            return true;
        }
        return false;
    }

    private boolean matchExpression ( final FilterExpression expression, final Event event )
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

    private boolean matchAssertion ( final FilterAssertion assertion, final Event event )
    {
        // first find out if attribute exists at all
        final boolean hasAttribute;
        if ( "id".equals ( assertion.getAttribute () ) )
        {
            hasAttribute = true;
        }
        else if ( "sourceTimestamp".equals ( assertion.getAttribute () ) )
        {
            hasAttribute = true;
        }
        else if ( "entryTimestamp".equals ( assertion.getAttribute () ) )
        {
            hasAttribute = true;
        }
        else
        {
            hasAttribute = event.getAttributes ().keySet ().contains ( assertion.getAttribute () );
        }
        // before we do the handling of missing attributes we handle presence
        if ( assertion.getAssertion () == Assertion.PRESENCE )
        {
            return hasAttribute;
        }
        // missing attributes are handled according to policy
        if ( !hasAttribute && this.handleMissing == HandleMissing.MISSING_EVALUATES_TO_FALSE )
        {
            return false;
        }
        else if ( !hasAttribute && this.handleMissing == HandleMissing.MISSING_EVALUATES_TO_TRUE )
        {
            return true;
        }
        // handle all other assertions
        final Object value = getAttribute ( event, assertion.getAttribute () );
        if ( assertion.getAssertion () == Assertion.EQUALITY )
        {
            return matchEquals ( value, assertion.getValue () );
        }
        else if ( assertion.getAssertion () == Assertion.GREATERTHAN )
        {
            return matchGreaterThan ( value, assertion.getValue () );
        }
        else if ( assertion.getAssertion () == Assertion.GREATEREQ )
        {
            return matchGreaterThan ( value, assertion.getValue () ) || matchEquals ( value, assertion.getValue () );
        }
        else if ( assertion.getAssertion () == Assertion.LESSTHAN )
        {
            return matchLessThan ( value, assertion.getValue () );
        }
        else if ( assertion.getAssertion () == Assertion.LESSEQ )
        {
            return matchLessThan ( value, assertion.getValue () ) || matchEquals ( value, assertion.getValue () );
        }
        else if ( assertion.getAssertion () == Assertion.SUBSTRING )
        {
            return matchSubstring ( value, assertion.getValue () );
        }
        else if ( assertion.getAssertion () == Assertion.APPROXIMATE )
        {
            return matchApproximate ( value, assertion.getValue () );
        }
        return false;
    }

    private boolean matchApproximate ( final Object value1, final Object value2 )
    {
        throw new IllegalArgumentException ( "approximate match is not implemented yet" );
    }

    @SuppressWarnings ( "unchecked" )
    private boolean matchSubstring ( final Object value1, final Object value2 )
    {
        if ( value2 == null )
        {
            return false;
        }
        final String toCompare = StringHelper.join ( (Collection<String>)value2, ".*", new Apply<String> () {
            public String apply ( final String parameter )
            {
                return Pattern.quote ( parameter );
            }
        } );
        return String.valueOf ( value1 ).matches ( toCompare );
    }

    @SuppressWarnings ( "unchecked" )
    private boolean matchLessThan ( final Object value1, final Object value2 )
    {
        if ( value1 == value2 )
        {
            return false;
        }
        if ( value1 == null && value2 != null || value1 != null && value2 == null )
        {
            return false;
        }
        if ( value1 instanceof Comparable<?> )
        {
            return ( (Comparable)value1 ).compareTo ( value2 ) < 0;
        }
        else
        {
            return false;
        }
    }

    @SuppressWarnings ( "unchecked" )
    private boolean matchGreaterThan ( final Object value1, final Object value2 )
    {
        if ( value1 == value2 )
        {
            return false;
        }
        if ( value1 == null && value2 != null || value1 != null && value2 == null )
        {
            return false;
        }
        if ( value1 instanceof Comparable<?> )
        {
            return ( (Comparable)value1 ).compareTo ( value2 ) > 0;
        }
        else
        {
            return false;
        }
    }

    private boolean matchEquals ( final Object value1, final Object value2 )
    {
        if ( value1 == value2 )
        {
            return true;
        }
        if ( value1 == null && value2 != null || value1 != null && value2 == null )
        {
            return false;
        }
        return value1.equals ( value2 );
    }

    private Object getAttribute ( final Event event, final String attribute )
    {
        if ( "id".equals ( attribute ) )
        {
            return event.getId ();
        }
        else if ( "sourceTimestamp".equals ( attribute ) )
        {
            return event.getSourceTimestamp ();
        }
        else if ( "entryTimestamp".equals ( attribute ) )
        {
            return event.getEntryTimestamp ();
        }
        else
        {
            return event.getAttributes ().get ( attribute );
        }
    }
}
