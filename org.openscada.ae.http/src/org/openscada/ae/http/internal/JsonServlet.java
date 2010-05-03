package org.openscada.ae.http.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openscada.ae.Event;
import org.openscada.ae.event.EventProcessor;

import com.inavare.tas.alarms.api.AlarmsConverter;

public class JsonServlet extends HttpServlet
{
    private static final long serialVersionUID = -2152989291571139312L;

    private final EventProcessor eventProcessor;

    public JsonServlet ( final EventProcessor eventProcessor )
    {
        this.eventProcessor = eventProcessor;
    }

    @Override
    protected void doPost ( final HttpServletRequest request, final HttpServletResponse response ) throws ServletException, IOException
    {
        if ( request.getPathInfo () == null )
        {
            send404Error ( request, response );
            return;
        }
        if ( request.getPathInfo ().equals ( "/publish" ) || request.getPathInfo ().equals ( "/publish/" ) )
        {
            // read input
            BufferedReader reader = request.getReader ();
            char[] buffer = new char[4 * 1024];
            int len;
            StringBuilder sb = new StringBuilder ();
            while ( ( len = reader.read ( buffer, 0, buffer.length ) ) != -1 )
            {
                sb.append ( buffer, 0, len );
            }
            Event event = AlarmsConverter.deserializeEvent ( sb.toString () );
            // publish event
            this.eventProcessor.publishEvent ( event );
            // return output
            response.setContentType ( "text/html" );
            PrintWriter pw = new PrintWriter ( response.getOutputStream () );
            pw.write ( "OK" );
            pw.close ();
        }
        else
        {
            send404Error ( request, response );
        }
    }

    private void send404Error ( final HttpServletRequest request, final HttpServletResponse response ) throws IOException
    {
        response.sendError ( HttpServletResponse.SC_NOT_FOUND );
    }
}