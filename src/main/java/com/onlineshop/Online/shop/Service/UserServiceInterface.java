package com.onlineshop.Online.shop.Service;

import com.onlineshop.Online.shop.Model.User;

public interface UserServiceInterface {
    User saveUser(User user);
    User getUserById(Long id);
}
