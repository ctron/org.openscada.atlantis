package org.openscada.da.client.signalgenerator;

import org.openscada.da.client.signalgenerator.page.GeneratorPage;

public class GeneratorPageInformation
{
    private GeneratorPage generatorPage;

    private String sortKey;

    private String label;

    public GeneratorPage getGeneratorPage ()
    {
        return this.generatorPage;
    }

    public void setGeneratorPage ( final GeneratorPage generatorPage )
    {
        this.generatorPage = generatorPage;
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
