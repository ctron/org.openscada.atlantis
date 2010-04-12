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

package org.openscada.da.server.dave.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.ca.ConfigurationFactory;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class VariableManagerImpl implements VariableManager, ConfigurationFactory
{

    private final static Logger logger = LoggerFactory.getLogger ( VariableManagerImpl.class );

    private static enum TYPE
    {
        BIT,
        TRIBIT,
        BYTE,
        FLOAT,
        WORD,
        UDT
    }

    private static class TypeEntry
    {
        private final String name;

        private final TYPE type;

        private String typeName;

        private final int[] index;

        private final TypeEntry[] attributes;

        public TypeEntry ( final String name, final String typeName, final int index )
        {
            this.name = name;
            this.type = TYPE.UDT;
            this.typeName = typeName;
            this.attributes = null;
            this.index = new int[] { index };
        }

        public TypeEntry ( final String name, final int[] index, final TypeEntry... attributes )
        {
            this.name = name;
            this.index = index.clone ();
            this.type = TYPE.TRIBIT;
            this.attributes = attributes;
        }

        public TypeEntry ( final String name, final int index, final int subIndex, final int options, final TypeEntry... attributes )
        {
            this.name = name;
            this.index = new int[] { index, subIndex, options };
            this.type = TYPE.BIT;
            this.attributes = attributes;
        }

        public TypeEntry ( final String name, final TYPE type, final int index, final int options, final TypeEntry... attributes )
        {
            this.name = name;
            this.index = new int[] { index, options };
            this.type = type;
            this.attributes = attributes;
        }

        public int[] getIndexes ()
        {
            return this.index;
        }

        public TypeEntry[] getAttributes ()
        {
            return this.attributes;
        }

        public int getIndex ()
        {
            return this.index[0];
        }

        public String getName ()
        {
            return this.name;
        }

        public int getSubIndex ()
        {
            return this.index[1];
        }

        public TYPE getType ()
        {
            return this.type;
        }

        public String getTypeName ()
        {
            return this.typeName;
        }

        @Override
        public String toString ()
        {
            switch ( this.type )
            {
            default:
                return String.format ( "%s:%s", this.name, this.type );
            }
        }
    }

    private final Multimap<String, VariableListener> listeners = HashMultimap.create ();

    private final Multimap<String, String> typeDeps = HashMultimap.create ();

    private final Map<String, Collection<TypeEntry>> types = new HashMap<String, Collection<TypeEntry>> ();

    private final ExecutorService executor;

    private final ObjectPoolImpl itemPool;

    public VariableManagerImpl ( final Executor executor, final ObjectPoolImpl itemPool )
    {
        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( "VariableManager" ) );
        this.itemPool = itemPool;
    }

    public void dispose ()
    {
        this.executor.shutdown ();
    }

    public synchronized void addVariableListener ( final String type, final VariableListener listener )
    {
        this.listeners.put ( type, listener );
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                listener.variableConfigurationChanged ( createVariables ( type ) );
            }
        } );
    }

    public synchronized void removeVariableListener ( final String type, final VariableListener listener )
    {
        this.listeners.remove ( type, listener );
    }

    public synchronized void delete ( final String configurationId ) throws Exception
    {
        this.types.remove ( configurationId );
        this.typeDeps.removeAll ( configurationId );
        handleTypeChange ( configurationId );
    }

    private void fireTypeChange ( final String type )
    {
        logger.debug ( "Fire type change: {}", type );

        // make a clone
        final Collection<VariableListener> listeners = new ArrayList<VariableListener> ( this.listeners.get ( type ) );

        this.executor.execute ( new Runnable () {

            public void run ()
            {
                for ( final VariableListener listener : listeners )
                {
                    logger.info ( "Apply type change: {}", type );
                    listener.variableConfigurationChanged ( createVariables ( type ) );
                }
            }
        } );
    }

    public synchronized void update ( final String configurationId, final Map<String, String> properties ) throws Exception
    {
        logger.debug ( "Adding type: {}", configurationId );

        final Collection<TypeEntry> config = parseConfig ( properties );
        config.addAll ( parseConfig2 ( properties ) );

        this.types.put ( configurationId, config );

        logger.debug ( "Generate deps:" );
        final Set<String> types = new HashSet<String> ();
        for ( final TypeEntry entry : config )
        {
            if ( entry.getType () == TYPE.UDT )
            {
                logger.debug ( "'{}' depends on '{}'", new Object[] { configurationId, entry.getTypeName () } );
                types.add ( entry.getTypeName () );
            }
        }
        this.typeDeps.putAll ( configurationId, types );

        handleTypeChange ( configurationId );
    }

    /**
     * Handle a type change and fire change events for all dependent types
     * @param configurationId
     */
    private void handleTypeChange ( final String configurationId )
    {
        logger.info ( "Handle type change: {}", configurationId );

        fireTypeChange ( configurationId );

        for ( final Map.Entry<String, Collection<String>> entry : this.typeDeps.asMap ().entrySet () )
        {
            logger.debug ( String.format ( "'%s' depends on '%s'", entry.getKey (), entry.getValue () ) );

            if ( entry.getValue ().contains ( configurationId ) )
            {
                logger.debug ( String.format ( "Trigger dependency - '%s' depends on '%s'", entry.getKey (), configurationId ) );
                handleTypeChange ( entry.getKey () );
            }
        }
    }

    private Variable[] createVariables ( final String type )
    {
        logger.debug ( "Creating variables for type: {}", type );

        final Collection<TypeEntry> entries = this.types.get ( type );
        if ( entries == null )
        {
            return new Variable[0];
        }
        else
        {
            final Collection<Variable> result = new ArrayList<Variable> ();
            for ( final TypeEntry entry : entries )
            {
                switch ( entry.getType () )
                {
                case BIT:
                    result.add ( new BitVariable ( entry.getName (), entry.getIndex (), entry.getSubIndex (), this.executor, this.itemPool, createAttributes ( entry ) ) );
                    break;
                case BYTE:
                    result.add ( new ByteVariable ( entry.getName (), entry.getIndex (), this.executor, this.itemPool, createAttributes ( entry ) ) );
                    break;
                case FLOAT:
                    result.add ( new FloatVariable ( entry.getName (), entry.getIndex (), this.executor, this.itemPool, createAttributes ( entry ) ) );
                    break;
                case WORD:
                    result.add ( new WordVariable ( entry.getName (), entry.getIndex (), this.executor, this.itemPool, createAttributes ( entry ) ) );
                    break;
                case UDT:
                    result.add ( new UdtVariable ( entry.getName (), entry.getIndex (), createVariables ( entry.getTypeName () ) ) );
                    break;
                }
            }
            return result.toArray ( new Variable[result.size ()] );
        }
    }

    private Attribute[] createAttributes ( final TypeEntry entry )
    {
        logger.debug ( "Creating attributes for {}", entry );

        final Collection<Attribute> result = new LinkedList<Attribute> ();

        for ( final TypeEntry attrEntry : entry.getAttributes () )
        {
            logger.debug ( "Creating attribute: {}", attrEntry );

            switch ( attrEntry.getType () )
            {
            case BIT:
                result.add ( new BitAttribute ( attrEntry.getName (), attrEntry.getIndex (), attrEntry.getSubIndex (), attrEntry.getIndexes ()[2] != 0 ) );
                break;
            case FLOAT:
                result.add ( new FloatAttribute ( attrEntry.getName (), attrEntry.getIndex (), attrEntry.getIndexes ()[1] != 0 ) );
                break;
            case TRIBIT:
                final int[] index = attrEntry.getIndexes ();
                result.add ( new TriBitAttribute ( attrEntry.getName (), index[0], index[1], index[2], index[3], index[4], index[5], index[6] != 0, index[7] != 0 ) );
                break;
            default:
                break;
            }
        }
        return result.toArray ( new Attribute[0] );
    }

    private Collection<TypeEntry> parseConfig2 ( final Map<String, String> properties )
    {
        final Collection<TypeEntry> result = new LinkedList<TypeEntry> ();

        for ( final Map.Entry<String, String> entry : properties.entrySet () )
        {
            final String key = entry.getKey ();
            if ( !key.startsWith ( "variable." ) )
            {
                continue;
            }
            final String varName = key.substring ( "variable.".length () );

            final String toks[] = entry.getValue ().split ( ":" );

            switch ( TYPE.valueOf ( toks[0] ) )
            {
            case BIT:
                result.add ( new TypeEntry ( varName, Integer.parseInt ( toks[1] ), Integer.parseInt ( toks[2] ), 0, parseAttributes ( properties, varName ) ) );
                break;
            case BYTE:
                result.add ( new TypeEntry ( varName, TYPE.BYTE, Integer.parseInt ( toks[1] ), 0, parseAttributes ( properties, varName ) ) );
                break;
            case FLOAT:
                result.add ( new TypeEntry ( varName, TYPE.FLOAT, Integer.parseInt ( toks[1] ), 0, parseAttributes ( properties, varName ) ) );
                break;
            case WORD:
                result.add ( new TypeEntry ( varName, TYPE.WORD, Integer.parseInt ( toks[1] ), 0, parseAttributes ( properties, varName ) ) );
                break;
            case UDT:
                result.add ( new TypeEntry ( varName, toks[1], Integer.parseInt ( toks[2] ) ) );
                break;
            }
        }

        return result;
    }

    private Collection<TypeEntry> parseConfig ( final Map<String, String> properties )
    {
        // 'definition' is: "var1:UDT:test:2 var2:BYTE:1 var3:BIT:1:0"
        // 'attribute.XXX' is attribute for XXX : "attr1:BYTE:1 attr2:BIT:1:0"

        final Collection<TypeEntry> result = new LinkedList<TypeEntry> ();

        final String def = properties.get ( "definition" );

        if ( def == null )
        {
            return result;
        }

        for ( final String tok : def.split ( " " ) )
        {
            final String toks[] = tok.split ( ":" );
            if ( toks.length < 3 )
            {
                // FIXME: throw error
                continue;
            }

            switch ( TYPE.valueOf ( toks[1] ) )
            {
            case BIT:
                result.add ( new TypeEntry ( toks[0], Integer.parseInt ( toks[2] ), Integer.parseInt ( toks[3] ), 0, parseAttributes ( properties, toks[0] ) ) );
                break;
            case BYTE:
                result.add ( new TypeEntry ( toks[0], TYPE.BYTE, Integer.parseInt ( toks[2] ), 0, parseAttributes ( properties, toks[0] ) ) );
                break;
            case FLOAT:
                result.add ( new TypeEntry ( toks[0], TYPE.FLOAT, Integer.parseInt ( toks[2] ), 0, parseAttributes ( properties, toks[0] ) ) );
                break;
            case WORD:
                result.add ( new TypeEntry ( toks[0], TYPE.WORD, Integer.parseInt ( toks[2] ), 0, parseAttributes ( properties, toks[0] ) ) );
                break;
            case UDT:
                result.add ( new TypeEntry ( toks[0], toks[2], Integer.parseInt ( toks[3] ) ) );
                break;
            }
        }

        return result;
    }

    private TypeEntry[] parseAttributes ( final Map<String, String> properties, final String varName )
    {
        final String definition = properties.get ( "attribute." + varName );

        if ( definition == null )
        {
            logger.debug ( "No attributes for '{}'", varName );
            return new TypeEntry[0];
        }

        logger.debug ( "Attribute definition for '{}': {}", new Object[] { varName, definition } );

        final Collection<TypeEntry> result = new LinkedList<TypeEntry> ();

        for ( final String tok : definition.split ( " " ) )
        {
            final String toks[] = tok.split ( ":" );
            if ( toks.length < 3 )
            {
                // FIXME: throw error
                continue;
            }

            switch ( TYPE.valueOf ( toks[1] ) )
            {
            case BIT:
                result.add ( new TypeEntry ( toks[0], Integer.parseInt ( toks[2] ), Integer.parseInt ( toks[3] ), Integer.parseInt ( toks[4] ) ) );
                break;
            case FLOAT:
                result.add ( new TypeEntry ( toks[0], TYPE.FLOAT, Integer.parseInt ( toks[2] ), Integer.parseInt ( toks[3] ) ) );
                break;
            case TRIBIT:
                result.add ( new TypeEntry ( toks[0], new int[] {//
                Integer.parseInt ( toks[2] ), Integer.parseInt ( toks[3] ),// read bit
                Integer.parseInt ( toks[4] ), Integer.parseInt ( toks[5] ),// write true bit
                Integer.parseInt ( toks[6] ), Integer.parseInt ( toks[7] ),// write false bit
                Integer.parseInt ( toks[8] ), // invert
                Integer.parseInt ( toks[9] ), // enableTimestamp
                } ) );
                break;
            case BYTE:
                result.add ( new TypeEntry ( toks[0], TYPE.BYTE, Integer.parseInt ( toks[2] ), Integer.parseInt ( toks[3] ) ) );
                break;
            default:
                break;
            }
        }
        return result.toArray ( new TypeEntry[0] );
    }
}
