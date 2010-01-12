/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.spring;

import org.apache.log4j.Logger;
import org.openscada.da.server.common.DataItem;
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
    @SuppressWarnings ( "unused" )
    private static Logger _log = Logger.getLogger ( Hive.class );

    private ApplicationContext ctx;

    private boolean enableChainPersistenceService = true;

    private ChainStorageService chainPersistenceService;

    protected void setup ()
    {
        if ( enableChainPersistenceService )
        {
            ChainStorageServiceHelper.registerDefaultPropertyService ( this );
        }
        else if ( this.chainPersistenceService != null )
        {
            ChainStorageServiceHelper.registerService ( this, this.chainPersistenceService );
        }

        for ( String beanName : ctx.getBeanNamesForType ( DataItemValidator.class ) )
        {
            addDataItemValidator ( (DataItemValidator)ctx.getBean ( beanName ) );
        }

        for ( String beanName : ctx.getBeanNamesForType ( DataItemFactory.class ) )
        {
            addItemFactory ( (DataItemFactory)ctx.getBean ( beanName ) );
        }

        for ( String beanName : ctx.getBeanNamesForType ( FactoryTemplate.class ) )
        {
            registerTemplate ( (FactoryTemplate)ctx.getBean ( beanName ) );
        }

        for ( String beanName : ctx.getBeanNamesForType ( DataItem.class ) )
        {
            registerItem ( (DataItem)ctx.getBean ( beanName ) );
        }
    }

    public void afterPropertiesSet () throws Exception
    {
        setup ();
    }

    public DataItem findDataItem ( String itemId )
    {
        return findRegisteredDataItem ( itemId );
    }

    public void setApplicationContext ( ApplicationContext ctx ) throws BeansException
    {
        this.ctx = ctx;
    }

    public void setEnableChainPersistenceService ( boolean enableChainPersistenceService )
    {
        this.enableChainPersistenceService = enableChainPersistenceService;
    }

    public void setChainPersistenceService ( ChainStorageService chainPersistenceService )
    {
        this.chainPersistenceService = chainPersistenceService;
    }
}
