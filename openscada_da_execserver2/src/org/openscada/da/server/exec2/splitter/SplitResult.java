package org.openscada.da.server.exec2.splitter;

public class SplitResult
{
    private String remainingBuffer;

    private String[] lines = new String[0];

    public String getRemainingBuffer ()
    {
        return this.remainingBuffer;
    }

    public void setRemainingBuffer ( final String remainingBuffer )
    {
        this.remainingBuffer = remainingBuffer;
    }

    public String[] getLines ()
    {
        return this.lines;
    }

    public void setLines ( final String[] lines )
    {
        this.lines = lines;
    }

}
