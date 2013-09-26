package org.eclipse.scada.ae.monitor.datasource.common;

import org.eclipse.scada.ae.data.Severity;

public enum ListSeverity
{
    OK ( null ),
    INFORMATION ( Severity.INFORMATION ),
    WARNING ( Severity.WARNING ),
    ALARM ( Severity.ALARM ),
    ERROR ( Severity.ERROR );

    private Severity severity;

    private ListSeverity ( final Severity severity )
    {
        this.severity = severity;
    }

    public Severity getSeverity ()
    {
        return this.severity;
    }
}