package net.service;

import net.content.User;

public interface NetService {
  User login(String email, String password);
  GroupList browseGroups(Long prev, Long next, Long count);
  String addGroup(String name, String description);
}
