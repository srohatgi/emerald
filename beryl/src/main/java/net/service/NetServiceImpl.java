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
public class NetServiceImpl implements NetService {
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
      return null;
    }
  }

  @GET
  @Path("groups")
  @Produces("application/json")
  public List<Group> browseGroups(@QueryParam("prev") String prev, @QueryParam("next") String next)
  {
    log.info("prev="+prev+" next="+next);
    try
    {
      return Group.browse(prev!=null?Long.parseLong(prev):null,next!=null?Long.parseLong(next):null);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      log.error("exception caught:"+ex.getMessage());
      return null;
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
      return null;
    }
  }
  
}
