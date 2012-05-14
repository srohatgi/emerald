package net.service.bootstrap;

import net.service.NetServiceImpl;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class ApplicationBootstrap extends Application {
  Set<Object> singletons = new HashSet<Object>();

  public ApplicationBootstrap() {
    singletons.add(new NetServiceImpl());
  }

  @Override
  public Set<Class<?>> getClasses() {
    HashSet<Class<?>> set = new HashSet<Class<?>>();
    return set;
  }

  @Override
  public Set<Object> getSingletons() {
    return singletons;
  }
}
