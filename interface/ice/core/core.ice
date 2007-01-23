#ifndef _OPENSCADA_CORE_SLICE_
#define _OPENSCADA_CORE_SLICE_

module OpenSCADA
{
	module Core
	{
		dictionary <string, string> Properties;
		
		/**
		 * Thrown in the case a interface gets passed an invalid session
		 **/
		exception InvalidSessionException
		{
		};
		
		exception OperationNotSupportedException
		{
		};
		
		interface Session
		{
		};
		
		interface Server
		{
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