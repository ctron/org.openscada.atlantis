/*
 * This file is part of the OpenSCADA projecimport java.util.LinkedList;

import org.openscada.da.server.opc.connection.OPCController;
import org.openscada.da.server.opc.connection.OPCStateListener;
ms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.opc.browser;

import java.util.LinkedList;

import org.openscada.da.server.opc.connection.OPCController;
import org.openscada.da.server.opc.connection.OPCStateListener;

public class OPCRootTreeFolder extends OPCTreeFolder implements OPCStateListener
{

    private final OPCController controller;

    public OPCRootTreeFolder ( final OPCController controller )
    {
        super ( controller, new LinkedList<String> () );
        this.controller = controller;
    }

    @Override
    public void added ()
    {
        super.added ();
        this.controller.addStateListener ( this );
    }

    @Override
    public void removed ()
    {
        this.controller.removeStateListener ( this );
        super.removed ();
    }

    public void connectionEstablished ()
    {
        checkRefresh ();
    }

    public void connectionLost ()
    {
        this.folderImpl.clear ();
        this.refreshed = false;
    }

}
