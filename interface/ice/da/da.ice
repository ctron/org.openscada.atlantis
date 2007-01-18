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
				IODirections ioDirectionsM;
			};
			
			sequence<Entry> EntrySequence;
			sequence<string> Location;
			sequence<string> NameSequence;
			
			exception InvalidLocationException
			{
				Location locationM;
			};
		
			["ami"]
			interface FolderCallback
			{
				void folderChanged ( Location locationP, EntrySequence added, NameSequence removed, bool full );
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
			Session * createSession ( Core::Properties propertiesP ) throws UnableToCreateSession;
			
			void registerForItem ( Session * sessionP, string item, bool initialCacheRead ) throws Core::InvalidSessionException, InvalidItemException;
			void unregisterForItem ( Session * sessionP, string item ) throws Core::InvalidSessionException, InvalidItemException;
			
			["amd"] void write ( Session * sessionP, string item, Core::VariantBase value ) throws Core::InvalidSessionException, InvalidItemException;
			["amd"] Core::Properties writeAttributes ( Session * sessionP, string item, Core::Attributes attributes ) throws Core::InvalidSessionException, InvalidItemException;
			
			 nonmutating Browser::EntrySequence browse ( Session * sessionP, Browser::Location location ) throws Core::InvalidSessionException, Core::OperationNotSupportedException, Browser::InvalidLocationException;
			 void subscribeFolder ( Session * sessionP, Browser::Location location ) throws Core::InvalidSessionException, Core::OperationNotSupportedException, Browser::InvalidLocationException;
			 void unsubscribeFolder ( Session * sessionP, Browser::Location location ) throws Core::InvalidSessionException, Core::OperationNotSupportedException, Browser::InvalidLocationException;
		};
	};
};

#endif