/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import org.openscada.ae.Event.EventBuilder;
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
import org.openscada.da.master.MasterItem;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Interner;

public class ListAlarmMonitor extends AbstractVariantMonitor implements DataItemMonitor
{

    private final static Logger logger = LoggerFactory.getLogger ( ListAlarmMonitor.class );

    public static final String FACTORY_ID = "ae.monitor.da.listAlarm"; //$NON-NLS-1$

    private Collection<Variant> referenceList;

    private boolean listIsAlarm;

    private final int defaultPriority;

    public ListAlarmMonitor ( final BundleContext context, final Executor executor, final Interner<String> stringInterner, final ObjectPoolTracker<MasterItem> poolTracker, final EventProcessor eventProcessor, final String id, final int defaultPriority )
    {
        super ( context, executor, stringInterner, poolTracker, eventProcessor, id, id, "VALUE" ); //$NON-NLS-1$
        this.defaultPriority = defaultPriority;
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
    protected int getDefaultPriority ()
    {
        return this.defaultPriority;
    }

    @Override
    public void update ( final UserInformation userInformation, final Map<String, String> properties ) throws Exception
    {
        super.update ( userInformation, properties );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( properties );

        // parameter - "referenceList"
        final Collection<Variant> newReferenceList = parseValues ( cfg.getString ( "referenceList", "" ), cfg.getString ( "splitPattern", "[, \t\n\r]+" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        if ( isDifferent ( this.referenceList, newReferenceList ) )
        {
            final EventBuilder builder = EventHelper.newConfigurationEvent ( userInformation, getId (), Messages.getString ( "ListAlarmMonitor.message.changeReferenceList" ), Variant.valueOf ( newReferenceList ), new Date () ); //$NON-NLS-1$
            injectEventAttributes ( builder );
            publishEvent ( builder );
            this.referenceList = newReferenceList;
        }

        // parameter - "listIsAlarm"
        final boolean listIsAlarm = cfg.getBoolean ( "listIsAlarm", true ); //$NON-NLS-1$
        if ( isDifferent ( this.listIsAlarm, listIsAlarm ) )
        {
            final EventBuilder builder = EventHelper.newConfigurationEvent ( userInformation, getId (), Messages.getString ( "ListAlarmMonitor.message.alarmItems" ), Variant.valueOf ( listIsAlarm ), new Date () ); //$NON-NLS-1$
            injectEventAttributes ( builder );
            publishEvent ( builder );

            this.listIsAlarm = listIsAlarm;
        }

        reprocess ();
    }

    protected Collection<Variant> parseValues ( final String data, final String splitPatternString )
    {
        if ( data == null )
        {
            return Collections.emptyList ();
        }

        final Pattern splitPattern = Pattern.compile ( splitPatternString );

        final Collection<Variant> result = new LinkedList<Variant> ();
        final String toks[] = splitPattern.split ( data );

        for ( final String item : toks )
        {
            final Variant value = VariantEditor.toVariant ( item );
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

        builder.setAttribute ( this.prefix + ".referenceList", Variant.valueOf ( this.referenceList ) ); //$NON-NLS-1$
        builder.setAttribute ( this.prefix + ".listIsAlarm", Variant.valueOf ( this.listIsAlarm ) ); //$NON-NLS-1$
    }

    @Override
    protected void handleConfigUpdate ( final Map<String, String> configUpdate, final Map<String, Variant> attributes, final WriteAttributeResults result )
    {
        super.handleConfigUpdate ( configUpdate, attributes, result );

        final Variant reference = attributes.get ( this.prefix + ".referenceList" ); //$NON-NLS-1$
        if ( reference != null )
        {
            configUpdate.put ( "referenceList", reference.asString ( "" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            result.put ( this.prefix + ".referenceList", WriteAttributeResult.OK ); //$NON-NLS-1$
        }

        final Variant listIsAlarm = attributes.get ( this.prefix + ".listIsAlarm" ); //$NON-NLS-1$
        if ( listIsAlarm != null )
        {
            final Boolean value = listIsAlarm.asBoolean ( null );
            if ( value != null )
            {
                configUpdate.put ( "listIsAlarm", value ? "true" : "false" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                result.put ( this.prefix + ".listIsAlarm", WriteAttributeResult.OK ); //$NON-NLS-1$
            }
        }
    }

    @Override
    protected void update ( final Builder builder )
    {
        logger.debug ( "Handle data update: {}", builder ); //$NON-NLS-1$

        if ( this.value == null || this.value.isNull () || this.timestamp == null || this.referenceList == null )
        {
            setUnsafe ();
        }
        else if ( isOk ( this.value ) )
        {
            setOk ( Variant.valueOf ( this.value ), this.timestamp );
        }
        else
        {
            setFailure ( Variant.valueOf ( this.value ), this.timestamp );
        }
    }

    protected boolean isOk ( final Variant value )
    {
        if ( this.referenceList.contains ( value ) )
        {
            return !this.listIsAlarm;
        }
        else
        {
            return this.listIsAlarm;
        }
    }

}
