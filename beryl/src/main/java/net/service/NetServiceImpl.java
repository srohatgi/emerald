package net.service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.content.User;

@Path("user")
public class NetServiceImpl implements NetService {
  Log log = LogFactory.getLog(NetServiceImpl.class);
  
  @POST
  @Path("login")
  @Produces("application/json")
  public User login(@QueryParam("email") String email, @QueryParam("password") String password) 
  {
    return new User();/*
    try 
    {
      log.info("email="+email+" password="+password);
      return User.fetchByLogin(email,password);
    }
    catch (Exception ex) 
    {
      return null;
    }*/
  }
}
