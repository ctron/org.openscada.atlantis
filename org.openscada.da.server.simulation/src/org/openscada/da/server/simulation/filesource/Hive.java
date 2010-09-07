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

package org.openscada.da.server.simulation.filesource;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.log4j.Logger;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.simulation.configuration.RootDocument;
import org.w3c.dom.Node;

public class Hive extends HiveCommon
{
    private static Logger logger = Logger.getLogger ( Hive.class );

    private FolderItemFactory factory;

    private int startupDelay = 2000;

    private String name = "Simulator";

    private final FolderCommon rootFolder = new FolderCommon ();

    private final ScriptEngine scriptEngine;

    // constructors

    public Hive () throws Exception
    {
        this ( new XMLConfigurator ( RootDocument.Factory.parse ( new File ( "configuration.xml" ) ) ) );
    }

    public Hive ( final XMLConfigurator configurator ) throws Exception
    {
        setRootFolder ( this.rootFolder );
        // init script engine
        this.scriptEngine = new ScriptEngineManager ().getEngineByName ( "javascript" );
        getScriptEngine ().put ( "logger", logger );
        final InputStream predefinedFunctions = ClassLoader.getSystemResourceAsStream ( "org/openscada/da/server/simulation/filesource/scripts/predefined.js" );
        this.scriptEngine.eval ( new InputStreamReader ( predefinedFunctions ) );
        configurator.configure ( this );
        try
        {
            Thread.sleep ( this.startupDelay );
        }
        catch ( final InterruptedException e )
        {
            throw new RuntimeException ( e );
        }
        getScriptEngine ().eval ( "startSimulation();" );
    }

    public Hive ( final Node node ) throws Exception
    {
        this ( new XMLConfigurator ( RootDocument.Factory.parse ( node ) ) );
    }

    // properties
    public FolderItemFactory getFactory ()
    {
        return this.factory;
    }

    @Override
    public FolderCommon getRootFolder ()
    {
        return this.rootFolder;
    }

    public ScriptEngine getScriptEngine ()
    {
        return this.scriptEngine;
    }

    public void setName ( final String name )
    {
        this.name = name;
    }

    public String getName ()
    {
        return this.name;
    }

    public int getStartupDelay ()
    {
        return this.startupDelay;
    }

    public void setStartupDelay ( final int startupDelay )
    {
        this.startupDelay = startupDelay;
    }

    public void setFactory ( final FolderItemFactory factory )
    {
        this.factory = factory;
    }
}
