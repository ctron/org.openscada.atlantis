var itemId = thisItem.getId ().replace(".open","");

hive.updateDataBoolean ( hive.getItem ( itemId + ".opened" ), true);
hive.updateDataBoolean ( hive.getItem ( itemId + ".closed" ), false );