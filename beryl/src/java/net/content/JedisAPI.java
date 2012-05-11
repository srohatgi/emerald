package net.content;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Jedis;

public class JedisAPI
{
  static JedisPool pool = new JedisPool("localhost", 6379);
  
  public JedisAPI()
  {
  }

  /***
   * create lookup indexes (like an rdbms)
   * @param name object name, example "user"
   * @param id object id, example user_id
   * @param lookups column names, example email
   */
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
  
  /***
   * lookup object based on an indexed column
   * @param name object name, "user"
   * @param lookup column indexed on, "email"
   * @param lookupId column row value, "s@s.com"
   * @return object id, user_id
   */
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
  
  /***
   * create and store an object
   * @param name object name, "user"
   * @param json properties, "email:contoso.com,age:23"
   * @return object id, user_id
   */
  public String storeObject(String name, Map<String,String> json)
  {
    return storeObject(name,json,null);
  }
  
  /***
   * create and store an object
   * @param name object name, "user"
   * @param json properties, "email:contoso.com,age:23"
   * @param lookups indexed properties, "email"
   * @return object id, user_id
   */
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

  /***
   * fetch object properties
   * @param name object name, "user"
   * @param id object id, "12323"
   * @return properties
   */
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

  /***
   * store collection of related objects
   * @param obj1 example: "user"
   * @param id1 example: user_id
   * @param obj2 example: "folder"
   * @param id2 example: folder_id
   * @param reltype one of: "SET", LIST"
   */
  public void storeRelations(String obj1, String id1, String obj2, String id2, String reltype)
  {
    Jedis j = null;
    try
    {
      j = pool.getResource();
      if ( reltype.equals("SET") )
      {
        Long result = j.sadd("user:"+id1+":"+obj2, id2);
        if ( result!= 1 )
        {
          throw new RuntimeException("unable to add: "+obj2+" as a set member of: "+obj1);
        }
      }
      else if ( reltype.equals("LIST") )
      {
        Long result = j.lpush("user:"+id1+":"+obj2, id2);
        if ( result!= 1 )
        {
          throw new RuntimeException("unable to add: "+obj2+" as a set member of: "+obj1);
        }
      }
    }
    finally
    {
      if ( j!= null ) pool.returnResource(j);
    }
  }

  /***
   * fetch collection of related object id's
   * @param obj1 "user"
   * @param id1 user_id
   * @param obj2 "folder"
   * @param reltype Set, List
   * @return Collection<String> Set<String> or List<String>
   */
  public Collection<String> fetchRelations(String obj1, String id1, String obj2, String reltype)
  {
    Jedis j = null;
    try
    {
      j = pool.getResource();
      String key = "user:"+id1+":"+obj2;
      if ( reltype.equals("SET") )
      {
        Set<String> result = j.smembers(key);
        return result;
      }
      else if ( reltype.equals("LIST") )
      {
        List<String> result = j.lrange(key, 0, j.llen(key));
        return result;
      }
      else
      {
        throw new RuntimeException("unsupported reltype!");
      }
    }
    finally
    {
      if ( j!= null ) pool.returnResource(j);
    }
  }

  public enum FsmState
  {
    START,
    PROCESS,
    END;
  }
  
  /***
   * creates/ updates fsm for a given object instance
   * @param fsm name of the fsm
   * @param targetState enum: values are START, PROCESS, END
   * @param object object name
   * @param objectid
   * @return
   */
  public void updateFsm(String fsm, FsmState targetState, String object, String objectid)
  {
    Jedis j = null;
    try
    {
      j = pool.getResource();
      String key;
      Long result;
      Double score = getUTC();
      FsmState originalState = FsmState.START;
      if ( targetState.equals(FsmState.END) ) originalState = FsmState.PROCESS;
      if ( !originalState.equals(targetState) )
      {
        // remove from original state
        key = fsm+":"+originalState.toString();
        result = j.zrem(key, object+":"+objectid);
        if ( result != 1 )
        {
          throw new  RuntimeException("unable to update fsm");
        }
      }
      // add the item to the target state set with the new score
      key = fsm+":"+targetState.toString();
      result = j.zadd(key, score, object+":"+objectid);
      if ( result != 1 )
      {
        throw new  RuntimeException("unable to update fsm");
      }
    }
    finally
    {
      if ( j!=null ) pool.returnResource(j);
    }
  }
  
  private static Double getUTC()
  {
    Calendar cal = new GregorianCalendar();
    return Double.valueOf(cal.getTimeInMillis());
  }
  
  public List<String> processFsm(String fsm, FsmState state)
  {
    Jedis j = null;
    try
    {
      j = pool.getResource();
      List<String> ids = j.zrange(fsm+":"+state.toString(), start, end)
    }
    finally
    {
      if ( j!= null ) pool.returnResource(j);
    }
    return null;
  }
}
