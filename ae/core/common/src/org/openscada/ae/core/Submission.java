package org.openscada.ae.core;

import java.util.Properties;

public interface Submission
{

    public void submitEvent ( Properties properties, Event event ) throws Exception;

}
