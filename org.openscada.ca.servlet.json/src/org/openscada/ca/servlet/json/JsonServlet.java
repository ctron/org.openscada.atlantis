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

package org.openscada.ca.servlet.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.Factory;
import org.openscada.ca.data.DiffEntry;
import org.openscada.ca.data.Operation;
import org.openscada.sec.UserInformation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class JsonServlet extends HttpServlet
{
    private class FactorySerializer implements JsonSerializer<Factory>
    {
        @Override
        public JsonElement serialize ( final Factory factory, final Type typeOfFactory, final JsonSerializationContext context )
        {
            final JsonObject obj = new JsonObject ();
            obj.addProperty ( "id", factory.getId () );
            obj.addProperty ( "description", factory.getDescription () );
            obj.addProperty ( "state", factory.getState ().toString () );
            return obj;
        }
    }

    private class DiffEntrySerializer implements JsonSerializer<DiffEntry>, JsonDeserializer<DiffEntry>
    {
        @Override
        public JsonElement serialize ( final DiffEntry config, final Type typeOfDiffEntry, final JsonSerializationContext context )
        {
            final JsonObject obj = new JsonObject ();
            obj.addProperty ( "factoryId", config.getFactoryId () );
            obj.addProperty ( "configurationId", config.getConfigurationId () );
            obj.addProperty ( "operation", config.getOperation ().toString () );

            {
                final JsonObject map = new JsonObject ();
                for ( final Entry<?, ?> entry : config.getAddedOrUpdatedData ().entrySet () )
                {
                    map.addProperty ( String.valueOf ( entry.getKey () ), String.valueOf ( entry.getValue () ) );
                }
                obj.add ( "addedOrUpdatedData", map );
            }
            {
                final JsonArray array = new JsonArray ();
                for ( final String removed : config.getRemovedData () )
                {
                    array.add ( context.serialize ( removed ) );
                }
                obj.add ( "removedData", array );
            }

            return obj;
        }

        @Override
        public DiffEntry deserialize ( final JsonElement element, final Type type, final JsonDeserializationContext context ) throws JsonParseException
        {
            final String factoryId = element.getAsJsonObject ().get ( "factoryId" ).getAsString ();
            final String configurationId = element.getAsJsonObject ().get ( "configurationId" ).getAsString ();
            final String operation = element.getAsJsonObject ().get ( "operation" ).getAsString ();
            final Map<String, String> newData = context.deserialize ( element.getAsJsonObject ().getAsJsonObject ( "addedOrUpdatedData" ), new TypeToken<Map<String, String>> () {}.getType () );
            final Set<String> removed = context.deserialize ( element.getAsJsonObject ().getAsJsonArray ( "removedData" ), new TypeToken<Set<String>> () {}.getType () );
            return new DiffEntry ( factoryId, configurationId, Operation.valueOf ( operation ), null, newData, removed );
        }
    }

    private static final long serialVersionUID = -3311156226543946433L;

    private final ConfigurationAdministrator configurationAdmin;

    private final Gson gson = new GsonBuilder ().setPrettyPrinting ().registerTypeAdapter ( Factory.class, new FactorySerializer () ).registerTypeAdapter ( DiffEntry.class, new DiffEntrySerializer () ).create ();

    private final ObjectMapper mapper = new ObjectMapper ();

    public JsonServlet ( final ConfigurationAdministrator configurationAdmin )
    {
        this.configurationAdmin = configurationAdmin;
    }

    @Override
    protected void doGet ( final HttpServletRequest req, final HttpServletResponse resp ) throws ServletException, IOException
    {
        if ( req.getPathInfo () == null || "/".equals ( req.getPathInfo () ) )
        {
            redirectToFactoryList ( req, resp );
            return;
        }
        else if ( req.getPathInfo ().startsWith ( "/factory" ) )
        {
            getFactory ( req, resp );
        }
        else if ( req.getPathInfo ().startsWith ( "/knownFactories" ) )
        {
            getKnownFactories ( req, resp );
        }
        else if ( req.getPathInfo ().startsWith ( "/configurations" ) )
        {
            getConfigurations ( req, resp );
        }
        else if ( req.getPathInfo ().startsWith ( "/configuration" ) )
        {
            getConfiguration ( req, resp );
        }
        else
        {
            send404Error ( req, resp );
        }
    }

    @Override
    protected void doPost ( final HttpServletRequest req, final HttpServletResponse resp ) throws ServletException, IOException
    {
        if ( req.getPathInfo ().startsWith ( "/createConfiguration" ) )
        {
            createConfiguration ( req, resp );
        }
        else if ( req.getPathInfo ().startsWith ( "/updateConfiguration" ) )
        {
            updateConfiguration ( req, resp );
        }
        else if ( req.getPathInfo ().startsWith ( "/deleteConfiguration" ) )
        {
            deleteConfiguration ( req, resp );
        }
        else if ( req.getPathInfo ().startsWith ( "/purgeFactory" ) )
        {
            purgeFactory ( req, resp );
        }
        else if ( req.getPathInfo ().startsWith ( "/createDiff" ) )
        {
            createDiff ( req, resp );
        }
        else if ( req.getPathInfo ().startsWith ( "/applyDiff" ) )
        {
            applyDiff ( req, resp );
        }
        else
        {
            send404Error ( req, resp );
        }
    }

    private void defaultContentType ( final HttpServletResponse resp )
    {
        resp.setContentType ( "text/javascript" );
    }

    private void send404Error ( final HttpServletRequest req, final HttpServletResponse resp ) throws IOException, ServletException
    {
        resp.sendError ( HttpServletResponse.SC_NOT_FOUND, "Not Found" );
    }

    private void redirectToFactoryList ( final HttpServletRequest req, final HttpServletResponse resp ) throws IOException, ServletException
    {
        resp.setHeader ( "Location", req.getServletPath () + "/knownFactories" );
        resp.sendError ( HttpServletResponse.SC_MOVED_PERMANENTLY, "Moved Permanently" );
    }

    private void getFactory ( final HttpServletRequest req, final HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        final String factoryId = getRequiredString ( req, resp, "factory.id" );
        this.gson.toJson ( this.configurationAdmin.getFactory ( factoryId ), Factory.class, resp.getWriter () );
    }

    private void getKnownFactories ( final HttpServletRequest req, final HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        this.gson.toJson ( this.configurationAdmin.getKnownFactories (), Factory[].class, resp.getWriter () );
    }

    private void getConfigurations ( final HttpServletRequest req, final HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        final String factoryId = getRequiredString ( req, resp, "factory.id" );
        this.gson.toJson ( this.configurationAdmin.getConfigurations ( factoryId ), resp.getWriter () );
    }

    private void getConfiguration ( final HttpServletRequest req, final HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        final String factoryId = getRequiredString ( req, resp, "factory.id" );
        final String id = getRequiredString ( req, resp, "id" );
        this.gson.toJson ( this.configurationAdmin.getConfiguration ( factoryId, id ), resp.getWriter () );
    }

    private void createConfiguration ( final HttpServletRequest req, final HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        try
        {
            final String factoryId = getRequiredString ( req, resp, "factory.id" );
            final String id = getRequiredString ( req, resp, "id" );
            final Map<String, String> properties = this.gson.fromJson ( req.getReader (), new TypeToken<Map<String, String>> () {}.getType () );
            final Future<Configuration> future = this.configurationAdmin.createConfiguration ( UserInformation.fromPrincipal ( req.getUserPrincipal () ), factoryId, id, properties );
            this.gson.toJson ( future.get (), resp.getWriter () );
        }
        catch ( final Exception e )
        {
            throw new ServletException ( e );
        }
    }

    private void updateConfiguration ( final HttpServletRequest req, final HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        try
        {
            final String factoryId = getRequiredString ( req, resp, "factory.id" );
            final String id = getRequiredString ( req, resp, "id" );
            final String full = getOptionalString ( req, resp, "full", "" );
            final Map<String, String> properties = this.gson.fromJson ( req.getReader (), new TypeToken<Map<String, String>> () {}.getType () );
            final Future<Configuration> future = this.configurationAdmin.updateConfiguration ( UserInformation.fromPrincipal ( req.getUserPrincipal () ), factoryId, id, properties, "full".equals ( full ) );
            this.gson.toJson ( future.get (), resp.getWriter () );
        }
        catch ( final Exception e )
        {
            throw new ServletException ( e );
        }
    }

    private void deleteConfiguration ( final HttpServletRequest req, final HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        try
        {
            final String factoryId = getRequiredString ( req, resp, "factory.id" );
            final String id = getRequiredString ( req, resp, "id" );
            final Future<Configuration> future = this.configurationAdmin.deleteConfiguration ( UserInformation.fromPrincipal ( req.getUserPrincipal () ), factoryId, id );
            this.gson.toJson ( future.get (), resp.getWriter () );
        }
        catch ( final Exception e )
        {
            throw new ServletException ( e );
        }
    }

    private void purgeFactory ( final HttpServletRequest req, final HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        try
        {
            final String factoryId = getRequiredString ( req, resp, "factory.id" );
            final Future<Void> future = this.configurationAdmin.purgeFactory ( UserInformation.fromPrincipal ( req.getUserPrincipal () ), factoryId );
            this.gson.toJson ( future.get (), resp.getWriter () );
        }
        catch ( final Exception e )
        {
            throw new ServletException ( e );
        }
    }

    @SuppressWarnings ( { "unchecked", "rawtypes" } )
    private void createDiff ( final HttpServletRequest req, final HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );

        final Map newConfiguration = this.mapper.readValue ( req.getReader (), HashMap.class );

        final Map existingConfiguration = new HashMap ();
        // convert existing configuration to same format as new one
        for ( final Factory factory : this.configurationAdmin.getKnownFactories () )
        {
            final Map config = new HashMap ();
            existingConfiguration.put ( factory.getId (), config );
            for ( final Configuration configuration : this.configurationAdmin.getConfigurations ( factory.getId () ) )
            {
                config.put ( configuration.getId (), new HashMap ( configuration.getData () ) );
            }
        }

        // create diff
        final List<DiffEntry> diff = new ArrayList<DiffEntry> ();

        // 1. find added
        for ( final Entry<String, Map<String, Map<String, String>>> factory : ( (Map<String, Map<String, Map<String, String>>>)newConfiguration ).entrySet () )
        {
            final String factoryId = factory.getKey ();
            final Map<String, Map<String, String>> existingConfigs = (Map<String, Map<String, String>>)existingConfiguration.get ( factoryId );
            for ( final Entry<String, Map<String, String>> config : ( (Map<String, Map<String, String>>)newConfiguration.get ( factoryId ) ).entrySet () )
            {
                final String id = config.getKey ();
                final Map<String, String> data = config.getValue ();
                if ( existingConfigs == null )
                {
                    diff.add ( new DiffEntry ( factoryId, id, Operation.ADD, null, new HashMap ( data ), null ) );
                }
                else if ( !existingConfigs.containsKey ( id ) )
                {
                    diff.add ( new DiffEntry ( factoryId, id, Operation.ADD, null, new HashMap ( data ), null ) );
                }
                else if ( !data.equals ( existingConfigs.get ( id ) ) )
                {
                    diff.add ( new DiffEntry ( factoryId, id, Operation.UPDATE_SET, null, new HashMap ( data ), null ) );
                }
            }
        }
        // find deleted
        for ( final Entry<String, Map<String, Map<String, String>>> factory : ( (Map<String, Map<String, Map<String, String>>>)existingConfiguration ).entrySet () )
        {
            final String factoryId = factory.getKey ();
            final Map<String, Map<String, String>> newConfigs = (Map<String, Map<String, String>>)newConfiguration.get ( factoryId );
            for ( final Entry<String, Map<String, String>> config : ( (Map<String, Map<String, String>>)existingConfiguration.get ( factoryId ) ).entrySet () )
            {
                final String id = config.getKey ();
                if ( newConfigs == null )
                {
                    diff.add ( new DiffEntry ( factoryId, id, Operation.DELETE, null, null, null ) );
                }
                else if ( !newConfigs.containsKey ( id ) )
                {
                    diff.add ( new DiffEntry ( factoryId, id, Operation.DELETE, null, null, null ) );
                }
            }
        }
        this.gson.toJson ( diff, resp.getWriter () );
    }

    private void applyDiff ( final HttpServletRequest req, final HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        try
        {
            final Collection<DiffEntry> changeSet = this.gson.fromJson ( req.getReader (), new TypeToken<Collection<DiffEntry>> () {}.getType () );
            final Future<Void> future = this.configurationAdmin.applyDiff ( UserInformation.fromPrincipal ( req.getUserPrincipal () ), changeSet );
            future.get ();
            resp.getWriter ().print ( changeSet.size () + " applied" );
        }
        catch ( final Exception e )
        {
            throw new ServletException ( e );
        }
    }

    private String getRequiredString ( final HttpServletRequest req, final HttpServletResponse resp, final String key ) throws ServletException, IOException
    {
        final Object o = req.getParameterMap ().get ( key );
        if ( o == null )
        {
            resp.getWriter ().print ( String.format ( "parameter %s must not be null", key ) );
            throw new ServletException ( String.format ( "parameter %s must not be null", key ) );
        }
        return ( (String[])o )[0];
    }

    private String getOptionalString ( final HttpServletRequest req, final HttpServletResponse resp, final String key, final String def )
    {
        final Object o = req.getParameterMap ().get ( key );
        if ( o == null )
        {
            return def;
        }
        return ( (String[])o )[0];
    }
}
