package net.content;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Folder
{
  String folder_id;
  YsiAPI yapi;
  Map<String,String> json;
  final static JedisAPI japi = new JedisAPI();
  
  public Folder(String name, String bin_id, String user_id, YsiAPI yapi)
  {
    this.yapi = yapi;
    String ysi_folder_id = yapi.addFolder(name, bin_id);
    this.json = new HashMap<String,String>();
    json.put("name", name);
    json.put("owner", user_id);
    json.put("bin", bin_id);
    json.put("ysi_folder_id", ysi_folder_id);
    
    folder_id = japi.storeObject("folder", json);
  }

  public Folder(String folder_id, YsiAPI yapi)
  {
    this.yapi = yapi;
    this.folder_id = folder_id;
    this.json = japi.fetchObject("folder",folder_id);
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
