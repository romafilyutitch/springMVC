package com.epam.esm;

import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.service.PageOutOfBoundsException;
import com.epam.esm.service.ResourceNotFoundException;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST API users controller
 * handles HTTP-request that related to
 * user resource
 *
 * Use JSON format to handle requests and responses
 */
@RestController()
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Shows user orders on first page
     * @return list of orders on first page
     * @throws PageOutOfBoundsException if page number is less of one and greater then pages amount
     * @throws ResourceNotFoundException if user is not found
     */
    @GetMapping
    public PagedModel<User> showUsers() throws PageOutOfBoundsException, ResourceNotFoundException {
        List<User> users = userService.findPage(1);
        return makeUserPage(1, users);
    }

    /**
     * Finds users on passed page
     * @param page page of users on page
     * @return list of users on passed page
     * @throws PageOutOfBoundsException if page number is less then one and greater then pages amount
     * @throws ResourceNotFoundException if user is not found
     */
    @GetMapping("/page/{page}")
    public PagedModel<User> showUsersPage(@PathVariable int page) throws PageOutOfBoundsException, ResourceNotFoundException {
        List<User> users = userService.findPage(page);
        return makeUserPage(page, users);
    }

    /**
     * Finds user that has passed id
     * @param userId id of user
     * @return user that has passed id
     * @throws ResourceNotFoundException if there is no user that has passed id
     * @throws PageOutOfBoundsException if page number is less then one and greater then pages amount
     */
    @GetMapping("/{userId}")
    public User showUser(@PathVariable Long userId) throws ResourceNotFoundException, PageOutOfBoundsException {
        User user = userService.findById(userId);
        Link selfLink = linkTo(methodOn(UserController.class).showUser(userId)).withSelfRel();
        Link ordersLink = linkTo(methodOn(UserController.class).showUserOrdersPage(user.getId(), 1)).withRel("orders");
        user.add(selfLink, ordersLink);
        return user;
    }

    /**
     * Finds user's orders first page
     * @param userId id of user whose orders need to be found
     * @return list of user's orders on first page
     * @throws ResourceNotFoundException if user is not found
     * @throws PageOutOfBoundsException if page number is less then one and greater then pages amount
     */
    @GetMapping("/{userId}/orders")
    public PagedModel<Order> showUserOrders(@PathVariable long userId) throws ResourceNotFoundException, PageOutOfBoundsException {
        User user = userService.findById(userId);
        List<Order> orders = userService.findUserOrderPage(user, 1);
        return makeUserOrdersPage(1, user, orders);
    }

    /**
     * Finds user's orders on passed page
     * @param userId id of users whose orders need to be found
     * @param page page of user's orders
     * @return list of user's order on passed page
     * @throws ResourceNotFoundException if user is not found
     * @throws PageOutOfBoundsException if page number is less then one and greater then pages amount
     */
    @GetMapping("/{userId}/orders/{page}")
    public PagedModel<Order> showUserOrdersPage(@PathVariable long userId, @PathVariable int page) throws ResourceNotFoundException, PageOutOfBoundsException {
        User user = userService.findById(userId);
        List<Order> orders = userService.findUserOrderPage(user, page);
        return makeUserOrdersPage(page, user, orders);
    }

    /**
     * Finds richest user
     * @return found richest user
     * @throws ResourceNotFoundException if user is not found
     * @throws PageOutOfBoundsException if page number is less then one and greater then pages amount
     */
    @GetMapping("/richest")
    public User showRichestUser() throws ResourceNotFoundException, PageOutOfBoundsException {
        User richestUser = userService.findRichestUser();
        Link userOrdersLink = linkTo(methodOn(UserController.class).showUserOrdersPage(richestUser.getId(), 1)).withRel("orders");
        Link selfLink = linkTo(methodOn(UserController.class).showRichestUser()).withSelfRel();
        richestUser.add(selfLink);
        richestUser.add(userOrdersLink);
        return richestUser;
    }

    /**
     * Finds richest user popular tag
     * @return richest user popular tag
     */
    @GetMapping("/richest/popularTag")
    public Tag showRichestUserPopularTag() {
        Tag popularTag = userService.findRichestUserPopularTag();
        Link selfLink = linkTo(methodOn(UserController.class).showRichestUserPopularTag()).withSelfRel();
        popularTag.add(selfLink);
        return popularTag;
    }

    private PagedModel<User> makeUserPage(int page, List<User> users) throws ResourceNotFoundException, PageOutOfBoundsException {
        for (User user : users) {
            Link ordersLink = linkTo(methodOn(UserController.class).showUserOrdersPage(user.getId(), 1)).withRel("orders");
            user.add(ordersLink);
        }
        Link selfLink = linkTo(methodOn(UserController.class).showUsersPage(page)).withSelfRel();
        Link firstPageLink = linkTo(methodOn(UserController.class).showUsersPage(1)).withRel("firstPage");
        Link lastPageLink = linkTo(methodOn(UserController.class).showUsersPage(userService.getTotalPages())).withRel("lastPage");
        Link nextPageLink = linkTo(methodOn(UserController.class).showUsersPage(page + 1)).withRel("nextPage");
        Link previousPageLink = linkTo(methodOn(UserController.class).showUsersPage(page - 1)).withRel("previousPage");
        List<Link> links = new ArrayList<>();
        links.add(selfLink);
        links.add(firstPageLink);
        links.add(lastPageLink);
        links.add(nextPageLink);
        links.add(previousPageLink);
        if (page == 1) {
            links.remove(previousPageLink);
        }
        if (page == userService.getTotalPages()) {
            links.remove(nextPageLink);
        }
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(users.size(), page, userService.getTotalElements(), userService.getTotalPages());
        return users.isEmpty() ? PagedModel.empty(metadata) : PagedModel.of(users, metadata, links);
    }

    private PagedModel<Order> makeUserOrdersPage(int page, User user, List<Order> orders) throws ResourceNotFoundException, PageOutOfBoundsException {
        for (Order order : orders) {
            Link certificateLink = linkTo(methodOn(CertificateController.class).showCertificate(order.getCertificate().getId())).withRel("certificate");
            order.add(certificateLink);
        }
        Link selfLink = linkTo(methodOn(UserController.class).showUserOrdersPage(user.getId(), page)).withSelfRel();
        Link firstPage = linkTo(methodOn(UserController.class).showUserOrdersPage(user.getId(), 1)).withRel("firstPage");
        Link lastPage = linkTo(methodOn(UserController.class).showUserOrdersPage(user.getId(), userService.getUserOrdersTotalPages(user))).withRel("lastPage");
        Link nextPage = linkTo(methodOn(UserController.class).showUserOrdersPage(user.getId(), page + 1)).withRel("nextPage");
        Link previousPage = linkTo(methodOn(UserController.class).showUserOrdersPage(user.getId(), page - 1)).withRel("previousPage");
        List<Link> links = new ArrayList<>();
        links.add(selfLink);
        links.add(firstPage);
        links.add(lastPage);
        links.add(nextPage);
        links.add(previousPage);
        if (page == 1) {
            links.remove(previousPage);
        }
        if (page == userService.getUserOrdersTotalPages(user)) {
            links.remove(nextPage);
        }
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(orders.size(), page, userService.getUserOrdersTotalElements(user), userService.getUserOrdersTotalPages(user));
        return orders.isEmpty() ? PagedModel.empty(metadata) : PagedModel.of(orders, metadata, links);
    }
}
