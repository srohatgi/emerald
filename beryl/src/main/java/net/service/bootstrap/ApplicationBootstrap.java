package net.service.bootstrap;

import net.service.NetExceptionMapper;
import net.service.NetServiceImpl;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class ApplicationBootstrap extends Application {
  Set<Object> singletons = new HashSet<Object>();
  HashSet<Class<?>> set = new HashSet<Class<?>>();

  public ApplicationBootstrap() {
    singletons.add(new NetServiceImpl());
    set.add(NetExceptionMapper.class);
  }

  @Override
  public Set<Class<?>> getClasses() {
    return set;
  }

  @Override
  public Set<Object> getSingletons() {
    return singletons;
  }
}
