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

package org.openscada.hd.server.storage.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Executor;

import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryBuffer
{

    private final static Logger logger = LoggerFactory.getLogger ( QueryBuffer.class );

    private final QueryListener listener;

    private QueryParameters parameters;

    private final Executor executor;

    private static class Entry implements Comparable<Entry>
    {
        private final double value;

        private final Date timestamp;

        private final boolean error;

        private final boolean manual;

        public Entry ( final double value, final Date timestamp, final boolean error, final boolean manual )
        {
            this.value = value;
            this.timestamp = timestamp;
            this.error = error;
            this.manual = manual;
        }

        @Override
        public int hashCode ()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( this.timestamp == null ? 0 : this.timestamp.hashCode () );
            return result;
        }

        public Date getTimestamp ()
        {
            return this.timestamp;
        }

        public double getValue ()
        {
            return this.value;
        }

        public boolean isError ()
        {
            return this.error;
        }

        public boolean isManual ()
        {
            return this.manual;
        }

        @Override
        public boolean equals ( final Object obj )
        {
            if ( this == obj )
            {
                return true;
            }
            if ( obj == null )
            {
                return false;
            }
            if ( getClass () != obj.getClass () )
            {
                return false;
            }
            final Entry other = (Entry)obj;
            if ( this.timestamp == null )
            {
                if ( other.timestamp != null )
                {
                    return false;
                }
            }
            else if ( !this.timestamp.equals ( other.timestamp ) )
            {
                return false;
            }
            return true;
        }

        @Override
        public int compareTo ( final Entry o )
        {
            return this.timestamp.compareTo ( o.timestamp );
        }

        @Override
        public String toString ()
        {
            return String.format ( "[timestamp: %tc, value: %s, error: %s, manual: %s]", this.timestamp, this.value, this.error, this.manual );
        }
    }

    private static class Data
    {
        private final TreeSet<Entry> entries = new TreeSet<QueryBuffer.Entry> ();

        private final Date start;

        private final Date end;

        private boolean changed;

        private double average = Double.NaN;

        private double quality = Double.NaN;

        private double manual = Double.NaN;

        private double min = Double.NaN;

        private double max = Double.NaN;

        private long entryCount;

        public Data ( final Date start, final Date end )
        {
            this.start = start;
            this.end = end;

            // defaulting to true since we need at least one transmission
            this.changed = true;
        }

        public void add ( final Entry entry )
        {
            if ( !entry.getTimestamp ().before ( this.start ) && entry.getTimestamp ().before ( this.end ) )
            {
                // we only accept entries for start <= timestamp < end
                this.entries.add ( entry );
                if ( !Double.isNaN ( entry.value ) )
                {
                    this.entryCount++;
                }
            }
        }

        public long getEntryCount ()
        {
            return this.entryCount;
        }

        public SortedSet<Entry> getEntries ()
        {
            return this.entries;
        }

        public Date getStart ()
        {
            return this.start;
        }

        public Date getEnd ()
        {
            return this.end;
        }

        public boolean isChanged ()
        {
            return this.changed;
        }

        public void resetChanged ()
        {
            this.changed = false;
        }

        public void setAverage ( final double average )
        {
            if ( Double.compare ( this.average, average ) != 0 )
            {
                this.changed = true;
                this.average = average;
            }
        }

        public void setQuality ( final double error )
        {
            if ( Double.compare ( this.quality, error ) != 0 )
            {
                this.changed = true;
                this.quality = error;
            }
        }

        public void setManual ( final double manual )
        {
            if ( Double.compare ( this.manual, manual ) != 0 )
            {
                this.changed = true;
                this.manual = manual;
            }
        }

        public double getAverage ()
        {
            return this.average;
        }

        public double getQuality ()
        {
            return this.quality;
        }

        public double getManual ()
        {
            return this.manual;
        }

        public void setMax ( final double max )
        {
            this.max = max;
        }

        public void setMin ( final double min )
        {
            this.min = min;
        }

        public double getMin ()
        {
            return this.min;
        }

        public double getMax ()
        {
            return this.max;
        }
    }

    private Entry firstEntry;

    private final SortedSet<Entry> entries = new TreeSet<QueryBuffer.Entry> ();

    private Data[] data;

    private QueryState state;

    private final boolean useNaNs = Boolean.getBoolean ( "org.openscada.hd.server.storage.hds.useNaNs" );

    public QueryBuffer ( final QueryListener listener, final Executor executor )
    {
        this.listener = listener;
        this.executor = executor;
    }

    public synchronized void changeParameters ( final QueryParameters parameters )
    {
        this.parameters = parameters;
        this.listener.updateState ( QueryState.LOADING );
        this.listener.updateParameters ( parameters, new HashSet<String> ( Arrays.asList ( "AVG", "MIN", "MAX" ) ) );

        // clear
        this.entries.clear ();
        this.firstEntry = null;
        this.data = new Data[parameters.getEntries ()];

        final long start = parameters.getStartTimestamp ().getTimeInMillis ();
        final long end = parameters.getEndTimestamp ().getTimeInMillis ();

        // create data buffer
        final double period = (double) ( end - start ) / (double)this.data.length;
        double counter = 0;
        for ( int i = 0; i < this.data.length; i++ )
        {
            final long startTix = (long) ( start + counter );
            final long endTix = (long) ( start + ( counter + period ) );
            logger.trace ( "Init index {} with {} -> {}", new Object[] { i, startTix, endTix } );
            this.data[i] = new Data ( new Date ( startTix ), new Date ( endTix ) );
            counter += period;
        }
    }

    public synchronized void insertData ( final double value, final Date timestamp, final boolean error, final boolean manual )
    {
        final Entry entry = new Entry ( value, timestamp, error, manual );
        logger.debug ( "Received new data: {}", entry );

        if ( timestamp.before ( this.parameters.getStartTimestamp ().getTime () ) )
        {
            if ( this.firstEntry == null || this.firstEntry.getTimestamp ().before ( timestamp ) )
            {
                logger.debug ( "Evaluating entry as first entry: {}", entry );
                if ( !Double.isNaN ( entry.getValue () ) || this.useNaNs )
                {
                    logger.debug ( "Using entry as first entry" );
                    this.firstEntry = entry;
                }
            }
        }
        else if ( !timestamp.after ( this.parameters.getEndTimestamp ().getTime () ) )
        {
            logger.debug ( "Adding entry: {}", entry );
            this.entries.add ( entry );

            final int i = getDataIndex ( timestamp );

            logger.debug ( "Inserting into cell: {}", i );

            if ( i >= 0 && i < this.parameters.getEntries () )
            {
                this.data[i].add ( entry );

                if ( this.state == QueryState.LOADING )
                {
                    render ( i, i + 1 );
                }
            }
        }
    }

    /**
     * Render buffer from provided start index to the end of the buffer
     * @param startIndex the start index
     */
    private void render ( final int startIndex )
    {
        render ( startIndex, Integer.MAX_VALUE );
    }

    /**
     * render the buffer from the provided start to the provided end
     * @param startIndex the start index
     * @param endIndex the end index
     */
    private void render ( final int startIndex, int endIndex )
    {
        endIndex = Math.min ( endIndex, this.parameters.getEntries () );

        Entry currentEntry = findPreviousEntry ( startIndex );

        double max = Double.NaN;
        double min = Double.NaN;

        final RunningAverage avg = new RunningAverage ();
        final RunningAverage quality = new RunningAverage ();
        final RunningAverage manual = new RunningAverage ();
        if ( currentEntry != null )
        {
            avg.next ( currentEntry.getValue (), currentEntry.getTimestamp ().getTime () );
            quality.next ( currentEntry.isError () ? 0.0 : 1.0, currentEntry.getTimestamp ().getTime () );
            manual.next ( currentEntry.isManual () ? 1.0 : 0.0, currentEntry.getTimestamp ().getTime () );
            if ( !Double.isNaN ( currentEntry.getValue () ) )
            {
                min = max = currentEntry.getValue ();
            }
        }

        for ( int i = startIndex; i < endIndex; i++ )
        {
            // reset to start of cell
            avg.step ( this.data[i].getStart ().getTime () );
            quality.step ( this.data[i].getStart ().getTime () );
            manual.step ( this.data[i].getStart ().getTime () );

            for ( final Entry entry : this.data[i].getEntries () )
            {

                quality.next ( entry.isError () ? 0.0 : 1.0, entry.getTimestamp ().getTime () );
                manual.next ( entry.isManual () ? 1.0 : 0.0, entry.getTimestamp ().getTime () );

                if ( !Double.isNaN ( entry.getValue () ) || this.useNaNs )
                {
                    avg.next ( entry.getValue (), entry.getTimestamp ().getTime () );
                    if ( Double.isNaN ( max ) || Double.compare ( entry.getValue (), max ) > 0 )
                    {
                        max = entry.getValue ();
                    }
                    if ( Double.isNaN ( min ) || Double.compare ( entry.getValue (), min ) < 0 )
                    {
                        min = entry.getValue ();
                    }
                    currentEntry = entry;
                }
            }

            this.data[i].setAverage ( avg.getAverage ( this.data[i].getEnd ().getTime () ) );
            this.data[i].setQuality ( quality.getAverage ( this.data[i].getEnd ().getTime () ) );
            this.data[i].setManual ( manual.getAverage ( this.data[i].getEnd ().getTime () ) );
            this.data[i].setMin ( min );
            this.data[i].setMax ( max );

            if ( currentEntry != null )
            {
                min = max = currentEntry.getValue ();
            }
        }

        notifyData ( startIndex, endIndex );
    }

    private void notifyData ( final int startIndex, final int endIndex )
    {
        final Collection<ValueInformation> information = new ArrayList<ValueInformation> ();
        final Map<String, Collection<Value>> values = new HashMap<String, Collection<Value>> ();
        values.put ( "AVG", new ArrayList<Value> () );
        values.put ( "MIN", new ArrayList<Value> () );
        values.put ( "MAX", new ArrayList<Value> () );

        int lastIndex = startIndex;
        for ( int i = startIndex; i < endIndex; i++ )
        {
            if ( this.data[i].isChanged () )
            {
                this.data[i].resetChanged ();

                final double quality = Double.isNaN ( this.data[i].getQuality () ) ? 0.0 : this.data[i].getQuality ();
                final double manual = Double.isNaN ( this.data[i].getManual () ) ? 0.0 : this.data[i].getManual ();

                information.add ( new ValueInformation ( convert ( this.data[i].getStart () ), convert ( this.data[i].getEnd () ), quality, manual, this.data[i].getEntryCount () ) );
                values.get ( "AVG" ).add ( new Value ( this.data[i].getAverage () ) );
                values.get ( "MIN" ).add ( new Value ( this.data[i].getMin () ) );
                values.get ( "MAX" ).add ( new Value ( this.data[i].getMax () ) );
                // add
            }
            else
            {
                // send
                if ( !information.isEmpty () )
                {
                    notifyData ( lastIndex, convert ( values ), information.toArray ( new ValueInformation[information.size ()] ) );
                    information.clear ();
                    values.get ( "AVG" ).clear ();
                    values.get ( "MIN" ).clear ();
                    values.get ( "MAX" ).clear ();
                }
                // clear
                lastIndex = i;
            }
        }

        // send last
        if ( !information.isEmpty () )
        {
            notifyData ( lastIndex, convert ( values ), information.toArray ( new ValueInformation[information.size ()] ) );
        }
    }

    private Calendar convert ( final Date date )
    {
        final Calendar c = Calendar.getInstance ();
        c.setTime ( date );
        return c;
    }

    private Map<String, Value[]> convert ( final Map<String, Collection<Value>> values )
    {
        final Map<String, Value[]> result = new HashMap<String, Value[]> ();

        for ( final Map.Entry<String, Collection<Value>> entry : values.entrySet () )
        {
            result.put ( entry.getKey (), entry.getValue ().toArray ( new Value[entry.getValue ().size ()] ) );
        }

        return result;
    }

    protected Entry findPreviousEntry ( final int i )
    {
        if ( i <= 0 )
        {
            return this.firstEntry;
        }
        else
        {
            if ( this.data[i - 1].getEntries ().isEmpty () )
            {
                return findPreviousEntry ( i - 1 );
            }
            else
            {
                return this.data[i - 1].getEntries ().last ();
            }
        }
    }

    protected Entry findNextEntry ( final int i )
    {
        if ( i + 1 >= this.parameters.getEntries () )
        {
            return null;
        }
        else
        {
            if ( this.data[i + 1].getEntries ().isEmpty () )
            {
                return findNextEntry ( i + 1 );
            }
            else
            {
                return this.data[i + 1].getEntries ().first ();
            }
        }
    }

    private int getDataIndex ( final Date timestamp )
    {
        if ( timestamp.before ( this.parameters.getStartTimestamp ().getTime () ) )
        {
            return -1;
        }

        final double period = getPeriod ();

        final long offset = timestamp.getTime () - this.parameters.getStartTimestamp ().getTimeInMillis ();

        return (int) ( offset / period );
    }

    private double getPeriod ()
    {
        return (double) ( this.parameters.getEndTimestamp ().getTimeInMillis () - this.parameters.getStartTimestamp ().getTimeInMillis () ) / (double)this.parameters.getEntries ();
    }

    public synchronized void complete ()
    {
        render ( 0 );
        notifyStateUpdate ( QueryState.COMPLETE );
    }

    public synchronized void close ()
    {
        notifyStateUpdate ( QueryState.DISCONNECTED );
    }

    public synchronized void updateData ( final double value, final Date timestamp, final boolean error, final boolean manual )
    {
        insertData ( value, timestamp, error, manual );
        complete ();
    }

    protected synchronized void notifyData ( final int index, final Map<String, Value[]> values, final ValueInformation[] valueInformation )
    {
        if ( this.listener == null )
        {
            return;
        }

        logger.debug ( "Sending data - index: {}, values#: {}, informations#: {}", new Object[] { index, values.size (), valueInformation.length } );

        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                QueryBuffer.this.listener.updateData ( index, values, valueInformation );
            }
        } );
    }

    protected synchronized void notifyStateUpdate ( final QueryState state )
    {
        logger.debug ( "Change state to {}", state );

        this.state = state;

        if ( this.listener == null )
        {
            return;
        }

        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                QueryBuffer.this.listener.updateState ( state );
            }
        } );

    }
}
