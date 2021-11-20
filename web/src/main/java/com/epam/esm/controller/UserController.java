package com.epam.esm.controller;

import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.page.UserLinksBuilder;
import com.epam.esm.service.InvalidPageException;
import com.epam.esm.service.PageOutOfBoundsException;
import com.epam.esm.service.ResourceNotFoundException;
import com.epam.esm.service.UserService;
import com.epam.esm.validation.InvalidResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST API users controller
 * handles HTTP-request that related to
 * user resource
 * <p>
 * Use JSON format to handle requests and responses
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserLinksBuilder linksBuilder;

    @Autowired
    public UserController(UserService userService, UserLinksBuilder linksBuilder) {
        this.userService = userService;
        this.linksBuilder = linksBuilder;
    }

    /**
     * Finds users on page
     *
     * @param offset page offset
     * @param limit  page limit
     * @return users on page
     * @throws ResourceNotFoundException if user not found
     * @throws PageOutOfBoundsException  if offset is greater then total elements
     * @throws InvalidPageException      if offset or limit is negative
     */
    @GetMapping
    public PagedModel<User> showUsers(@RequestParam(required = false, defaultValue = "0") int offset,
                                      @RequestParam(required = false, defaultValue = "10") int limit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        List<User> usersOnPage = userService.findPage(offset, limit);
        return linksBuilder.buildPageLinks(usersOnPage, offset, limit);
    }

    /**
     * Finds user that has passed id
     *
     * @param userId id of user
     * @return user that has passed id
     * @throws ResourceNotFoundException if there is no user that has passed id
     * @throws PageOutOfBoundsException  if page number is less then one and greater then pages amount
     */
    @GetMapping("/{userId}")
    public User showUser(@PathVariable Long userId) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        User user = userService.findById(userId);
        return linksBuilder.buildLinks(user);
    }

    /**
     * Finds user's orders first page
     *
     * @param userId id of user whose orders need to be found
     * @return list of user's orders on first page
     * @throws ResourceNotFoundException if user is not found
     * @throws PageOutOfBoundsException  if page number is less then one and greater then pages amount
     */
    @GetMapping("/{userId}/orders")
    public PagedModel<Order> showUserOrders(@PathVariable long userId, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        User user = userService.findById(userId);
        List<Order> orders = userService.findUserOrderPage(user, offset, limit);
        return linksBuilder.buildUserOrdersPageLinks(user, orders, offset, limit);
    }

    /**
     * Finds user order that has passed id
     *
     * @param userId  user id
     * @param orderId order id
     * @return found order that has passed id
     * @throws ResourceNotFoundException if order is not found
     * @throws PageOutOfBoundsException  if offset is greater then total elements
     * @throws InvalidPageException      if offset or limit is invalid
     */
    @GetMapping("/{userId}/orders/{orderId}")
    public Order showUserOrder(@PathVariable long userId, @PathVariable long orderId) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        User foundUser = userService.findById(userId);
        Order foundOrder = userService.findUserOrder(foundUser, orderId);
        return linksBuilder.buildUserOrderLinks(foundUser, foundOrder);
    }

    /**
     * Finds richest user
     *
     * @return found richest user
     * @throws ResourceNotFoundException if user is not found
     * @throws PageOutOfBoundsException  if page number is less then one and greater then pages amount
     */
    @GetMapping("/richest")
    public User showRichestUser() throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        User richestUser = userService.findRichestUser();
        return linksBuilder.buildLinks(richestUser);
    }

    /**
     * Finds richest user popular tag
     *
     * @return richest user popular tag
     */
    @GetMapping("/richest/popularTag")
    public Tag showRichestUserPopularTag() {
        Tag popularTag = userService.findRichestUserPopularTag();
        Link selfLink = linkTo(methodOn(UserController.class).showRichestUserPopularTag()).withSelfRel();
        popularTag.add(selfLink);
        return popularTag;
    }

    /**
     * Makes user signup
     *
     * @param user than need to be signed up
     * @return signed up user
     * @throws InvalidResourceException  if user is invalid
     * @throws InvalidPageException      if offset is negative or limit is equal or less then zero
     * @throws ResourceNotFoundException if saved user is not found
     * @throws PageOutOfBoundsException  if offset is greater then users amount
     */
    @PostMapping("/signup")
    public User saveUser(@RequestBody User user) throws InvalidResourceException, InvalidPageException, ResourceNotFoundException, PageOutOfBoundsException {
        User savedUser = userService.save(user);
        return linksBuilder.buildLinks(savedUser);
    }
}
