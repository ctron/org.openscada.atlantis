package org.openscada.da.net.handler;

import org.openscada.da.core.OperationParameters;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.sec.UserInformation;

public class Operation
{

    public static final String FIELD_USER = "user";

    public static final String FIELD_OPERATION_PARAMETERS = "operation-parameters";

    public static OperationParameters convertOperationParameters ( final Value value )
    {
        if ( value == null )
        {
            return null;
        }
        if ( ! ( value instanceof MapValue ) )
        {
            return null;
        }
        final MapValue mapValue = (MapValue)value;

        final String user = mapValue.get ( FIELD_USER ) != null ? mapValue.get ( FIELD_USER ).toString () : null;

        return new OperationParameters ( new UserInformation ( user ) );
    }

    public static void encodeOperationParameters ( final OperationParameters operationParameters, final Message message )
    {
        if ( operationParameters != null )
        {
            final MapValue parameters = new MapValue ();
            message.getValues ().put ( FIELD_OPERATION_PARAMETERS, parameters );
            if ( operationParameters.getUserInformation () != null && operationParameters.getUserInformation ().getName () != null )
            {
                parameters.put ( FIELD_USER, new StringValue ( operationParameters.getUserInformation ().getName () ) );
            }
        }
    }
}
