/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.stock;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.xmlbeans.XmlException;
import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.ValidationStrategy;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.da.server.common.configuration.Configurator;
import org.openscada.da.server.common.configuration.xml.XMLConfigurator;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.stock.business.YahooStockQuoteService;
import org.openscada.da.server.stock.items.StockQuoteItem;
import org.openscada.da.server.stock.items.UpdateManager;
import org.openscada.utils.collection.MapBuilder;

public class Hive extends HiveCommon
{

    private static final int UPDATE_PERIOD = 30 * 1000;

    private final ScheduledExecutorService _scheduler = Executors.newSingleThreadScheduledExecutor ();

    private FolderCommon _symbolsFolder = null;

    private UpdateManager _updateManager = null;

    public Hive () throws ConfigurationError, IOException, XmlException
    {
        this ( null );
    }

    public Hive ( final Configurator configurator ) throws ConfigurationError, IOException, XmlException
    {
        super ();

        setValidatonStrategy ( ValidationStrategy.GRANT_ALL );

        // create root folder
        final FolderCommon rootFolder = new FolderCommon ();
        setRootFolder ( rootFolder );

        // create and register test folder
        this._symbolsFolder = new FolderCommon ();
        rootFolder.add ( "symbols", this._symbolsFolder, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "This folder contains the items by stock symbol" ) ).getMap () );
        if ( configurator == null )
        {
            xmlConfigure ();
        }
        else
        {
            configurator.configure ( this );
        }

        this._updateManager = new UpdateManager ();
        this._updateManager.setStockQuoteService ( new YahooStockQuoteService () );

        this._scheduler.scheduleAtFixedRate ( new Runnable () {

            public void run ()
            {
                Hive.this._updateManager.update ();
            }
        }, UPDATE_PERIOD, UPDATE_PERIOD, TimeUnit.MILLISECONDS );

        addSymbol ( "RHT" );
        addSymbol ( "YHOO" );
    }

    @Override
    public void stop () throws Exception
    {
        this._scheduler.shutdown ();
        super.stop ();
    }

    private void xmlConfigure () throws ConfigurationError, IOException, XmlException
    {
        final String configurationFile = System.getProperty ( "openscada.da.hive.configuration" );
        if ( configurationFile != null )
        {
            final File file = new File ( configurationFile );
            xmlConfigure ( file );
        }
    }

    private void xmlConfigure ( final File file ) throws ConfigurationError, XmlException, IOException
    {
        new XMLConfigurator ( file ).configure ( this );
    }

    public void addSymbol ( final String symbol )
    {
        final StockQuoteItem newItem = new StockQuoteItem ( symbol, this._updateManager );
        registerItem ( newItem );
        this._symbolsFolder.add ( symbol, newItem, new MapBuilder<String, Variant> ().getMap () );
    }

}
