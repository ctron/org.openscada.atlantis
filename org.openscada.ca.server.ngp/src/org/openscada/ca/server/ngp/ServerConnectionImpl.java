/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.ca.server.ngp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.mina.core.session.IoSession;
import org.openscada.ca.Configuration;
import org.openscada.ca.Factory;
import org.openscada.ca.data.ConfigurationInformation;
import org.openscada.ca.data.FactoryInformation;
import org.openscada.ca.data.message.ApplyDiffRequest;
import org.openscada.ca.data.message.ApplyDiffResponse;
import org.openscada.ca.data.message.ErrorResponse;
import org.openscada.ca.data.message.GetConfigurationRequest;
import org.openscada.ca.data.message.GetConfigurationResponse;
import org.openscada.ca.data.message.GetFactoriesRequest;
import org.openscada.ca.data.message.GetFactoriesResponse;
import org.openscada.ca.data.message.GetFactoryWithDataRequest;
import org.openscada.ca.data.message.GetFactoryWithDataResponse;
import org.openscada.ca.server.FactoryWithData;
import org.openscada.ca.server.Service;
import org.openscada.ca.server.Session;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.data.ErrorInformation;
import org.openscada.core.data.Request;
import org.openscada.core.data.Response;
import org.openscada.core.data.ResponseMessage;
import org.openscada.core.server.ngp.ServiceServerConnection;
import org.openscada.utils.ExceptionHelper;
import org.openscada.utils.concurrent.FutureListener;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerConnectionImpl extends ServiceServerConnection<Session, Service>
{

    private final static Logger logger = LoggerFactory.getLogger ( ServerConnectionImpl.class );

    private abstract class ErrorAwareHandler<M> implements FutureListener<M>
    {
        private final Request request;

        public ErrorAwareHandler ( final Request request, final NotifyFuture<M> future )
        {
            this.request = request;
            future.addListener ( this );
        }

        @Override
        public void complete ( final Future<M> future )
        {
            logger.debug ( "Request completed" );

            try
            {
                final M result = future.get ();
                sendMessage ( handle ( new Response ( this.request ), result ) );
            }
            catch ( final Exception e )
            {
                logger.info ( "Request failure", e );
                sendMessage ( new ErrorResponse ( new Response ( this.request ), makeErrorInformation ( e ) ) );
            }
        }

        protected abstract ResponseMessage handle ( Response response, M result );

        private ErrorInformation makeErrorInformation ( final Exception e )
        {
            return new ErrorInformation ( null, e.getMessage (), ExceptionHelper.formatted ( e ) );
        }
    }

    public ServerConnectionImpl ( final IoSession session, final Service service )
    {
        super ( session, service );
    }

    @Override
    public void messageReceived ( final Object message ) throws Exception
    {
        logger.trace ( "Received message: {}", message );

        if ( message instanceof GetFactoriesRequest )
        {
            handleGetFactories ( (GetFactoriesRequest)message );
        }
        else if ( message instanceof GetFactoryWithDataRequest )
        {
            handleGetFactoryWithData ( (GetFactoryWithDataRequest)message );
        }
        else if ( message instanceof GetConfigurationRequest )
        {
            handleGetConfigurationRequest ( (GetConfigurationRequest)message );
        }
        else if ( message instanceof ApplyDiffRequest )
        {
            handleApplyDiff ( (ApplyDiffRequest)message );
        }
        else
        {
            super.messageReceived ( message );
        }
    }

    protected void handleApplyDiff ( final ApplyDiffRequest message ) throws InvalidSessionException
    {
        new ErrorAwareHandler<Void> ( message.getRequest (), this.service.applyDiff ( this.session, message.getDiffs (), message.getOperationParameters (), createCallbackHandler ( message.getCallbackHandlerId () ) ) ) {
            @Override
            protected ResponseMessage handle ( final Response response, final Void result )
            {
                return new ApplyDiffResponse ( new Response ( message.getRequest () ) );
            }
        };
    }

    protected void handleGetConfigurationRequest ( final GetConfigurationRequest message ) throws InvalidSessionException
    {
        new ErrorAwareHandler<Configuration> ( message.getRequest (), this.service.getConfiguration ( this.session, message.getFactoryId (), message.getConfigurationId () ) ) {
            @Override
            protected ResponseMessage handle ( final Response response, final Configuration result )
            {
                return new GetConfigurationResponse ( new Response ( message.getRequest () ), convertConfiguration ( result ) );
            }
        };
    }

    protected void handleGetFactoryWithData ( final GetFactoryWithDataRequest message ) throws InvalidSessionException
    {
        new ErrorAwareHandler<FactoryWithData> ( message.getRequest (), this.service.getFactory ( this.session, message.getFactoryId () ) ) {
            @Override
            protected ResponseMessage handle ( final Response response, final FactoryWithData result )
            {
                return new GetFactoryWithDataResponse ( new Response ( message.getRequest () ), convertFactory ( result ) );
            }
        };
    }

    protected void handleGetFactories ( final GetFactoriesRequest message ) throws InvalidSessionException
    {
        new ErrorAwareHandler<Factory[]> ( message.getRequest (), this.service.getKnownFactories ( this.session ) ) {
            @Override
            protected ResponseMessage handle ( final Response response, final Factory[] result )
            {
                return new GetFactoriesResponse ( new Response ( message.getRequest () ), convertFactories ( result ) );
            }
        };
    }

    protected FactoryInformation convertFactory ( final FactoryWithData result )
    {
        final Factory factory = result.getFactory ();
        final Configuration[] configurations = result.getConfigurations ();
        return new FactoryInformation ( factory.getId (), factory.getDescription (), factory.getState (), convertConfigurations ( configurations ) );
    }

    private List<ConfigurationInformation> convertConfigurations ( final Configuration[] configurations )
    {
        if ( configurations == null )
        {
            return null;
        }

        final List<ConfigurationInformation> result = new ArrayList<ConfigurationInformation> ( configurations.length );

        for ( final Configuration configuration : configurations )
        {
            result.add ( convertConfiguration ( configuration ) );
        }
        return result;
    }

    private ConfigurationInformation convertConfiguration ( final Configuration configuration )
    {
        return new ConfigurationInformation ( configuration.getFactoryId (), configuration.getId (), configuration.getState (), configuration.getData (), ExceptionHelper.formatted ( configuration.getErrorInformation () ) );
    }

    protected List<FactoryInformation> convertFactories ( final Factory[] factories )
    {
        if ( factories == null )
        {
            return null;
        }

        final List<FactoryInformation> result = new ArrayList<FactoryInformation> ( factories.length );

        for ( final Factory factory : factories )
        {
            result.add ( new FactoryInformation ( factory.getId (), factory.getDescription (), factory.getState (), Collections.<ConfigurationInformation> emptyList () ) );
        }
        return result;
    }

}
