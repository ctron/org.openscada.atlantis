package org.openscada.spring.server;

import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.net.Exporter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class NetExporter implements InitializingBean, DisposableBean
{

    private Hive hive;

    private Exporter exporter;

    private String connectionString;

    public void setConnectionString ( final String connectionString )
    {
        this.connectionString = connectionString;
    }

    public void setHive ( final Hive hive )
    {
        this.hive = hive;
    }

    @Override
    public void afterPropertiesSet () throws Exception
    {
        Assert.notNull ( this.hive, "'hive' must be set" );
        Assert.hasText ( this.connectionString, "'connectionString' must be set" );

        this.exporter = new Exporter ( this.hive, ConnectionInformation.fromURI ( this.connectionString ) );
        this.exporter.start ();
    }

    @Override
    public void destroy () throws Exception
    {
        this.exporter.stop ();
        this.exporter = null;
    }

}
