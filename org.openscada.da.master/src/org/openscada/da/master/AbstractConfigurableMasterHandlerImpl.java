package org.openscada.da.master;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.util.tracker.ServiceTracker;

public abstract class AbstractConfigurableMasterHandlerImpl extends AbstractMasterHandlerImpl
{

    private final String prefix;

    private final ServiceTracker tracker;

    private final String factoryId;

    private final String configurationId;

    public AbstractConfigurableMasterHandlerImpl ( final String configurationId, final ObjectPoolTracker poolTracker, final int priority, final ServiceTracker caTracker, final String prefix, final String factoryId )
    {
        super ( poolTracker, priority );
        this.configurationId = configurationId;
        this.tracker = caTracker;
        this.prefix = prefix + ".";
        this.factoryId = factoryId;
    }

    protected String getPrefixed ( final String id )
    {
        return this.prefix + id;
    }

    @Override
    public WriteRequestResult processWrite ( final WriteRequest request )
    {
        if ( request.getAttributes () == null )
        {
            return null;
        }

        // extract our prefixed attributes
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        for ( final Map.Entry<String, Variant> entry : request.getAttributes ().entrySet () )
        {
            final String key = entry.getKey ();
            if ( key.startsWith ( this.prefix ) )
            {
                attributes.put ( key.substring ( this.prefix.length () ), entry.getValue () );
            }
        }

        if ( attributes.isEmpty () )
        {
            // we have nothing to do
            return null;
        }

        try
        {
            final WriteAttributeResults result = handleUpdate ( request.getWriteInformation (), Collections.unmodifiableMap ( attributes ) );

            // remove processed attributes
            for ( final String attr : result.keySet () )
            {
                attributes.remove ( attr );
            }

            for ( final String attr : attributes.keySet () )
            {
                result.put ( attr, new WriteAttributeResult ( new OperationException ( String.format ( "Attribute '%s' is not supported", attr ) ) ) );
            }

            return new WriteRequestResult ( request.getValue (), request.getAttributes (), result );
        }
        catch ( final Throwable e )
        {
            return new WriteRequestResult ( e );
        }
    }

    /**
     * This method will be called on write request that have attributes which match our prefix.
     * @param writeInformation the write information of the write request
     * @param attributes the filtered attributes that match our prefix 
     * @return the attribute result of the written attributes
     * @throws Exception if anything goes wrong
     */
    protected abstract WriteAttributeResults handleUpdate ( final WriteInformation writeInformation, final Map<String, Variant> attributes ) throws Exception;

    protected WriteAttributeResults updateConfiguration ( final Map<String, String> data, final boolean fullSet ) throws OperationException
    {
        final WriteAttributeResults result = new WriteAttributeResults ();

        if ( data.isEmpty () )
        {
            return result;
        }

        final Object service = this.tracker.getService ();
        if ( ! ( service instanceof ConfigurationAdministrator ) )
        {
            final OperationException error = new OperationException ( "Configuration administrator not available" );
            for ( final String attr : data.keySet () )
            {
                result.put ( attr, new WriteAttributeResult ( error ) );
            }
            return result;
        }
        else
        {
            for ( final String attr : data.keySet () )
            {
                result.put ( this.prefix + attr, WriteAttributeResult.OK );
            }

            final ConfigurationAdministrator admin = (ConfigurationAdministrator)service;
            admin.updateConfiguration ( this.factoryId, this.configurationId, data, fullSet );

            return result;
        }
    }
}
