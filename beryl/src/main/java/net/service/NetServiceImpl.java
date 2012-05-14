package net.service;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.content.User;

@Path("user")
public class NetServiceImpl implements NetService {
  Log log = LogFactory.getLog(NetServiceImpl.class);
  
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
}
