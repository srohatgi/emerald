package net.content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
public class User implements Serializable 
{
  private String id;
  private Map<String,String> json;
  YsiAPI yapi;
  static final JedisAPI japi = new JedisAPI();
  
  public User() { json = new HashMap<String,String>(); }
  
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getEmail() { return json.get("email"); }
  public void setEmail(String email) { json.put("email",email); }
  public String getAuthToken() { return json.get("authToken"); }
  public void setAuthToken(String authToken) { json.put("authToken",authToken); }
  public String getBin() { return json.get("__bin__"); }
  public void setBin(String bin) { json.put("__bin__",bin); }
  
  public static User fetchByLogin(String email, String passwd) 
  {
    User u = new User();
    try
    {
      u.yapi = YsiAPI.instance(email, passwd);
    } catch (Exception e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new RuntimeException("unable to login");
    }
    String bin_id = u.yapi.binFolder();
    u.json = new HashMap<String,String>();
    u.json.put("email", email);
    u.json.put("authToken", u.yapi.authToken);
    u.json.put("__bin__",bin_id);
    
    String[] lookups = { "authToken", "email" };
    u.setId(japi.storeObject("user", u.json, lookups));
    return u;
  }
  
  public static User fetchById(String id) 
  {
    User u = new User();
    u.json = japi.fetchObject("user", id);
    u.setId(id);
    u.yapi = YsiAPI.instance(u.getAuthToken());
    return u;
  }
  
  public static User fetchByAuthToken(String authToken)
  {
    String id = japi.lookupId("user","authToken",authToken);
    return fetchById(id);
  }
  
  public void add(Folder f)
  {
    japi.storeRelations("user",getId(),"folder",f.folder_id,"SET");
  }
  
  public Set<Folder> folders()
  {
    Set<String> folder_ids = (Set<String>) japi.fetchRelations("user", getId(), "folder", "SET");
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
    //japi.storeRelations("user", id, "user", id2, reltype);
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
          System.out.println("id:"+u.getId()+","+e.getKey()+":"+e.getValue());
        }
      }
      else // lets create and save a new user
      {
        u = User.fetchByLogin(env.get("YSI_TEST_ACCT"),env.get("YSI_TEST_ACCT_PASSWD"));
        System.out.println("YSI authToken:"+u.getAuthToken()+",id:"+u.getId());
        BufferedWriter bw = new BufferedWriter(new FileWriter(env.get("COOKIE_FILE")));
        bw.write(u.getAuthToken());
        bw.newLine();
        bw.close();
      }
      
      u.add(new Folder("tryout",u.getBin(),u.getId(),u.yapi));
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
