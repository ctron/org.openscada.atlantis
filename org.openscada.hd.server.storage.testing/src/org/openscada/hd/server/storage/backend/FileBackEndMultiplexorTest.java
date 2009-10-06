package org.openscada.hd.server.storage.backend;

import java.io.File;

import org.openscada.hd.server.storage.StorageChannelMetaData;

/**
 * Test class for following classes:
 * org.openscada.hd.server.storage.backend.FileBackEndFactory.
 * org.openscada.hd.server.storage.backend.BackEndMultiplexor.
 * @author Ludwig Straub
 */
public class FileBackEndMultiplexorTest extends BackEndTestBase
{
    /** Base directory for test files. */
    private final static String ROOT = "we_base";

    /**
     * This method creates, initializes and returns the backend that has to be tested.
     * If a backend with the same meta data already exists, the old back end will be deleted.
     * @param metaData metadata that should be used when creating a back end
     * @return backend that has to be tested
     * @throws Exception in case of problems
     */
    @Override
    protected BackEnd createBackEnd ( StorageChannelMetaData metaData ) throws Exception
    {
        BackEnd backEnd = new BackEndMultiplexor ( new FileBackEndFactory ( ROOT ), 50 );
        backEnd.delete ();
        backEnd.create ( metaData );
        backEnd.initialize ( metaData );
        backEnd.delete ();
        return backEnd;
    }

    /**
     * This method cleans all artefacts that have been created during a test run.
     * @throws Exception in case of problems
     */
    @Override
    public void cleanup () throws Exception
    {
        if ( backEnd != null )
        {
            super.cleanup ();
            if ( ( ROOT != null ) && ( ROOT.length () > 0 ) )
            {
                File root = new File ( ROOT );
                for ( File subDir : root.listFiles () )
                {
                    subDir.delete ();
                }
                root.delete ();
            }
        }
    }
}
