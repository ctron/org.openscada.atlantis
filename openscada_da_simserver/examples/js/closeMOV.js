var itemId = thisItem.getId ().replace(".close","");

hive.updateDataBoolean ( hive.getItem ( itemId + ".opened" ), false );
hive.updateDataBoolean ( hive.getItem ( itemId + ".closed" ), true );