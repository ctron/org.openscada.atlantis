package org.openscada.hd.server.storage.backend;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.openscada.hd.server.storage.backend.FileBackEndMultiplexorTest;
import org.openscada.hd.server.storage.backend.FileBackEndTest;

/**
 * This class can be used as test entry point to perform all available tests for service classes.
 * @author Ludwig Straub
 */
@RunWith ( Suite.class )
@Suite.SuiteClasses ( { FileBackEndTest.class, FileBackEndMultiplexorTest.class } )
public class StorageBackEndTestSuite
{
}
