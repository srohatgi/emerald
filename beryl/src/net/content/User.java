package net.content;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class User
{
  String id, authToken, email;
  YsiAPI yapi;
  JedisAPI japi;
  
  public User(String email, String passwd) throws Exception
  {
    yapi = YsiAPI.instance(email, passwd);
    this.email = email;
    this.authToken = yapi.authToken;
    japi = new JedisAPI();
    Map<String,String> json = new HashMap<String,String>();
    json.put("id", this.id);
    json.put("email", this.email);
    json.put("authToken", this.authToken);
    id = japi.storeObject("user", json);
  }
  
  public User(String authToken) 
  {
    japi = new JedisAPI();
    email = japi.fetchEmail();
    yapi = YsiAPI.instance(authToken);
    this.authToken = authToken;
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
    try
    {
      Map<String,String> env = System.getenv();
      User u = new User(env.get("YSI_TEST_ACCT"),env.get("YSI_TEST_ACCT_PASSWD"));
      System.out.println("YSI authToken:"+u.authToken);
      System.out.println("User id:"+u.id);
    }
    catch (Exception e)
    {
      System.err.println("error running!");
      e.printStackTrace();
    }
  }
}
