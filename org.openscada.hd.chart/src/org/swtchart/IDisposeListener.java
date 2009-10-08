package org.swtchart;

import org.eclipse.swt.widgets.Event;

/**
 * The dispose listener.
 */
public interface IDisposeListener
{

    /**
     * The method to be invoked when the target is disposed.
     * 
     * @param e
     *            the event
     */
    void disposed ( Event e );
}