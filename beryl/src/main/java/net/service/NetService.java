package net.service;

import net.content.Group;
import net.content.User;

public interface NetService {
  User login(String email, String password);
  GroupList browseGroups(Long prev, Long next, Long count);
  Group addGroup(String name, String description);
}
