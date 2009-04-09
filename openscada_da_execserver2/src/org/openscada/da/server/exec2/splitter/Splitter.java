package org.openscada.da.server.exec2.splitter;

public interface Splitter
{
    /**
     * Split of lines from the buffer
     * @param inputBuffer the input buffer to split
     * @return a result or <code>null</code> if there was nothing to split off
     */
    public SplitResult split ( String inputBuffer );
}
