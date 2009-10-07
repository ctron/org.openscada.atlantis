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
    private final static String ROOT = "va_base_test";

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
        backEnd.initialize ( metaData );
        backEnd.delete ();
        backEnd.create ( metaData );
        return backEnd;
    }

    /**
     * This method cleans the root directory.
     */
    private void cleanDirectory ()
    {
        if ( PERFORM_CLEANUP )
        {
            if ( ( ROOT != null ) && ( ROOT.length () > 0 ) )
            {
                deleteDirectory ( new File ( ROOT ) );
            }
        }
    }

    /**
     * This method deletes the passed directory or file.
     * In case of a directory, all subdirectories and files will also be deleted.
     * @param file or directory that has to be deleted
     */
    private void deleteDirectory ( final File file )
    {
        if ( file != null )
        {
            if ( file.isDirectory () )
            {
                for ( File subDir : file.listFiles () )
                {
                    deleteDirectory ( subDir );
                }
            }
            file.delete ();
        }
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
            cleanDirectory ();
        }
    }
}
