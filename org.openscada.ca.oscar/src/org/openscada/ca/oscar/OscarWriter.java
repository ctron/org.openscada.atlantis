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

package org.openscada.ca.oscar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OscarWriter
{
    private final Map<String, Map<String, Map<String, String>>> data;

    private final Map<String, Set<String>> ignoreData;

    /**
     * Create a new writer instance
     * 
     * @param data
     *            the data to write (most not be <code>null</code>)
     * @param ignoreData
     *            the ignore data (may be <code>null</code>)
     */
    public OscarWriter ( final Map<String, Map<String, Map<String, String>>> data, final Map<String, Set<String>> ignoreData )
    {
        this.data = data;
        this.ignoreData = ignoreData;
    }

    public void write ( final File file ) throws FileNotFoundException, IOException
    {
        write ( new FileOutputStream ( file ) );
    }

    /**
     * Perform the write operation
     * <p>
     * The stream is closed.
     * </p>
     * 
     * @param targetStream
     *            target stream to write to
     * @throws IOException
     *             if an IO error occurs
     */
    public void write ( final OutputStream targetStream ) throws IOException
    {
        final ZipOutputStream zout = new ZipOutputStream ( targetStream );

        try
        {
            ZipEntry entry;

            // write data
            entry = new ZipEntry ( "data.json" );
            zout.putNextEntry ( entry );
            writeData ( this.data, zout );
            zout.closeEntry ();

            // write ignore fields if we have some
            if ( this.ignoreData != null && !this.ignoreData.isEmpty () )
            {
                entry = new ZipEntry ( "ignoreFields.json" );
                zout.putNextEntry ( entry );
                writeIgnoreData ( this.ignoreData, zout );
                zout.closeEntry ();
            }
        }
        finally
        {
            zout.close ();
        }

    }

    /**
     * Write out the data as JSON encoded data
     * <p>
     * The stream is not closed
     * </p>
     * 
     * @param data
     *            the data to write
     * @param stream
     *            the stream to write to
     * @throws IOException
     */
    public static void writeData ( final Map<String, Map<String, Map<String, String>>> data, final OutputStream stream ) throws IOException
    {
        final BufferedWriter writer = new BufferedWriter ( new OutputStreamWriter ( stream, "UTF-8" ) );
        final Gson g = new GsonBuilder ().setPrettyPrinting ().create ();
        g.toJson ( data, writer );
        writer.flush ();
    }

    /**
     * Write out the ignore data as JSON encoded data
     * <p>
     * The stream is not closed
     * </p>
     * 
     * @param ignoreData
     *            the data to write
     * @param stream
     *            the stream to write to
     * @throws IOException
     */
    public static void writeIgnoreData ( final Map<String, Set<String>> ignoreData, final OutputStream stream ) throws IOException
    {
        final BufferedWriter writer = new BufferedWriter ( new OutputStreamWriter ( stream, "UTF-8" ) );
        final Gson g = new GsonBuilder ().setPrettyPrinting ().create ();
        g.toJson ( ignoreData, writer );
        writer.flush ();
    }
}
