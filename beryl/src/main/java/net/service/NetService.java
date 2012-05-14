package net.service;

import java.util.List;

import net.content.User;
import net.content.Group;

public interface NetService {
  User login(String email, String password);
  List<Group> browseGroups(String prev,String next);
}
