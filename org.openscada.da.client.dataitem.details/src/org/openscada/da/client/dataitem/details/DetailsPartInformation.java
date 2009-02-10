package org.openscada.da.client.dataitem.details;

import org.openscada.da.client.dataitem.details.part.DetailsPart;

public class DetailsPartInformation
{
    private DetailsPart detailsPart;

    private String sortKey;

    private String label;

    public DetailsPart getDetailsPart ()
    {
        return this.detailsPart;
    }

    public void setDetailsPart ( final DetailsPart detailsPart )
    {
        this.detailsPart = detailsPart;
    }

    public String getSortKey ()
    {
        return this.sortKey;
    }

    public void setSortKey ( final String sortKey )
    {
        this.sortKey = sortKey;
    }

    public String getLabel ()
    {
        return this.label;
    }

    public void setLabel ( final String label )
    {
        this.label = label;
    }

}
