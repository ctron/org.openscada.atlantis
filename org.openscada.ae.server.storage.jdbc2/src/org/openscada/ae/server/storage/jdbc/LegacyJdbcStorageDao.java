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

package org.openscada.ae.server.storage.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.Event.Fields;
import org.openscada.core.Variant;
import org.openscada.core.VariantEditor;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.str.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LegacyJdbcStorageDao extends BaseStorageDao
{
    private static final Logger logger = LoggerFactory.getLogger ( LegacyJdbcStorageDao.class );

    private final String insertEventSql = "INSERT INTO %sOPENSCADA_AE_EVENTS " //
            + "(ID, SOURCE_TIMESTAMP, ENTRY_TIMESTAMP, MONITOR_TYPE, EVENT_TYPE, " //
            + "VALUE_TYPE, VALUE_STRING, VALUE_INTEGER, VALUE_DOUBLE, MESSAGE, " //
            + "MESSAGE_CODE, PRIORITY, SOURCE, ACTOR_NAME, ACTOR_TYPE)" //
            + " VALUES " //
            + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final String insertAttributesSql = "INSERT INTO %sOPENSCADA_AE_EVENTS_ATTR " //
            + "(ID, KEY, VARIANT_TYPE, VALUE_STRING, VALUE_INTEGER, VALUE_DOUBLE)" //
            + " VALUES " //
            + "(?, ?, ?, ?, ?, ?)";

    private final String deleteAttributesSql = "DELETE FROM %sOPENSCADA_AE_EVENTS_ATTR " //
            + "WHERE ID = ? AND KEY = ?";

    private final String selectEventSql = "SELECT E.ID, 'default' AS INSTANCE_ID, E.SOURCE_TIMESTAMP, E.ENTRY_TIMESTAMP, E.MONITOR_TYPE, E.EVENT_TYPE, " //
            + "E.VALUE_TYPE, E.VALUE_STRING, E.VALUE_INTEGER, E.VALUE_DOUBLE, E.MESSAGE, " //
            + "E.MESSAGE_CODE, E.PRIORITY, E.SOURCE, E.ACTOR_NAME, E.ACTOR_TYPE, " //
            + "A.KEY, A.VARIANT_TYPE, A.VALUE_STRING, A.VALUE_INTEGER, A.VALUE_DOUBLE " //
            + "FROM %1$sOPENSCADA_AE_EVENTS E LEFT JOIN %1$sOPENSCADA_AE_EVENTS_ATTR A ON (A.ID = E.ID) ";

    private final String whereSql = " WHERE 'default' = ? ";

    private final String defaultOrder = " ORDER BY E.SOURCE_TIMESTAMP DESC, E.ENTRY_TIMESTAMP DESC";

    @Override
    public void storeEvent ( final Event event ) throws Exception
    {
        final Connection con = createConnection ();
        Statement stm1 = null;
        Statement stm2 = null;
        {
            final PreparedStatement stm = con.prepareStatement ( String.format ( this.insertEventSql, this.getSchema () ) );
            stm.setString ( 1, event.getId ().toString () );
            stm.setTimestamp ( 2, new java.sql.Timestamp ( event.getSourceTimestamp ().getTime () ) );
            stm.setTimestamp ( 3, new java.sql.Timestamp ( event.getEntryTimestamp ().getTime () ) );
            stm.setString ( 4, clip ( 32, Variant.valueOf ( event.getField ( Fields.MONITOR_TYPE ) ).asString ( "" ) ) );
            stm.setString ( 5, clip ( 32, Variant.valueOf ( event.getField ( Fields.EVENT_TYPE ) ).asString ( "" ) ) );
            stm.setString ( 6, clip ( 32, Variant.valueOf ( event.getField ( Fields.VALUE ) ).getType ().name () ) );
            stm.setString ( 7, clip ( this.getMaxLength (), Variant.valueOf ( event.getField ( Fields.VALUE ) ).asString ( "" ) ) );
            final Long longValue = Variant.valueOf ( event.getField ( Fields.VALUE ) ).asLong ( null );
            if ( longValue == null )
            {
                stm.setNull ( 8, Types.BIGINT );
            }
            else
            {
                stm.setLong ( 8, longValue );
            }
            final Double doubleValue = Variant.valueOf ( event.getField ( Fields.VALUE ) ).asDouble ( null );
            if ( doubleValue == null )
            {
                stm.setNull ( 9, Types.DOUBLE );
            }
            else
            {
                stm.setDouble ( 9, longValue );
            }
            stm.setString ( 10, clip ( this.getMaxLength (), Variant.valueOf ( event.getField ( Fields.MESSAGE ) ).asString ( "" ) ) );
            stm.setString ( 11, clip ( 255, Variant.valueOf ( event.getField ( Fields.MESSAGE_CODE ) ).asString ( "" ) ) );
            stm.setInt ( 12, Variant.valueOf ( event.getField ( Fields.PRIORITY ) ).asInteger ( 50 ) );
            stm.setString ( 13, clip ( 255, Variant.valueOf ( event.getField ( Fields.SOURCE ) ).asString ( "" ) ) );
            stm.setString ( 14, clip ( 128, Variant.valueOf ( event.getField ( Fields.ACTOR_NAME ) ).asString ( "" ) ) );
            stm.setString ( 15, clip ( 32, Variant.valueOf ( event.getField ( Fields.ACTOR_TYPE ) ).asString ( "" ) ) );
            stm.addBatch ();
            stm.executeBatch ();
            stm1 = stm;
        }
        {
            final PreparedStatement stm = con.prepareStatement ( String.format ( this.insertAttributesSql, this.getSchema () ) );
            boolean hasAttr = false;
            for ( final String attr : event.getAttributes ().keySet () )
            {
                if ( SqlConverter.inlinedAttributes.contains ( attr ) )
                {
                    continue;
                }
                stm.setString ( 1, event.getId ().toString () );
                stm.setString ( 2, attr );
                stm.setString ( 3, clip ( 32, event.getAttributes ().get ( attr ).getType ().name () ) );
                stm.setString ( 4, clip ( this.getMaxLength (), event.getAttributes ().get ( attr ).asString ( "" ) ) );
                final Long longValue = Variant.valueOf ( event.getAttributes ().get ( attr ) ).asLong ( null );
                if ( longValue == null )
                {
                    stm.setNull ( 5, Types.BIGINT );
                }
                else
                {
                    stm.setLong ( 5, longValue );
                }
                final Double doubleValue = Variant.valueOf ( event.getAttributes ().get ( attr ) ).asDouble ( null );
                if ( doubleValue == null )
                {
                    stm.setNull ( 6, Types.DOUBLE );
                }
                else
                {
                    stm.setDouble ( 6, doubleValue );
                }
                stm.addBatch ();
                hasAttr = true;
            }
            if ( hasAttr )
            {
                stm.executeBatch ();
            }
            stm2 = stm;
        }
        con.commit ();
        closeStatement ( stm1 );
        closeStatement ( stm2 );
        closeConnection ( con );
    }

    @Override
    public Event loadEvent ( final UUID id ) throws SQLException
    {
        final Connection con = createConnection ();
        final String sql = this.selectEventSql + this.whereSql + " AND E.ID = ? " + this.defaultOrder;
        final PreparedStatement stm = con.prepareStatement ( String.format ( sql, this.getSchema (), "" ), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
        stm.setString ( 1, "default" );
        stm.setString ( 2, id.toString () );
        final ResultSet result = stm.executeQuery ();
        final List<Event> events = new ArrayList<Event> ();
        final boolean hasMore = toEventList ( result, events, true, 1 );
        closeStatement ( stm );
        closeConnection ( con );
        if ( hasMore )
        {
            logger.warn ( "more distinct records found for id {}, this shouldn't happen at all", id );
        }
        if ( events != null && !events.isEmpty () )
        {
            return events.get ( 0 );
        }
        return null;
    }

    @Override
    public ResultSet queryEvents ( final Filter filter ) throws SQLException, NotSupportedException
    {
        final Connection con = createConnection ();
        final SqlCondition condition = SqlConverter.toSql ( this.getSchema (), filter );
        String sql = this.selectEventSql + StringHelper.join ( condition.joins, " " ) + this.whereSql;
        sql += condition.condition;
        sql += this.defaultOrder;
        final String querySql = String.format ( sql, this.getSchema () );
        logger.debug ( "executing query: " + querySql + " with parameters " + condition.joinParameters + " / " + condition.parameters );
        final PreparedStatement stm = con.prepareStatement ( querySql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
        int i = 0;
        for ( final String parameter : condition.joinParameters )
        {
            i += 1;
            stm.setString ( i, parameter );
        }
        i += 1;
        stm.setString ( i, "default" );
        for ( final Serializable parameter : condition.parameters )
        {
            i += 1;
            stm.setObject ( i, parameter );
        }
        final ResultSet rs = stm.executeQuery ();
        logger.debug ( "query completed, returning resultset" );
        return rs;
    }

    @Override
    public boolean toEventList ( final ResultSet rs, final Collection<Event> events, final boolean isBeforeFirst, final long count ) throws SQLException
    {
        UUID lastId = null;
        EventBuilder eb = Event.create ();
        boolean hasMore = true;
        long l = 0;
        while ( true )
        {
            if ( isBeforeFirst )
            {
                hasMore = rs.next ();
                if ( !hasMore )
                {
                    break;
                }
            }
            final UUID id = UUID.fromString ( rs.getString ( 1 ) );
            if ( lastId != null && !id.equals ( lastId ) )
            {
                events.add ( eb.build () );
                l += 1;
                if ( l == count )
                {
                    break;
                }
                lastId = id;
                eb = Event.create ();
            }
            else if ( lastId == null )
            {
                lastId = id;
            }
            // base event
            eb.id ( id );
            final Date sourceTimestamp = new Date ( rs.getTimestamp ( 3 ).getTime () );
            final Date entryTimestamp = new Date ( rs.getTimestamp ( 4 ).getTime () );
            final String monitorType = rs.getString ( 5 );
            final String eventType = rs.getString ( 6 );
            String valueType = rs.getString ( 7 );
            String valueString = rs.getString ( 8 );
            final String message = rs.getString ( 11 );
            final String messageCode = rs.getString ( 12 );
            final Integer priority = rs.getInt ( 13 );
            final String source = rs.getString ( 14 );
            final String actor = rs.getString ( 15 );
            final String actorType = rs.getString ( 16 );

            eb.sourceTimestamp ( sourceTimestamp );
            eb.entryTimestamp ( entryTimestamp );
            eb.attribute ( Fields.MONITOR_TYPE, monitorType );
            eb.attribute ( Fields.EVENT_TYPE, eventType );
            if ( valueType != null && valueString != null )
            {
                final VariantEditor ed = new VariantEditor ();
                ed.setAsText ( valueType + "#" + valueString );
                eb.attribute ( Fields.VALUE, ed.getValue () );
            }
            eb.attribute ( Fields.MESSAGE, message );
            eb.attribute ( Fields.MESSAGE_CODE, messageCode );
            eb.attribute ( Fields.PRIORITY, priority );
            eb.attribute ( Fields.SOURCE, source );
            eb.attribute ( Fields.ACTOR_NAME, actor );
            eb.attribute ( Fields.ACTOR_TYPE, actorType );

            // other attributes
            final String field = rs.getString ( 17 );
            valueType = rs.getString ( 18 );
            valueString = rs.getString ( 19 );
            if ( field != null )
            {
                if ( valueType != null && valueString != null )
                {
                    final VariantEditor ed = new VariantEditor ();
                    ed.setAsText ( valueType + "#" + valueString );
                    eb.attribute ( field, ed.getValue () );
                }
                else
                {
                    eb.attribute ( field, Variant.NULL );
                }
            }

            hasMore = rs.next ();
            if ( !hasMore )
            {
                events.add ( eb.build () );
                break;
            }
        }
        return hasMore;
    }

    @Override
    protected String getDeleteAttributesSql ()
    {
        return deleteAttributesSql;
    }

    @Override
    protected String getInsertAttributesSql ()
    {
        return insertAttributesSql;
    }
}
