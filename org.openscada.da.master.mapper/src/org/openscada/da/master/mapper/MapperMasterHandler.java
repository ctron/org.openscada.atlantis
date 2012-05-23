package org.openscada.da.master.mapper;

import java.util.Dictionary;
import java.util.Map;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.mapper.ValueMapper;
import org.openscada.da.mapper.ValueMapperListener;
import org.openscada.da.master.AbstractMasterHandlerImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.openscada.utils.osgi.pool.SingleObjectPoolServiceTracker;
import org.openscada.utils.osgi.pool.SingleObjectPoolServiceTracker.ServiceListener;

public class MapperMasterHandler extends AbstractMasterHandlerImpl implements ValueMapperListener
{

    private String sourceAttributeName;

    private String targetAttributeName;

    private final ObjectPoolTracker mapperPoolTracker;

    private volatile ValueMapper mapper;

    private SingleObjectPoolServiceTracker mapperTracker;

    public MapperMasterHandler ( final ObjectPoolTracker poolTracker, final ObjectPoolTracker mapperPoolTracker, final int defaultPriority )
    {
        super ( poolTracker, defaultPriority );
        this.mapperPoolTracker = mapperPoolTracker;
    }

    @Override
    public synchronized void update ( final org.openscada.sec.UserInformation userInformation, final java.util.Map<String, String> parameters ) throws Exception
    {
        if ( this.mapperTracker != null )
        {
            this.mapperTracker.close ();
            this.mapperTracker = null;
        }

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        this.mapperTracker = new SingleObjectPoolServiceTracker ( this.mapperPoolTracker, cfg.getStringChecked ( "mapperId", "'mapperId' must be specified" ), new ServiceListener () {

            @Override
            public void serviceChange ( final Object service, final Dictionary<?, ?> properties )
            {
                setMapper ( (ValueMapper)service );
            }
        } );

        this.sourceAttributeName = cfg.getString ( "sourceAttributeName" );
        this.targetAttributeName = cfg.getString ( "targetAttributeName" );

        this.mapperTracker.open ();

        reprocess ();
    };

    protected void setMapper ( final ValueMapper mapper )
    {
        if ( this.mapper != null )
        {
            this.mapper.removeListener ( this );
        }
        this.mapper = mapper;
        if ( this.mapper != null )
        {
            this.mapper.addListener ( this );
        }
        reprocess ();
    }

    @Override
    public DataItemValue dataUpdate ( final Map<String, Object> context, final DataItemValue value )
    {
        Variant sourceValue;
        if ( this.sourceAttributeName == null || this.sourceAttributeName.isEmpty () )
        {
            sourceValue = value.getAttributes ().get ( this.sourceAttributeName );
        }
        else
        {
            sourceValue = value.getValue ();
        }
        final ValueMapper mapper = getMapper ();
        if ( mapper != null )
        {
            final Builder builder = new DataItemValue.Builder ( value );
            if ( this.targetAttributeName == null || this.targetAttributeName.isEmpty () )
            {
                builder.setValue ( mapper.mapValue ( sourceValue ) );
            }
            else
            {
                builder.setAttribute ( this.targetAttributeName, mapper.mapValue ( sourceValue ) );
            }
            return builder.build ();
        }
        else
        {
            return null;
        }
    }

    protected ValueMapper getMapper ()
    {
        return this.mapper;
    }

    @Override
    public void stateChanged ()
    {
        reprocess ();
    }

}
