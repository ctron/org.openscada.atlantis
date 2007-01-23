#ifndef _OPENSCADA_DA_SLICE_
#define _OPENSCADA_DA_SLICE_

#include "../ice/Ice/Identity.ice"
#include "../core/core.ice"

module OpenSCADA
{
	module DA
	{
		/**
		 * An enumeration of possible IO directions.
		 **/
		enum IODirection
		{
			/**
			 * [INPUT] Data coming into the OpenSCADA System
			 **/
			INPUT,
			/**
			 * [OUTPUT] Data going out from the OpenSCADA System
			 **/
			OUTPUT
		};
		sequence<IODirection> IODirections;
		
		module Browser
		{
			/**
			 * A generic browser entry
			 **/
			class Entry
			{
				string name;
				Core::Attributes attributes;
			};
			
			/**
			 * A browser entry that acts as a folder
			 **/
			class FolderEntry extends Entry
			{
			};
			
			/**
			 * A browser entry that acts as an item.
			 **/
			class ItemEntry extends Entry
			{
				/**
				 * The ID of the item in the server
				 **/
				string itemId;
				/**
				 * The supported IO operations as a hint to the user
				 **/
				IODirections ioDirectionsM;
			};
			
			sequence<Entry> EntrySequence;
			sequence<string> Location;
			sequence<string> NameSequence;
			
			exception InvalidLocationException
			{
				Location locationM;
			};
		
			/**
			 * The folder callback that will be attached to the session
			 **/
			["ami"]
			interface FolderCallback
			{
				/**
				 * Called when a subscribed folder changed.
				 **/
				void folderChanged ( Location locationP, EntrySequence added, NameSequence removed, bool full );
			};
			
		};

		/**
		 * The data callback that will be attached to the session
		 **/
		["ami"]
		interface DataCallback
		{
			/**
			 * Called when a value of a subscribed data item changed.
			 **/
			idempotent void valueChange ( string item, Core::VariantBase value, bool cache );
			
			/**
			 * Called when attributes of a subscribed data item changed.
			 **/
			idempotent void attributesChange ( string item, Core::Attributes attributes, bool full );
		};

		/**
		 * The session object for DA interfaces
		 **/
		interface Session extends Core::Session
		{
			/**
			 * Set the data callback for this session. Any previous callback
			 * Will be removed. Only one callback at a time may be active.
			 **/
			void setDataCallback ( Ice::Identity ident );
			
			/**
			 * Clear the previously set data callback of this session. If no
			 * callback was set the method does nothing.
			 **/
			void unsetDataCallback ();
			
			/**
			 * Set the folder callback for this session. Any previous callback
			 * Will be removed. Only one callback at a time may be active.
			 **/
			void setFolderCallback ( Ice::Identity ident );
			
			/**
			 * Clear the previously set folder callback of this session. If no
			 * callback was set the method does nothing.
			 **/
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
			/**
			 * Create a new session
			 * @return A new session
			 **/
			Session * createSession ( Core::Properties propertiesP ) throws UnableToCreateSession;
			
			/**
			 * Subscribe the session to an item
			 **/
			void registerForItem ( Session * sessionP, string item, bool initialCacheRead ) throws Core::InvalidSessionException, InvalidItemException;
			/**
			 * Unsubscribe the session from an item
			 */
			void unregisterForItem ( Session * sessionP, string item ) throws Core::InvalidSessionException, InvalidItemException;
			
			/**
			 * Write a value to an item
			 **/
			["amd"] void write ( Session * sessionP, string item, Core::VariantBase value ) throws Core::InvalidSessionException, InvalidItemException;
			/**
			 * Write attributes to an item
			 **/
			["amd"] Core::Properties writeAttributes ( Session * sessionP, string item, Core::Attributes attributes ) throws Core::InvalidSessionException, InvalidItemException;
			
			/**
			 * Return all browser entries that are a the specified location.
			 **/
			nonmutating Browser::EntrySequence browse ( Session * sessionP, Browser::Location location ) throws Core::InvalidSessionException, Core::OperationNotSupportedException, Browser::InvalidLocationException;
			
			/**
			 * Subscribe to a specific browser location. You will receive the current status in the first
			 * event after subscribing.
			 **/
			void subscribeFolder ( Session * sessionP, Browser::Location location ) throws Core::InvalidSessionException, Core::OperationNotSupportedException, Browser::InvalidLocationException;
			/**
			 * Unsubscribe from a subscribed browser location. If the session is not subscribed
			 * to that location the method does nothing.
			 **/
			void unsubscribeFolder ( Session * sessionP, Browser::Location location ) throws Core::InvalidSessionException, Core::OperationNotSupportedException, Browser::InvalidLocationException;
		};
	};
};

#endif