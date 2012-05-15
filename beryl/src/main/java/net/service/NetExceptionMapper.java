package net.service;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NetExceptionMapper implements ExceptionMapper<RuntimeException>
{

  public Response toResponse(RuntimeException exception)
  {
    return Response.status(500)
                   .entity("{ error: \""+exception.getMessage()+"\" }\n")
                   .build();
  }
}
