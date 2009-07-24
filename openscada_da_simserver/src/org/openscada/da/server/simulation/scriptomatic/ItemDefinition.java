package org.openscada.da.server.simulation.scriptomatic;

import org.openscada.utils.lang.Immutable;

@Immutable
public class ItemDefinition
{
    private final String id;

    private final String contextId;

    private final Object triggerCode;

    private final Object initCode;

    private final Object cycleCode;

    private final long cycleTime;

    private final boolean defaultChain;

    public ItemDefinition ( final String id, final String contextId, final Object triggerCode, final Object initCode, final Object cycleCode, final long cycleTime, final boolean defaultChain )
    {
        super ();
        this.id = id;
        this.contextId = contextId;
        this.triggerCode = triggerCode;
        this.initCode = initCode;
        this.cycleCode = cycleCode;
        this.cycleTime = cycleTime;
        this.defaultChain = defaultChain;
    }

    public String getId ()
    {
        return this.id;
    }

    public String getContextId ()
    {
        return this.contextId;
    }

    public Object getTriggerCode ()
    {
        return this.triggerCode;
    }

    public Object getInitCode ()
    {
        return this.initCode;
    }

    public Object getCycleCode ()
    {
        return this.cycleCode;
    }

    public long getCycleTime ()
    {
        return this.cycleTime;
    }

    public boolean isDefaultChain ()
    {
        return this.defaultChain;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( this.id == null ) ? 0 : this.id.hashCode () );
        return result;
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
        final ItemDefinition other = (ItemDefinition)obj;
        if ( this.id == null )
        {
            if ( other.id != null )
            {
                return false;
            }
        }
        else if ( !this.id.equals ( other.id ) )
        {
            return false;
        }
        return true;
    }

}
