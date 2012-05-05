package net.content;

import java.util.List;
import java.util.Set;

public class Folder
{
  String name;
  
  public Folder(String name)
  {
    
  }
  
  public Folder(String name, User user)
  {
    YsiAPI yapi = user.yapi;
    
  }

  public List<Activity> timeline()
  {
    // TODO: read from jedis
    return null;
  }
  
  public Set<File> files()
  {
    // TODO: read from YAPI
    return null;
  }
}
