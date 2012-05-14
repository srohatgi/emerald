package net.service;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Date;
import java.util.List;

import net.content.User;

@Path("user")
public class NetServiceImpl implements NetService {
  @POST
  @Path("login")
  @Produces("application/json")
  public User login(@QueryParam("email") String email, @QueryParam("password") String password) {
    try 
    {
      return new User("sumeet_rohatgi@hotmail.com","test12");
    }
    catch (Exception ex) 
    {
      return null;
    }
  }
}
