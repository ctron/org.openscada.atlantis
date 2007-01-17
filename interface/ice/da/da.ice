#ifndef _OPENSCADA_DA_SLICE_
#define _OPENSCADA_DA_SLICE_

#include "../ice/Ice/Identity.ice"
#include "../core/core.ice"

module OpenSCADA
{
	module DA
	{
		enum IODirection
		{
			INPUT,
			OUTPUT
		};
		sequence<IODirection> IODirections;
		
		module Browser
		{
			class Entry
			{
				string name;
				Core::Attributes attributes;
			};
			
			class FolderEntry extends Entry
			{
			};
			
			class ItemEntry extends Entry
			{
				string itemId;
				IODirections ioDirections;
			};
			
			sequence<Entry> EntrySequence;
			sequence<string> Location;
			
			exception InvalidLocationException
			{
				Location location;
			};
			
			sequence<string> NameSequence;
			
			["ami"]
			interface FolderCallback
			{
				void folderChanged ( NameSequence location, EntrySequence added, NameSequence removed, bool full );
			};
			
		};

		["ami"]
		interface DataCallback
		{
			idempotent void valueChange ( string item, Core::VariantBase value, bool cache );
			idempotent void attributesChange ( string item, Core::Attributes attributes, bool full );
		};
						
		interface Session extends Core::Session
		{
			void setDataCallback ( Ice::Identity ident );	
			void unsetDataCallback ();
			
			void setFolderCallback ( Ice::Identity ident );
			void unsetFolderCallback ();
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
			
			 nonmutating Browser::EntrySequence browse ( Session * session, Browser::Location location ) throws Core::InvalidSessionException, Core::OperationNotSupportedException, Browser::InvalidLocationException;
			 void subscribeFolder ( Session * session, Browser::Location location ) throws Core::InvalidSessionException, Core::OperationNotSupportedException, Browser::InvalidLocationException;
			 void unsubscribeFolder ( Session * session, Browser::Location location ) throws Core::InvalidSessionException, Core::OperationNotSupportedException, Browser::InvalidLocationException;
		};
	};
};

#endif