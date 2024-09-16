package com.projectcloud.project_cloud.service;

import com.projectcloud.project_cloud.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private static List<User> Users = new ArrayList<>();

    public List<User> getAllUsers() {
        return UserService.Users;
    }

    public User getUser(int id){
        return findUser(id);
    }

    public User createUser (User item) {
        UserService.Users.add(item);
        return item;
    }

    private User findUser(int id) {
        User response = null;

        for (User user : Users) {
            if (user.getId() == id) {
                response = user;
                break;
            }
        }
        return response;
    }
}
