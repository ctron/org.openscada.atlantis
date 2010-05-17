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

package org.openscada.ae.server.http.internal;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.openscada.ae.Event;
import org.openscada.core.Variant;
import org.openscada.core.VariantEditor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class EventSerializer
{
    public static final String isoDatePattern = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final DateFormat isoDateFormat = new SimpleDateFormat ( isoDatePattern );

    private static class VariantTypeAdapter implements JsonSerializer<Variant>, JsonDeserializer<Variant>
    {
        public JsonElement serialize ( final Variant src, final Type typeOfSrc, final JsonSerializationContext context )
        {
            return new JsonPrimitive ( src.toString () );
        }

        public Variant deserialize ( final JsonElement json, final Type typeOfT, final JsonDeserializationContext context ) throws JsonParseException
        {
            final VariantEditor ve = new VariantEditor ();
            ve.setAsText ( json.getAsJsonPrimitive ().getAsString () );
            return (Variant)ve.getValue ();
        }
    }

    private static final Gson gson = new GsonBuilder ().setDateFormat ( DateFormat.FULL ).setDateFormat ( isoDatePattern ).registerTypeAdapter ( Variant.class, new VariantTypeAdapter () ).create ();

    public static String serializeEvent ( final Event event )
    {
        return gson.toJson ( event );
    }

    public static Event deserializeEvent ( final String event )
    {
        return gson.fromJson ( event, Event.class );
    }
}
