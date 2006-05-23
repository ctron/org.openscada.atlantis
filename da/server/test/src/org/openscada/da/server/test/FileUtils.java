package org.openscada.da.server.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	public static String[] readFile ( File file ) throws IOException
	{
		BufferedReader reader = new BufferedReader ( new FileReader ( file ) );
		
		List<String> content = new ArrayList<String>();
		
		String line = null;
		while ( (line = reader.readLine()) != null )
		{
			content.add ( line );
		}
		
		return content.toArray(new String[content.size()]);
	}
}
