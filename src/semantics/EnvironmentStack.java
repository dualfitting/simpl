package semantics;

import java.util.HashMap;
import java.util.LinkedList;

import javacc.SimpleNode;

public class EnvironmentStack {
	private LinkedList<HashMap<String, SimpleNode>> envList
		= new LinkedList<HashMap<String, SimpleNode>>();

	public boolean containsKey(String key)
	{
		for(int i = 0; i < envList.size(); ++i)
		{
			if(envList.get(i).containsKey(key))
			{
				return true;
			}
		}
		return false;
	}
	
	public SimpleNode get(String key)
	{
		for(int i = 0; i < envList.size(); ++i)
		{
			if(envList.get(i).containsKey(key))
			{
				return envList.get(i).get(key);
			}
		}
		return null;
	}
	
	public void put(String key, SimpleNode value)
	{
		for(int i = 0; i < envList.size(); ++i)
		{
			if(envList.get(i).containsKey(key))
			{
				envList.get(i).put(key, value);
				return;
			}
		}
		envList.get(0).put(key, value);

	}
	
	public void add(String key, SimpleNode value)
	{
		envList.get(0).put(key, value);
	}
	
	public void push(HashMap<String, SimpleNode> env)
	{
		envList.add(0, env);
	}
	
	public HashMap<String, SimpleNode> pop()
	{
		return envList.remove(0);
	}
	
	public void clear()
	{
		envList.clear();
		envList.add(new HashMap<String, SimpleNode>());
	}
	
	public HashMap<String, SimpleNode> peek()
	{
		return envList.get(0);
	}

}
