package org.openscada.da.server.common.chain.item;

import org.openscada.da.core.IODirection;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.chain.DataItemBaseChained;

/**
 * A default chain creator 
 * @author Jens Reimann
 *
 */
public class ChainCreator
{
    public static void applyDefaultInputChain ( final DataItemBaseChained item, final HiveServiceRegistry serviceRegistry )
    {
        item.addChainElement ( IODirection.INPUT, new NegateInputItem ( serviceRegistry ) );
        item.addChainElement ( IODirection.INPUT, new ScaleInputItem ( serviceRegistry ) );
        item.addChainElement ( IODirection.INPUT, new ManualOverrideChainItem ( serviceRegistry ) );
        item.addChainElement ( IODirection.INPUT, new LevelAlarmChainItem ( serviceRegistry ) );
        item.addChainElement ( IODirection.INPUT, new SumAlarmChainItem ( serviceRegistry ) );
        item.addChainElement ( IODirection.INPUT, new SumErrorChainItem ( serviceRegistry ) );
        item.addChainElement ( IODirection.INPUT, new ManualErrorOverrideChainItem () );
    }
}
