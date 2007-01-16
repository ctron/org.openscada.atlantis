#ifndef _OPENSCADA_CORE_SLICE_
#define _OPENSCADA_CORE_SLICE_

module OpenSCADA
{
	module Core
	{
		dictionary <string, string> Properties;
		
		exception InvalidSessionException
		{
		};
		
		interface Session
		{
		};
		
		interface Server
		{
			void closeSession ( Session * session ) throws InvalidSessionException;
		};
		
		enum VariantType { VTboolean, VTstring, VTint32, VTint64, VTdouble };
		
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
		
		dictionary <string, VariantBase> Attributes;
	};
};

#endif