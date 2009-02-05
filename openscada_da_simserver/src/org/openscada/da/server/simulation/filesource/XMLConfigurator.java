package org.openscada.da.server.simulation.filesource;

import java.io.File;

import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.simulation.configuration.RootDocument;

public class XMLConfigurator
{
    private final RootDocument document;

    public XMLConfigurator ( final RootDocument document )
    {
        this.document = document;
    }

    public void configure ( final Hive hive ) throws Exception
    {
        String name = null;
        String filename = null;
        String filenameJs = null;
        if ( this.document.getRoot ().getExcelConfig () != null )
        {
            name = this.document.getRoot ().getExcelConfig ().getName ();
            filename = this.document.getRoot ().getExcelConfig ().getInputFile ();
            filenameJs = this.document.getRoot ().getExcelConfig ().getJsFile ();
            final File fileJs = filenameJs == null ? null : new File ( filenameJs );
            hive.setFactory ( new FolderItemFactory ( hive, hive.getRootFolder (), name, name ) );
            final HiveBuilder hb = HiveBuilder.create ( name );
            new ExcelFile ( new File ( filename ), fileJs, hb ).configureHive ();
            hb.configureHive ( hive );
        }
        else if ( this.document.getRoot ().getOpenOfficeConfig () != null )
        {
            name = this.document.getRoot ().getOpenOfficeConfig ().getName ();
            filename = this.document.getRoot ().getOpenOfficeConfig ().getInputFile ();
            filenameJs = this.document.getRoot ().getOpenOfficeConfig ().getJsFile ();
            final File fileJs = filenameJs == null ? null : new File ( filenameJs );
            hive.setFactory ( new FolderItemFactory ( hive, hive.getRootFolder (), name, name ) );
            final HiveBuilder hb = HiveBuilder.create ( name );
            new OpenOfficeFile ( new File ( filename ), fileJs, hb ).configureHive ();
            hb.configureHive ( hive );
        }
    }
}
