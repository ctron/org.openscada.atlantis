#ifndef _OPENSCADA_DA_SLICE_
#define _OPENSCADA_DA_SLICE_

#include "../ice/Ice/Identity.ice"
#include "../core/core.ice"

module OpenSCADA
{
	/**
	 * The data access interfaces for OpenSCADA
	 **/
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
		
		enum SubscriptionState
		{
			CONNECTED,
			DISCONNECTED,
			GRANTED
		};
		
		/**
		 * The browser module contains all data structures and interfaces
		 * needed by the item browser interface. The item browser is a
		 * hierarchical enumeration of data item IDs including additional
		 * meta data information.
		 *
		 * Since the item browser is optional when implementing a DA server
		 * it is seperated from the main DA module. The item browser is considered
		 * an "add-on" which helps a human user to select data items from the servers
		 * list of data items.
		 **/
		module Browser
		{
			/**
			 * A generic browser entry.
             * This class is the generic base class for all browser entries and
             * must not be instatiated directly.
			 **/
			class Entry
			{
				/**
				 * The name of the entry. The name must be unique at its location
				 * but not in the whole server. In contrast the "item ID" must be unique
				 * throughout the whole server.
				 **/
				string name;
				
				/**
				 * A set of attributes providing additional information about this entry
				 * (e.g. content of the folder or a description of the data item).
				 **/
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
				 * The ID of the item in the server. It must be the server-unique
				 * ID of the item.
				 **/
				string itemId;
				
				/**
				 * The supported IO operations as a hint to the user. The entry should
				 * contain the allowed IO operations but it is not required.
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
			 * The folder callback that will be attached to the session.
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
		 * The data callback that will be attached to the session.
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
			
			/**
			 * Called when a subscription state of an item changes
			 **/
			idempotent void subscriptionChange ( string item, SubscriptionState subscriptionStateP );
		};
		 
		struct WriteAttributesResultEntry
		{
			string item;
			string result;
		};
		sequence<WriteAttributesResultEntry> WriteAttributesResultSequence;
		
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
			 * Create a new session.
			 *
			 * The server might require additional session properties in order the create
			 * a new session (e.g. username/password). What properties are required depend
			 * in the implementation of the server.
			 *
			 * @param properties The properties used to create the session.
			 * @throws UnableToCreateSession Raised if the session cannot be created to due any reason
			 * @return A new session
			 **/
			Session * createSession ( Core::Properties \properties ) throws UnableToCreateSession;
			
			/**
			 * Subscribe the session to an item
			 **/
			void subscribeItem ( Session * sessionP, string item ) throws Core::InvalidSessionException, InvalidItemException;
			/**
			 * Unsubscribe the session from an item
			 */
			void unsubscribeItem ( Session * sessionP, string item ) throws Core::InvalidSessionException, InvalidItemException;
			
			/**
			 * Write a value to an item. The call will be return once the value is written.
			 **/
			["amd"] void write ( Session * sessionP, string item, Core::VariantBase value ) throws Core::InvalidSessionException, InvalidItemException;
			/**
			 * Write attributes to an item. The call will be return once all attributes are written.
			 **/
			["amd"] WriteAttributesResultSequence writeAttributes ( Session * sessionP, string item, Core::Attributes attributes ) throws Core::InvalidSessionException, InvalidItemException;
			
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