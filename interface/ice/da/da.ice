#ifndef _OPENSCADA_DA_SLICE_
#define _OPENSCADA_DA_SLICE_

#include "../ice/Ice/Identity.ice"
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
			void setCallback ( Ice::Identity ident );	
			void unsetCallback ();
		};
		
		exception UnableToCreateSession
		{
		};
		
		exception InvalidItemException
		{
			string item;
		};
		
		["ami"]
		interface Hive extends Core::Server
		{
			Session * createSession ( Core::Properties properties ) throws UnableToCreateSession;
			
			void registerForItem ( Session * session, string item, bool initialCacheRead ) throws Core::InvalidSessionException, InvalidItemException;
			void unregisterForItem ( Session * session, string item ) throws Core::InvalidSessionException, InvalidItemException;
			
			["amd"] void write ( Session * session, string item, Core::VariantBase value ) throws Core::InvalidSessionException, InvalidItemException;
			["amd"] Core::Properties writeAttributes ( Session * session, string item, Core::Attributes attributes ) throws Core::InvalidSessionException, InvalidItemException;
		};
	};
};

#endif