package org.openscada.hd.server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.openscada.hd.server.storage.backend.StorageBackEndTestSuite;

/**
 * This class can be used as test entry point to perform all available tests for service classes.
 * @author Ludwig Straub
 */
@RunWith ( Suite.class )
@Suite.SuiteClasses ( { StorageBackEndTestSuite.class } )
public class TestSuite
{
}
