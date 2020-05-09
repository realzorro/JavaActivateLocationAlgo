package common.log;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ZLog{

	static public boolean wl(String OneLog) 
	{	
		try {
			FileWriter writer = new FileWriter("log.txt", true);
			
			Date now = new Date();		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String strT = sdf.format(now);
			
			writer.write(strT+":"+OneLog+"\r\n");
			writer.close();
		} 
		catch (IOException e) 
		{
			return false;
		}

		return true;
	}
	
	static public void pl(String OneLog) 
	{
		Date now = new Date();		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strT = sdf.format(now);
		
		System.out.println(strT+":"+OneLog);
	}
	
	static public void bl(String OneLog)
	{
		wl(OneLog);
		pl(OneLog);
	}
	
}
