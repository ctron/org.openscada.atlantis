#ifndef _OPENSCADA_CORE_SLICE_
#define _OPENSCADA_CORE_SLICE_

/**
 * The open source SCADA system
 **/
module OpenSCADA
{
	/**
	 * Core definitions that are used by all other modules (e.g. DA).
	 **/
	module Core
	{
		dictionary <string, string> Properties;
		
		/**
		 * Thrown in the case a interface gets passed an invalid session.
		 * A session is not valid if: it is already closed, does not belong
		 * to the server that created it.
		 **/
		exception InvalidSessionException
		{
		};
		
		exception OperationNotSupportedException
		{
			/**
			 * A message explaining why the operation is not supported
			 **/
			string message;
		};
		
		interface Session
		{
		};
		
		/**
		 * A common base for all OpenSCADA servers
		 **/
		interface Server
		{
			/**
			 * Close a session that was aquired by some "createSession" call from
			 * derieved classes.
			 * @param sessionP The session to destroy
			 * @throws InvalidSessionException Raised if the session is not valid.
			 **/
			void closeSession ( Session * sessionP ) throws InvalidSessionException;
		};
		
		enum VariantType { VTboolean, VTstring, VTint32, VTint64, VTdouble, VTnull };
		
		class VariantBase
		{
			VariantType vt;
		};
		
		class VariantBoolean extends VariantBase
		{
			bool value;
		};
		
		class VariantString extends VariantBase
		{
			string value;
		};
		
		class VariantInt32 extends VariantBase
		{
			int value;
		};
		
		class VariantInt64 extends VariantBase
		{
			long value;
		};
		
		class VariantDouble extends VariantBase
		{
			double value;
		};
		
		dictionary <string, VariantBase> Attributes;
	};
};

#endif
