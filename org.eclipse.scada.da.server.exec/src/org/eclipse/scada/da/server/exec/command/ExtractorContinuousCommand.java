/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.da.server.exec.command;

import java.util.Collection;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.da.server.common.chain.DataItemInputChained;
import org.eclipse.scada.da.server.exec.Hive;
import org.eclipse.scada.da.server.exec.extractor.Extractor;
import org.eclipse.scada.da.server.exec.splitter.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtractorContinuousCommand extends AbstractContinuousCommand
{
    private final static Logger logger = LoggerFactory.getLogger ( ExtractorContinuousCommand.class );

    private int currentLineCount;

    private final Collection<Extractor> extrators;

    private final int ignoreStartLines;

    private DataItemInputChained lastInput;

    public ExtractorContinuousCommand ( final String id, final ProcessConfiguration processConfiguration, final int restartDelay, final int maxInputBuffer, final int ignoreStartLines, final Splitter splitter, final Collection<Extractor> extractors )
    {
        super ( id, processConfiguration, restartDelay, maxInputBuffer, splitter );
        this.extrators = extractors;
        this.ignoreStartLines = ignoreStartLines;
    }

    @Override
    public void start ( final Hive hive, final FolderCommon parentFolder )
    {
        super.start ( hive, parentFolder );

        this.lastInput = this.itemFactory.createInput ( "lastInput", null );

        for ( final Extractor extractor : this.extrators )
        {
            extractor.register ( hive, this.itemFactory );
        }
    }

    @Override
    public void stop ()
    {
        for ( final Extractor extractor : this.extrators )
        {
            extractor.unregister ();
        }

        super.stop ();
    }

    @Override
    protected void processFailed ( final Throwable e )
    {
        super.processFailed ( e );
        final ExecutionResult result = new ExecutionResult ();
        result.setExecutionError ( new RuntimeException ( "Process failed", e ) );
        for ( final Extractor extractor : this.extrators )
        {
            extractor.process ( result );
        }

    }

    @Override
    protected void handleStdLine ( final String line )
    {
        logger.debug ( "Got line: " + line );
        this.lastInput.updateData ( Variant.valueOf ( line ), null, null );

        this.currentLineCount++;
        if ( this.currentLineCount > this.ignoreStartLines )
        {
            final ExecutionResult result = new ExecutionResult ();
            result.setOutput ( line );
            for ( final Extractor extractor : this.extrators )
            {
                extractor.process ( result );
            }
        }
    }

    @Override
    protected void processStarted ( final Process process )
    {
        this.currentLineCount = 0;
        super.processStarted ( process );
    }

}
