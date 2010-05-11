/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.ae.monitor.dataitem.monitor.internal.list;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.common.EventHelper;
import org.openscada.ae.monitor.dataitem.AbstractVariantMonitor;
import org.openscada.ae.monitor.dataitem.DataItemMonitor;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.core.VariantEditor;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListAlarmMonitor extends AbstractVariantMonitor implements DataItemMonitor
{

    private final static Logger logger = LoggerFactory.getLogger ( ListAlarmMonitor.class );

    public static final String FACTORY_ID = "ae.monitor.da.listAlarm";

    private final static Pattern splitPattern = Pattern.compile ( "[, \t\n\r]+" );

    private Collection<Variant> referenceList;

    private boolean listIsAlarm;

    protected Date timestamp;

    public ListAlarmMonitor ( final BundleContext context, final Executor executor, final ObjectPoolTracker poolTracker, final EventProcessor eventProcessor, final String id )
    {
        super ( context, executor, poolTracker, eventProcessor, id, id, "VALUE" );
    }

    @Override
    protected String getFactoryId ()
    {
        return FACTORY_ID;
    }

    @Override
    protected String getConfigurationId ()
    {
        return getId ();
    }

    @Override
    public void update ( final Map<String, String> properties ) throws Exception
    {
        super.update ( properties );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( properties );

        // parameter - "referenceList"
        final Collection<Variant> newReferenceList = parseValues ( cfg.getString ( "referenceList", "" ) );
        if ( isDifferent ( this.referenceList, newReferenceList ) )
        {
            publishEvent ( EventHelper.newConfigurationEvent ( this.getId (), "Change reference value list", Variant.valueOf ( newReferenceList ), new Date () ) );
            this.referenceList = newReferenceList;
        }

        // parameter - "listIsAlarm"
        final boolean listIsAlarm = cfg.getBoolean ( "listIsAlarm", true );
        if ( isDifferent ( this.listIsAlarm, listIsAlarm ) )
        {
            publishEvent ( EventHelper.newConfigurationEvent ( this.getId (), "Items in reference list are alarm", Variant.valueOf ( listIsAlarm ), new Date () ) );
            this.listIsAlarm = listIsAlarm;
        }

        reprocess ();
    }

    protected Collection<Variant> parseValues ( final String data )
    {
        if ( data == null )
        {
            return Collections.emptyList ();
        }

        final Collection<Variant> result = new LinkedList<Variant> ();
        final String toks[] = splitPattern.split ( data );

        for ( final String item : toks )
        {
            final VariantEditor ve = new VariantEditor ();
            ve.setAsText ( item );
            final Variant value = (Variant)ve.getValue ();
            if ( value != null )
            {
                result.add ( value );
            }
        }

        return result;
    }

    @Override
    protected void injectAttributes ( final Builder builder )
    {
        super.injectAttributes ( builder );

        builder.setAttribute ( this.prefix + ".referenceList", Variant.valueOf ( this.referenceList ) );
        builder.setAttribute ( this.prefix + ".listIsAlarm", Variant.valueOf ( this.listIsAlarm ) );
    }

    @Override
    protected void handleConfigUpdate ( final Map<String, String> configUpdate, final Map<String, Variant> attributes, final WriteAttributeResults result )
    {
        super.handleConfigUpdate ( configUpdate, attributes, result );

        final Variant reference = attributes.get ( this.prefix + ".referenceList" );
        if ( reference != null )
        {
            configUpdate.put ( "referenceList", reference.asString ( "" ) );
            result.put ( this.prefix + ".referenceList", WriteAttributeResult.OK );
        }

        final Variant listIsAlarm = attributes.get ( this.prefix + ".listIsAlarm" );
        if ( listIsAlarm != null )
        {
            final Boolean value = listIsAlarm.asBoolean ( null );
            if ( value != null )
            {
                configUpdate.put ( "listIsAlarm", value ? "true" : "false" );
                result.put ( this.prefix + ".listIsAlarm", WriteAttributeResult.OK );
            }
        }
    }

    @Override
    protected void update ( final Builder builder )
    {
        logger.debug ( "Handle data update: {}", builder );

        if ( this.value == null || this.value.isNull () || this.timestamp == null || this.referenceList == null )
        {
            setUnsafe ();
        }
        else if ( isAlarm ( this.value ) )
        {
            setOk ( new Variant ( this.value ), this.timestamp );
        }
        else
        {
            setFailure ( new Variant ( this.value ), this.timestamp );
        }
    }

    protected boolean isAlarm ( final Variant value )
    {
        if ( this.referenceList.contains ( value ) )
        {
            return this.listIsAlarm;
        }
        else
        {
            return !this.listIsAlarm;
        }
    }

}
