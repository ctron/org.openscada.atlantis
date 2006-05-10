package org.openscada.da.core;

public class InvalidItemException extends Exception {

	private String _itemName = "";
	/**
	 * 
	 */
	private static final long serialVersionUID = 898848351108459646L;
	
	public InvalidItemException ( String itemName )
	{
        super("Invalid item");
        
		_itemName = itemName;
	}

	public String getItemName() {
		return _itemName;
	}

	public void setItemName(String itemName) {
		_itemName = itemName;
	}
}
