package net.content;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Jedis;

public class JedisAPI
{
  static JedisPool pool = new JedisPool("localhost", 6379);
  
  public JedisAPI()
  {
  }

  public void buildLookups(String name, String id, String[] lookups)
  {
    Map<String,String> json = fetchObject(name,id);
    Jedis j = null;
    try
    {
      j = pool.getResource();
      buildLookups(name, id, lookups, json, j);
    }
    finally
    {
      if ( j!= null ) pool.returnResource(j);
    }
  }

  private void buildLookups(String name, String id, String[] lookups,
      Map<String, String> json, Jedis j)
  {
    for (String l: lookups)
    {
      String key = name+":"+l+":"+json.get(l);
      String result = j.set(key, id);
      if ( !result.equals("OK") ) throw new RuntimeException("unable to build lookup for:"+name+", with id:"+id);
    }
  }
  
  public String lookupId(String name, String lookup, String lookupId)
  {
    Jedis j = null;
    try
    {
      j = pool.getResource();
      String key = name+":"+lookup+":"+lookupId;
      String id = j.get(key);
      return id;
    }
    finally
    {
      if ( j!= null ) pool.returnResource(j);
    }
  }
  
  public String storeObject(String name, Map<String,String> json)
  {
    return storeObject(name,json,null);
  }
  
  public String storeObject(String name, Map<String,String> json, String[] lookups)
  {
    Jedis j = null;
    try
    {
      j = pool.getResource();
      String id = UUID.randomUUID().toString();
      String s = "";
      for (Map.Entry<String, String> e: json.entrySet()) 
      {
        s += e.getKey()+":"+e.getValue()+",";
      }
      String result = j.set(name+":"+id, s);
      if ( !result.equals("OK") ) throw new RuntimeException("error saving object:"+name);
      System.out.println("storing result:"+result);
      if ( lookups != null )
      {
        buildLookups(name,id,lookups,json,j);
      }
      return id;
    }
    finally
    {
      if ( j!= null ) pool.returnResource(j);
    }
  }

  public Map<String, String> fetchObject(String name,String id)
  {
    Jedis j = null;
    try
    {
      j = pool.getResource();
      String obj_str = j.get(name+":"+id);
      String[] pairs = obj_str.split(",");
      Map<String,String> json = new HashMap<String,String>();
      for (String s : pairs) 
      {
        String[] entry = s.split(":");
        json.put(entry[0],entry[1]);
      }
      return json;
    }
    finally
    {
      if ( j != null ) pool.returnResource(j);
    }
  }

}
