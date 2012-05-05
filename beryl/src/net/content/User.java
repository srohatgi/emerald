package net.content;

import java.util.Set;
import com.yousendit.dpi.YouSendItDPI;

public class User
{
  String authToken, email;
  YouSendItDPI yapi;
  JedisAPI japi;
  
  public User(String email, String passwd) throws Exception
  {
    yapi = new YouSendItDPI(YouSendItDPI.SandboxEndpoint,"");
    authToken = yapi.login(email, passwd);
    this.email = email;
    japi = new JedisAPI(authToken);
  }
  
  public User(String authToken) 
  {
    japi = new JedisAPI(authToken);
    email = japi.fetchEmail();
    yapi = new YouSendItDPI(YouSendItDPI.SandboxEndpoint,"",authToken);
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
}
