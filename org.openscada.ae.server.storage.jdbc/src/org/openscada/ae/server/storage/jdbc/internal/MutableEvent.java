package org.openscada.ae.server.storage.jdbc.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.openscada.ae.Event;
import org.openscada.core.Variant;

/**
 * just used by hibernate. All other communication with other systems should
 * only happen through the immutable Event class
 * 
 * @author jrose
 */
public class MutableEvent
{
    private static final int DEFAULT_PRIORITY = 50;

    public enum Fields
    {
        MONITOR_TYPE ( "monitorType", String.class ),
        EVENT_TYPE ( "eventType", String.class ),
        VALUE ( "value", Variant.class ),
        MESSAGE ( "message", String.class ),
        MESSAGE_CODE ( "messageSource", String.class ),
        PRIORITY ( "priority", Integer.class ),
        SOURCE ( "source", String.class ),
        ACTOR_NAME ( "actorName", String.class ),
        ACTOR_TYPE ( "actorType", String.class );

        private final Class<? extends Object> clazz;

        private final String name;

        Fields ( final String name, final Class<? extends Object> clazz )
        {
            this.name = name;
            this.clazz = clazz;
        }

        public Class<? extends Object> getType ()
        {
            return this.clazz;
        }

        public String getName ()
        {
            return this.name;
        }

        public boolean contains ( final String name )
        {
            return byField ( name ) == null ? false : true;
        }

        public Fields byField ( final String name )
        {
            for ( final Fields field : values () )
            {
                if ( field.getName ().equals ( name ) )
                {
                    return field;
                }
            }
            return null;
        }
    }

    private UUID id;

    private Date sourceTimestamp;

    private Date entryTimestamp;

    // often used fields

    private String monitorType = "";

    private String eventType = "";

    private Variant value = new Variant ();

    private String message = "";

    private String messageCode = "";

    private Integer priority = 50;

    private String source = "";

    private String actorName = "";

    private String actorType = "";

    public String getMonitorType ()
    {
        return monitorType;
    }

    public void setMonitorType ( String monitorType )
    {
        this.monitorType = monitorType;
    }

    public String getEventType ()
    {
        return eventType;
    }

    public void setEventType ( String eventType )
    {
        this.eventType = eventType;
    }

    public Variant getValue ()
    {
        return value;
    }

    public void setValue ( Variant value )
    {
        this.value = value;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage ( String message )
    {
        this.message = message;
    }

    public String getMessageCode ()
    {
        return messageCode;
    }

    public void setMessageCode ( String messageCode )
    {
        this.messageCode = messageCode;
    }

    public String getActorName ()
    {
        return actorName;
    }

    public void setActorName ( String actorName )
    {
        this.actorName = actorName;
    }

    public String getActorType ()
    {
        return actorType;
    }

    public void setActorType ( String actorType )
    {
        this.actorType = actorType;
    }

    public void setAttributes ( Map<String, Variant> attributes )
    {
        this.attributes = attributes;
    }

    // all other fields

    private Map<String, Variant> attributes = new HashMap<String, Variant> ( 8 );

    public UUID getId ()
    {
        return id;
    }

    public void setId ( UUID id )
    {
        this.id = id;
    }

    public Date getSourceTimestamp ()
    {
        return sourceTimestamp;
    }

    public void setSourceTimestamp ( Date sourceTimestamp )
    {
        this.sourceTimestamp = sourceTimestamp;
    }

    public Date getEntryTimestamp ()
    {
        return entryTimestamp;
    }

    public void setEntryTimestamp ( Date entryTimestamp )
    {
        this.entryTimestamp = entryTimestamp;
    }

    public String getType ()
    {
        return monitorType;
    }

    public void setType ( String type )
    {
        this.monitorType = type;
    }

    public String getSource ()
    {
        return source;
    }

    public void setSource ( String source )
    {
        this.source = source;
    }

    public Integer getPriority ()
    {
        return priority;
    }

    public void setPriority ( Integer priority )
    {
        this.priority = priority;
    }

    public Map<String, Variant> getAttributes ()
    {
        return attributes;
    }

    public static Event toEvent ( MutableEvent m )
    {
        Map<String, Variant> attr = new HashMap<String, Variant> ( m.getAttributes () );
        // often used fields
        attr.put ( "monitorType", new Variant ( m.monitorType ) );
        attr.put ( "eventType", new Variant ( m.eventType ) );
        attr.put ( "value", m.value );
        attr.put ( "message", new Variant ( m.message ) );
        attr.put ( "messageCode", new Variant ( m.messageCode ) );
        attr.put ( "priority", new Variant ( m.priority ) );
        attr.put ( "source", new Variant ( m.source ) );
        attr.put ( "actorName", new Variant ( m.actorName ) );
        attr.put ( "actorType", new Variant ( m.actorType ) );
        return Event.create ().id ( m.id ).sourceTimestamp ( m.sourceTimestamp ).entryTimestamp ( m.entryTimestamp ).attributes ( attr ).build ();
    }

    public static MutableEvent fromEvent ( Event e )
    {
        MutableEvent m = new MutableEvent ();
        // important fields
        m.setId ( e.getId () );
        m.setSourceTimestamp ( e.getSourceTimestamp () );
        m.setEntryTimestamp ( e.getEntryTimestamp () );
        // often used fields
        Map<String, Variant> attr = new HashMap<String, Variant> ( e.getAttributes () );
        Variant v;
        v = attr.remove ( Fields.MONITOR_TYPE.getName () );
        m.setMonitorType ( v == null ? "" : v.asString ( "" ) );
        v = attr.remove ( Fields.EVENT_TYPE.getName () );
        m.setEventType ( v == null ? "" : v.asString ( "" ) );
        m.setValue ( attr.remove ( Fields.VALUE.getName () ) );
        v = attr.remove ( Fields.MESSAGE.getName () );
        m.setMessage ( v == null ? "" : v.asString ( "" ) );
        v = attr.remove ( Fields.MESSAGE_CODE.getName () );
        m.setMessageCode ( v == null ? "" : v.asString ( "" ) );
        v = attr.remove ( Fields.PRIORITY.getName () );
        m.setPriority ( v == null ? DEFAULT_PRIORITY : v.asInteger ( DEFAULT_PRIORITY ) );
        v = attr.remove ( Fields.SOURCE.getName () );
        m.setSource ( v == null ? "" : v.asString ( "" ) );
        v = attr.remove ( Fields.ACTOR_NAME.getName () );
        m.setActorName ( v == null ? "" : v.asString ( "" ) );
        v = attr.remove ( Fields.ACTOR_TYPE.getName () );
        m.setActorType ( v == null ? "" : v.asString ( "" ) );
        // all other
        m.getAttributes ().putAll ( attr );
        return m;
    }

    @Override
    public String toString ()
    {
        return toEvent ( this ).toString ();
    }
}
