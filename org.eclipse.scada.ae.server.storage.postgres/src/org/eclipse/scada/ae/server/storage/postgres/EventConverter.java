/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jürgen Rose (cptmauli@googlemail.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.eclipse.scada.ae.server.storage.postgres;

import java.lang.reflect.Type;

import org.eclipse.scada.ae.Event;
import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.VariantEditor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class EventConverter
{
    private static class VariantSerializer implements JsonSerializer<Variant>
    {
        @Override
        public JsonElement serialize ( final Variant src, final Type typeOfSrc, final JsonSerializationContext context )
        {
            return new JsonPrimitive ( src.toString () );
        }
    }

    private static class VariantDeserializer implements JsonDeserializer<Variant>
    {
        @Override
        public Variant deserialize ( final JsonElement jsonElement, final Type typeOfDst, final JsonDeserializationContext context ) throws JsonParseException
        {
            return VariantEditor.toVariant ( jsonElement.getAsString () );
        }
    }

    private static final String isoDatePatterrn = "yyyy-MM-dd HH:mm:ss.SSS";

    private static final Gson gson = new GsonBuilder ().setDateFormat ( isoDatePatterrn ).serializeNulls ().serializeSpecialFloatingPointValues ().registerTypeAdapter ( Variant.class, new VariantSerializer () ).registerTypeAdapter ( Variant.class, new VariantDeserializer () ).create ();

    public static final EventConverter INSTANCE = new EventConverter ();

    public Event toEvent ( final String json )
    {
        return gson.fromJson ( json, Event.class );
    }

    public String toJson ( final Event event )
    {
        return gson.toJson ( event );
    }
}
