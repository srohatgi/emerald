package net.service;

import net.content.User;

public interface NetService {
  User login(String email, String password);
}
