package net.content;

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
  
  public String storeObject(String name, Map<String,String> json)
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
      if ( result != "OK" ) throw new RuntimeException("error saving object:"+name);
      System.out.println("result:"+result);
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
      String json = j.get(name+":"+id);
      return null;
    }
    finally
    {
      if ( j != null ) pool.returnResource(j);
    }
  }

  public String fetchEmail()
  {
    // TODO Auto-generated method stub
    return null;
  }
}
