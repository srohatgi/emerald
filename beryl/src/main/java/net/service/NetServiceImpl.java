package net.service;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.content.Group;
import net.content.User;

@Path("user")
public class NetServiceImpl implements NetService {
  private static final Log log = LogFactory.getLog(NetServiceImpl.class);
  
  @POST
  @Path("login")
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

  public List<Group> browseGroups(String prev, String next)
  {
    log.info("prev="+prev+" next="+next);
    try
    {
      return Group.browse(prev,next);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      log.error("exception caught:"+ex.getMessage());
      return null;
    }
  }
  
}
