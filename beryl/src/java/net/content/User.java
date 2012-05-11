package net.content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
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
    String bin_id = yapi.binFolder();
    json = new HashMap<String,String>();
    json.put("email", email);
    json.put("authToken", yapi.authToken);
    json.put("__bin__",bin_id);
    
    String[] lookups = { "authToken", "email" };
    id = japi.storeObject("user", json, lookups);
  }
  
  public User(String id) 
  {
    this.json = japi.fetchObject("user", id);
    this.id = id;
    this.yapi = YsiAPI.instance(json.get("authToken"));
  }
  
  public static User fetchByAuthToken(String authToken)
  {
    String id = japi.lookupId("user","authToken",authToken);
    return new User(id);
  }
  
  public void add(Folder f)
  {
    japi.storeRelations("user",id,"folder",f.folder_id,"SET");
  }
  
  public Set<Folder> folders()
  {
    Set<String> folder_ids = (Set<String>) japi.fetchRelations("user", id, "folder", "SET");
    Set<Folder> sf = new HashSet<Folder>();
    for (String folder_id: folder_ids)
    {
      Folder f = new Folder(folder_id,yapi);
      sf.add(f);
    }
    return sf;
  }
  
  public Set<Folder> follows()
  {
    // TODO: read from jedis
    return null;
  }

  public void request(String reqtype, String folderName)
  {
    japi.storeRelations("user", id, "user", id2, reltype)
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
      
      u.add(new Folder("tryout",u.json.get("__bin__"),u.id,u.yapi));
      Set<Folder> sf = u.folders();
      for (Folder f: sf)
      {
        System.out.println("folder added:"+f.json.get("name"));
      }
    }
    catch (Exception e)
    {
      System.err.println("error running!");
      e.printStackTrace();
    }
  }
}
