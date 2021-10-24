package com.epam.esm;

import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> showUsers() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public User showUser(@PathVariable Long userId) {
        return userService.findById(userId).get();
    }

    @GetMapping("/{userId}/orders")
    public List<Order> showUserOrders(@PathVariable Long userId) {
        Optional<User> optionalUser = userService.findById(userId);
        User user = optionalUser.get();
        return user.getOrders();
    }

    @GetMapping("/richest")
    public User showRichestUser() {
        return userService.findRichestUser();
    }

    @GetMapping("/richest/popularTag")
    public Tag showRichestUserPopularTag() {
        return userService.findRichestUserPopularTag();
    }
}
