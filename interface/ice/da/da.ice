#ifndef _OPENSCADA_DA_SLICE_
#define _OPENSCADA_DA_SLICE_

#include "../core/core.ice"

module OpenSCADA
{
	module DA
	{
		["ami"]
		interface DataCallback
		{
			idempotent void valueChange ( string item, Core::VariantBase value, bool cache );
			idempotent void attributesChange ( string item, Core::Attributes attributes, bool full );
		};
		
		interface Session extends Core::Session
		{
			void registerCallback ( DataCallback * dataCallback );	
			void unregisterCallback ();
		};
		
		exception UnableToCreateSession
		{
		};
		
		exception InvalidItemException
		{
		};
		
		["ami"]
		interface Hive extends Core::Server
		{
			Session * createSession ( Core::Properties properties ) throws UnableToCreateSession;
			
			void registerForItem ( Core::Session * session, string item ) throws Core::InvalidSessionException, InvalidItemException;
			void unregisterForItem ( Core::Session * session, string item ) throws Core::InvalidSessionException, InvalidItemException;
			
			["amd"] void write ( Core::Session * session, string item, Core::VariantBase value ) throws Core::InvalidSessionException, InvalidItemException;
			["amd"] Core::Properties writeAttributes ( Core::Session * session, string item, Core::Attributes attributes ) throws Core::InvalidSessionException, InvalidItemException;
		};
	};
};

#endif