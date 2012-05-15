package net.service;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.content.Group;
import net.content.User;

@Path("/")
public class NetServiceImpl implements NetService 
{
  private static final Log log = LogFactory.getLog(NetServiceImpl.class);
  
  @POST
  @Path("users/login")
  @Produces("application/json")
  public User login(@FormParam("email") String email, @FormParam("password") String password) 
  {
    log.info("email="+email+" password="+password);
    try 
    {
      return User.fetchByLogin(email,password);
    }
    catch (Exception ex) 
    {
      ex.printStackTrace();
      log.error("exception caught:"+ex.getMessage());
      throw new RuntimeException(ex);
    }
  }

  @GET
  @Path("groups")
  @Produces("application/json")
  public GroupList browseGroups(@QueryParam("prev") Long prev, @QueryParam("next") Long next, @QueryParam("count") Long count)
  {
    log.info("prev="+prev+" next="+next+" count="+count);
    if ( count == null || count == 0L ) count = 10L;
    try
    {
      List<Group> groups = Group.browse(prev,next,count);
      GroupList glist = new GroupList();
      glist.setGroups(groups.toArray(new Group[0]));
      if ( next == null && prev == null ) 
      {
        glist.setNext(count);
        glist.setPrev(0L);
      }
      else if ( next != null )
      {
        
        glist.setNext((next+count)>groups.size()?null:next+count);
        glist.setPrev(next);
      }
      else if ( prev != null )
      {
        glist.setNext(prev);
        glist.setPrev((prev-count)>0?prev-count:null);
      }
      return glist;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      log.error("exception caught:"+ex.getMessage());
      throw new RuntimeException(ex);
    }
  }
  
  @POST
  @Path("groups/new")
  @Produces("application/json")
  public String addGroup(@FormParam("name") String name, @FormParam("description") String description)
  {
    log.info("group name:"+name+" description:"+description);
    try
    {
      return Group.addGroup(name).getId();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      log.error("exception caught:"+ex.getMessage());
      throw new RuntimeException(ex);
    }
  }
  
}
