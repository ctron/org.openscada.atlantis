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

package org.openscada.ae.client.internal;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author jrose
 *
 */
public class CollectionsBackPort
{
    public static <E> Set<E> newSetFromMap ( final Map<E, Boolean> map )
    {
        return new SetFromMap<E> ( map );
    }

    private static class SetFromMap<E> extends AbstractSet<E> implements Set<E>, Serializable
    {
        private static final long serialVersionUID = -4114452102647116690L;

        private final Map<E, Boolean> m; // The backing map

        private transient Set<E> s; // Its keySet

        SetFromMap ( final Map<E, Boolean> map )
        {
            if ( !map.isEmpty () )
            {
                throw new IllegalArgumentException ( "Map is non-empty" );
            }
            this.m = map;
            this.s = map.keySet ();
        }

        @Override
        public void clear ()
        {
            this.m.clear ();
        }

        @Override
        public int size ()
        {
            return this.m.size ();
        }

        @Override
        public boolean isEmpty ()
        {
            return this.m.isEmpty ();
        }

        @Override
        public boolean contains ( final Object o )
        {
            return this.m.containsKey ( o );
        }

        @Override
        public boolean remove ( final Object o )
        {
            return this.m.remove ( o ) != null;
        }

        @Override
        public boolean add ( final E e )
        {
            return this.m.put ( e, Boolean.TRUE ) == null;
        }

        @Override
        public Iterator<E> iterator ()
        {
            return this.s.iterator ();
        }

        @Override
        public Object[] toArray ()
        {
            return this.s.toArray ();
        }

        @Override
        public <T> T[] toArray ( final T[] a )
        {
            return this.s.toArray ( a );
        }

        @Override
        public String toString ()
        {
            return this.s.toString ();
        }

        @Override
        public int hashCode ()
        {
            return this.s.hashCode ();
        }

        @Override
        public boolean equals ( final Object o )
        {
            return o == this || this.s.equals ( o );
        }

        @Override
        public boolean containsAll ( final Collection<?> c )
        {
            return this.s.containsAll ( c );
        }

        @Override
        public boolean removeAll ( final Collection<?> c )
        {
            return this.s.removeAll ( c );
        }

        @Override
        public boolean retainAll ( final Collection<?> c )
        {
            return this.s.retainAll ( c );
        }

        private void readObject ( final java.io.ObjectInputStream stream ) throws IOException, ClassNotFoundException
        {
            stream.defaultReadObject ();
            this.s = this.m.keySet ();
        }
    }
}
