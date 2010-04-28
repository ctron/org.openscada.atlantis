package org.openscada.da.server.opc.connection;

import org.openscada.opc.dcom.da.OPCGroupState;
import org.openscada.utils.beans.AbstractPropertyChange;

public class GroupState extends AbstractPropertyChange
{
    private final static String PROP_CONNECTED = "connected";

    private final static String PROP_UPDATE_RATE = "updateRate";

    private final static String PROP_PERCENT_DEADBAND = "percentDeadband";

    private final static String PROP_NAME = "name";

    private final static String PROP_LOCALE_ID = "localeId";

    private boolean connected;

    private Integer updateRate;

    private Float percentDeadband;

    private String name;

    private Integer localeId;

    public boolean isConnected ()
    {
        return this.connected;
    }

    protected void setConnected ( final boolean connected )
    {
        final boolean oldConnected = this.connected;
        this.connected = connected;
        firePropertyChange ( PROP_CONNECTED, oldConnected, connected );
    }

    public Integer getUpdateRate ()
    {
        return this.updateRate;
    }

    protected void setUpdateRate ( final Integer updateRate )
    {
        final Integer oldUpdateRate = this.updateRate;
        this.updateRate = updateRate;
        firePropertyChange ( PROP_UPDATE_RATE, oldUpdateRate, updateRate );
    }

    public Float getPercentDeadband ()
    {
        return this.percentDeadband;
    }

    protected void setPercentDeadband ( final Float percentDeadband )
    {
        final Float oldPercentDeadband = this.percentDeadband;
        this.percentDeadband = percentDeadband;
        firePropertyChange ( PROP_PERCENT_DEADBAND, oldPercentDeadband, percentDeadband );
    }

    public String getName ()
    {
        return this.name;
    }

    protected void setName ( final String name )
    {
        final String oldName = this.name;
        this.name = name;
        firePropertyChange ( PROP_NAME, oldName, name );
    }

    public Integer getLocaleId ()
    {
        return this.localeId;
    }

    protected void setLocaleId ( final Integer localeId )
    {
        final Integer oldLocaleId = this.localeId;
        this.localeId = localeId;
        firePropertyChange ( PROP_LOCALE_ID, oldLocaleId, localeId );
    }

    public void update ( final OPCGroupState state )
    {
        if ( state == null )
        {
            setConnected ( false );
            setUpdateRate ( null );
            setPercentDeadband ( null );
            setName ( null );
            setLocaleId ( null );
            return;
        }

        setConnected ( true );
        setUpdateRate ( state.getUpdateRate () );
        setPercentDeadband ( state.getPercentDeadband () );
        setName ( state.getName () );
        setLocaleId ( state.getLocaleID () );
    }
}
