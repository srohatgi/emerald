package net.content;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/***
 * participation in a group
 * allows your folders to be 
 * discover-able to group members
 * @author sumeet
 *
 */
public class Group
{
  static final JedisAPI japi = new JedisAPI();
  String id;
  Map<String,String> json;
  
  public static Group CreateGroup(String name)
  {
    HashMap<String,String>json = new HashMap<String,String>();
    json.put("name", name);
    Group g = new Group();
    g.id = japi.storeObject("group", json);
    g.json = json;
    return g;
  }
  
  public Group(String id)
  {
    json = japi.fetchObject("group", id);
    this.id = id;
  }
  
  public void addUser(User u)
  {
    japi.storeRelations("group", id, "user", u.id, "SET");
  }
  
  public Set<User> users()
  {
    Set<String> users = (Set<String>)japi.fetchRelations("group", id, "user", "SET");
    Set<User> su = new HashSet<User>();
    for(String uid:users)
    {
      su.add(new User(uid));
    }
    return su;
  }

  private Group()
  {
    // TODO Auto-generated constructor stub
  }
}
