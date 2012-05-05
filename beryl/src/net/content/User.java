package net.content;

import java.util.Set;

public class User
{
  String authToken, email;
  YsiAPI yapi;
  JedisAPI japi;
  
  public User(String email, String passwd) throws Exception
  {
    yapi = YsiAPI.instance(email, passwd);
    this.email = email;
    this.authToken = yapi.authToken;
    japi = new JedisAPI(authToken);
  }
  
  public User(String authToken) 
  {
    japi = new JedisAPI(authToken);
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
      User u = new User("","");
      System.out.println("authToken:"+u.authToken);
    }
    catch (Exception e)
    {
      System.err.println("error running!");
      e.printStackTrace();
    }
  }
}
