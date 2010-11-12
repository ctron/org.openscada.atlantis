/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.spring;

import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.ValidationStrategy;
import org.openscada.da.server.common.chain.storage.ChainStorageService;
import org.openscada.da.server.common.chain.storage.ChainStorageServiceHelper;
import org.openscada.da.server.common.factory.DataItemFactory;
import org.openscada.da.server.common.factory.DataItemValidator;
import org.openscada.da.server.common.factory.FactoryTemplate;
import org.openscada.da.server.common.impl.HiveCommon;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class Hive extends HiveCommon implements InitializingBean, ApplicationContextAware
{
    private ApplicationContext ctx;

    private boolean enableChainPersistenceService = true;

    private ChainStorageService chainPersistenceService;

    private ValidationStrategy validationStrategy = ValidationStrategy.GRANT_ALL;

    protected void setup ()
    {
        setValidatonStrategy ( this.validationStrategy );

        if ( this.enableChainPersistenceService )
        {
            ChainStorageServiceHelper.registerDefaultPropertyService ( this );
        }
        else if ( this.chainPersistenceService != null )
        {
            ChainStorageServiceHelper.registerService ( this, this.chainPersistenceService );
        }

        for ( final String beanName : this.ctx.getBeanNamesForType ( DataItemValidator.class ) )
        {
            addDataItemValidator ( (DataItemValidator)this.ctx.getBean ( beanName ) );
        }

        for ( final String beanName : this.ctx.getBeanNamesForType ( DataItemFactory.class ) )
        {
            addItemFactory ( (DataItemFactory)this.ctx.getBean ( beanName ) );
        }

        for ( final String beanName : this.ctx.getBeanNamesForType ( FactoryTemplate.class ) )
        {
            registerTemplate ( (FactoryTemplate)this.ctx.getBean ( beanName ) );
        }

        for ( final String beanName : this.ctx.getBeanNamesForType ( DataItem.class ) )
        {
            registerItem ( (DataItem)this.ctx.getBean ( beanName ) );
        }
    }

    public void afterPropertiesSet () throws Exception
    {
        setup ();
    }

    public DataItem findDataItem ( final String itemId )
    {
        return findRegisteredDataItem ( itemId );
    }

    public void setApplicationContext ( final ApplicationContext ctx ) throws BeansException
    {
        this.ctx = ctx;
    }

    public void setEnableChainPersistenceService ( final boolean enableChainPersistenceService )
    {
        this.enableChainPersistenceService = enableChainPersistenceService;
    }

    public void setChainPersistenceService ( final ChainStorageService chainPersistenceService )
    {
        this.chainPersistenceService = chainPersistenceService;
    }

    public void setValidationStrategy ( final ValidationStrategy validationStrategy )
    {
        this.validationStrategy = validationStrategy;
    }
}
