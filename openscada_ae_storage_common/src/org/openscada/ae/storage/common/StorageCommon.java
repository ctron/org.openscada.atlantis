package org.openscada.ae.storage.common;

import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArraySet;

import org.openscada.ae.storage.Session;
import org.openscada.ae.storage.Storage;
import org.openscada.core.InvalidSessionException;

public abstract class StorageCommon implements Storage
{

    protected Collection<SessionCommon> sessions = new CopyOnWriteArraySet<SessionCommon> ();

    protected SessionCommon performCreateSession ( final Properties props )
    {
        return new SessionCommon ( this );
    }

    protected void registerSession ( final SessionCommon session )
    {
        this.sessions.add ( session );
    }

    protected void unregisterSession ( final SessionCommon session )
    {
        this.sessions.remove ( session );
        session.dispose ();
    }

    protected SessionCommon validateSession ( final Session session ) throws InvalidSessionException
    {
        // check class instance
        if ( ! ( session instanceof SessionCommon ) )
        {
            throw new InvalidSessionException ();
        }

        // check session membership
        if ( !this.sessions.contains ( session ) )
        {
            throw new InvalidSessionException ();
        }

        // return new session
        return (SessionCommon)session;
    }

    public Session createSession ( final Properties props )
    {
        final SessionCommon session = performCreateSession ( props );
        registerSession ( session );
        return session;
    }

    public void closeSession ( final Session session ) throws InvalidSessionException
    {
        final SessionCommon sessionCommon = validateSession ( session );
        unregisterSession ( sessionCommon );
    }
}
