/*
 * This file is part of the OpenSCADA projecimport java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.openscada.da.server.opc2.Hive;
import org.openscada.da.server.opc2.connection.ConnectionSetup;
import org.openscada.da.server.opc2.connection.OPCModel;
import org.openscada.da.server.opc2.job.Worker;
import org.openscada.da.server.opc2.job.impl.BrowseJob;
d in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.opc2.browser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.openscada.da.server.opc2.Hive;
import org.openscada.da.server.opc2.connection.ConnectionSetup;
import org.openscada.da.server.opc2.connection.OPCModel;
import org.openscada.da.server.opc2.job.Worker;
import org.openscada.da.server.opc2.job.impl.BrowseJob;

public class OPCBrowserManager
{

    private static class Request
    {
        private final BrowseRequest request;

        private final BrowseRequestListener listener;

        public Request ( final BrowseRequest request, final BrowseRequestListener listener )
        {
            this.request = request;
            this.listener = listener;
        }
    }

    private final Worker worker;

    private final OPCModel model;

    private final List<Request> requests = new LinkedList<Request> ();

    public OPCBrowserManager ( final Worker worker, final ConnectionSetup configuration, final OPCModel model, final Hive hive )
    {
        this.worker = worker;
        this.model = model;
    }

    /**
     * Perform all browse requests
     * @throws Throwable 
     */
    public void performBrowse () throws Throwable
    {
        List<Request> currentRequests;
        synchronized ( this.requests )
        {
            currentRequests = new ArrayList<Request> ( this.requests );
            this.requests.clear ();
        }

        for ( final Request request : currentRequests )
        {
            processRequest ( request );
        }
    }

    private void processRequest ( final Request request ) throws Throwable
    {
        final BrowseJob job = new BrowseJob ( this.model.getDefaultTimeout (), this.model, request.request );

        try
        {
            final BrowseResult result = this.worker.execute ( job, job );
            request.listener.browseComplete ( result );
        }
        catch ( final Throwable e )
        {
            request.listener.browseError ( e );
            throw e;
        }
    }

    public void addBrowseRequest ( final BrowseRequest request, final BrowseRequestListener listener )
    {
        if ( listener == null )
        {
            throw new NullPointerException ( "Listener must not be null" );
        }

        if ( !this.model.isConnected () )
        {
            listener.browseError ( new RuntimeException ( "OPC is not connected" ).fillInStackTrace () );
            return;
        }

        synchronized ( this.requests )
        {
            this.requests.add ( new Request ( request, listener ) );
        }
    }

}
