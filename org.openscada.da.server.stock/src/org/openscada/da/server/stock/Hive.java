/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.da.server.stock;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.ValidationStrategy;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.da.server.common.configuration.Configurator;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.stock.business.YahooStockQuoteService;
import org.openscada.da.server.stock.items.StockQuoteItem;
import org.openscada.da.server.stock.items.UpdateManager;
import org.openscada.utils.collection.MapBuilder;

public class Hive extends HiveCommon
{

    private static final int UPDATE_PERIOD = 30 * 1000;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor ();

    private final FolderCommon symbolsFolder;

    private final UpdateManager updateManager;

    public Hive () throws ConfigurationError, IOException
    {
        this ( null );
    }

    public Hive ( final Configurator configurator ) throws ConfigurationError, IOException
    {
        super ();

        setValidatonStrategy ( ValidationStrategy.GRANT_ALL );

        // create root folder
        final FolderCommon rootFolder = new FolderCommon ();
        setRootFolder ( rootFolder );

        // create and register test folder
        this.symbolsFolder = new FolderCommon ();
        rootFolder.add ( "symbols", this.symbolsFolder, new MapBuilder<String, Variant> ().put ( "description", Variant.valueOf ( "This folder contains the items by stock symbol" ) ).getMap () );
        if ( configurator != null )
        {
            configurator.configure ( this );
        }

        this.updateManager = new UpdateManager ();
        this.updateManager.setStockQuoteService ( new YahooStockQuoteService () );

        this.scheduler.scheduleAtFixedRate ( new Runnable () {

            @Override
            public void run ()
            {
                Hive.this.updateManager.update ();
            }
        }, UPDATE_PERIOD, UPDATE_PERIOD, TimeUnit.MILLISECONDS );

        addSymbol ( "RHT" );
        addSymbol ( "YHOO" );
    }

    @Override
    public String getHiveId ()
    {
        return "org.openscada.da.server.stock";
    }

    @Override
    public void stop () throws Exception
    {
        this.scheduler.shutdown ();
        super.stop ();
    }

    public void addSymbol ( final String symbol )
    {
        final StockQuoteItem newItem = new StockQuoteItem ( symbol, this.updateManager );
        registerItem ( newItem );
        this.symbolsFolder.add ( symbol, newItem, new MapBuilder<String, Variant> ().getMap () );
    }

}
