package org.openscada.utils.lang;

public class Pair<T1, T2>
{
    public T1 first = null;
    public T2 second = null;
    
    public Pair ( T1 first, T2 second )
    {
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( first == null ) ? 0 : first.hashCode () );
        result = PRIME * result + ( ( second == null ) ? 0 : second.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass () != obj.getClass () )
            return false;

        final Pair other = (Pair)obj;
        if ( first == null )
        {
            if ( other.first != null )
                return false;
        }
        else
            if ( !first.equals ( other.first ) )
                return false;
        if ( second == null )
        {
            if ( other.second != null )
                return false;
        }
        else
            if ( !second.equals ( other.second ) )
                return false;
        return true;
    }
}
