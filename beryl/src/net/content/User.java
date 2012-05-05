package net.content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

public class User
{
  String id;
  Map<String,String> json;
  YsiAPI yapi;
  static final JedisAPI japi = new JedisAPI();
  
  public User(String email, String passwd) throws Exception
  {
    yapi = YsiAPI.instance(email, passwd);
    json = new HashMap<String,String>();
    json.put("email", email);
    json.put("authToken", yapi.authToken);
    String[] lookups = { "authToken" };
    id = japi.storeObject("user", json, lookups);
  }
  
  public User(String id) 
  {
    json = japi.fetchObject("user", id);
    this.id = id;
  }
  
  public static User fetchByAuthToken(String authToken)
  {
    String id = japi.lookupId("user","authToken",authToken);
    return new User(id);
  }
  
  public void addFolder(String name)
  {
    
  }
  
  public Set<Folder> follows()
  {
    // TODO: read from jedis
    return null;
  }
  
  public Set<Folder> owns()
  {
    // TODO: read from YSI
    return null;
  }
  
  public static void main(String[] args)
  {
    Map<String,String> env = System.getenv();
    try
    {
      User u;
      java.io.File cookie = new java.io.File(env.get("COOKIE_FILE"));
      if ( cookie.exists() ) // we've ran before
      {
        BufferedReader br = new BufferedReader(new FileReader(env.get("COOKIE_FILE")));
        String authToken = br.readLine();
        br.close();
        u = User.fetchByAuthToken(authToken);
        for (Map.Entry<String,String> e: u.json.entrySet())
        {
          System.out.println("id:"+u.id+","+e.getKey()+":"+e.getValue());
        }
      }
      else // lets create and save a new user
      {
        u = new User(env.get("YSI_TEST_ACCT"),env.get("YSI_TEST_ACCT_PASSWD"));
        System.out.println("YSI authToken:"+u.json.get("authToken")+",id:"+u.id);
        BufferedWriter bw = new BufferedWriter(new FileWriter(env.get("COOKIE_FILE")));
        bw.write(u.json.get("authToken"));
        bw.newLine();
        bw.close();
      }
    }
    catch (Exception e)
    {
      System.err.println("error running!");
      e.printStackTrace();
    }
  }
}
