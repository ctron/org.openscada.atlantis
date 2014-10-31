/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.opc.job;

/**
 * Handle feedback from the guardian
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 *
 */
public interface GuardianHandler
{
    /**
     * the guardian detected a timeout condition and needs to cancel
     * the operation it guards. this must be implemented by the user of
     * the guardian in order to cancel the job running.
     */
    public abstract void performCancel ();
}
