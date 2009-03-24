package org.openscada.ae.storage;

import java.util.Collection;
import java.util.Properties;

import org.openscada.ae.core.QueryDescriptor;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;

public interface Storage
{
    public abstract Session createSession ( Properties props ) throws UnableToCreateSessionException;

    public abstract void closeSession ( Session session ) throws InvalidSessionException;

    public abstract Collection<QueryDescriptor> listQueries ( Session session ) throws InvalidSessionException;
}
