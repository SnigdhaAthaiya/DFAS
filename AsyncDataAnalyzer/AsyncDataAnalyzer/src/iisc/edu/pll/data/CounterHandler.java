package iisc.edu.pll.data;

import java.util.HashMap;
import java.util.HashSet;

public class CounterHandler {
	
	public static HashMap<String, Integer> getNoChange(HashSet<String> messageSet)
	{
		HashMap<String, Integer> del = new HashMap<>();
		for(String key : messageSet)
		{
			del.put(key, 0);
		}
		return del;
	}
	
	public static HashMap<String, Integer> getDelta(HashMap<String, Integer> del, String message, int val)
	{
		String target ="";
		for(String key : del.keySet())
		{
			if(key.equals(message))
				target = key;
		}
		
		del.replace(target, val);
		return del;
	}

}
