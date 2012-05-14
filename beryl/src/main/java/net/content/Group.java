package net.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

/***
 * participation in a group
 * allows your folders to be 
 * discover-able to group members
 * @author sumeet
 *
 */
@XmlRootElement(name = "group")
public class Group implements Serializable
{
  private static final JedisAPI japi = new JedisAPI();
  private String id;
  private Map<String,String> json;
  
  public static Group addGroup(String name)
  {
    Group g = new Group();
    g.json.put("name", name);
    g.id = japi.storeObject("group", g.json);
    return g;
  }
  
  public static List<Group> browse(Long prev,Long next)
  {
    Long start = 0L, end = 10L;
    if ( next != null )
    {
      start = next;
      end = next+10;
    }
    else if ( prev != null )
    {
      start = prev - 10;
      end = prev;
    }
    else //if ( prev == null && next == null )
    {
      start = 0L;
      end = 10L;
    }
    List<String> groups = japi.getObjects("group", start, end);
    ArrayList<Group> result = new ArrayList<Group>();
    for (String groupid:groups) 
    {
      result.add(fetchById(groupid.split(":")[1]));
    }
    return result;
  }
  
  public static Group fetchById(String id)
  {
    Group g = new Group();
    g.json = japi.fetchObject("group", id);
    g.id = id;
    return g;
  }
  
  public void addUser(User u)
  {
    japi.storeRelations("group", id, "user", u.getId(), "SET");
  }
  
  public Set<User> users()
  {
    Set<String> users = (Set<String>)japi.fetchRelations("group", id, "user", "SET");
    Set<User> su = new HashSet<User>();
    for(String uid:users)
    {
      su.add(User.fetchById(uid));
    }
    return su;
  }

  public Group()
  {
    json = new HashMap<String,String>();
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getName() { return json.get("name"); }
  public void setName(String name) { json.put("name", name); }
}
