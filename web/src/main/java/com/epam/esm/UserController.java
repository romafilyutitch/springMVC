package com.epam.esm;

import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.service.CertificateNotFoundException;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.TagNotFoundException;
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

@RestController()
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public UserController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping
    public PagedModel<User> showUsers(@RequestParam(value = "page", required = false, defaultValue = "1") long page) throws TagNotFoundException, CertificateNotFoundException {
        List<User> users = userService.findAll(page);
        for (User user : users) {
            Link ordersLink = linkTo(methodOn(UserController.class).showUserOrders(user.getId(), 1)).withRel("orders");
            user.add(ordersLink);
        }
        Link selfLink = linkTo(methodOn(UserController.class).showUsers(page)).withSelfRel();
        Link firstPageLink = linkTo(methodOn(UserController.class).showUsers(1)).withRel("firstPage");
        Link lastPageLink = linkTo(methodOn(UserController.class).showUsers(userService.getTotalPages())).withRel("lastPage");
        Link nextPageLink = linkTo(methodOn(UserController.class).showUsers(page + 1)).withRel("nextPage");
        Link previousPageLink = linkTo(methodOn(UserController.class).showUsers(page - 1)).withRel("previousPage");
        List<Link> links = new ArrayList<>();
        links.add(selfLink);
        links.add(firstPageLink);
        links.add(lastPageLink);
        links.add(nextPageLink);
        links.add(previousPageLink);
        if (page == 1) {
            links.remove(previousPageLink);
        } else if (page == userService.getTotalPages()) {
            links.remove(nextPageLink);
        }
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(users.size(), page, userService.getTotalElements(), userService.getTotalPages());
        return users.isEmpty() ? PagedModel.empty(metadata) : PagedModel.of(users, metadata, links);
    }

    @GetMapping("/{userId}")
    public User showUser(@PathVariable Long userId) throws TagNotFoundException, CertificateNotFoundException {
        User user = userService.findById(userId).get();
        Link selfLink = linkTo(methodOn(UserController.class).showUser(userId)).withSelfRel();
        Link ordersLink = linkTo(methodOn(UserController.class).showUserOrders(user.getId(), 1)).withRel("orders");
        user.add(selfLink, ordersLink);
        return user;
    }

    @GetMapping("/{userId}/orders")
    public PagedModel<Order> showUserOrders(@PathVariable Long userId,
                                      @RequestParam(value = "page", required = false, defaultValue = "1") long page) throws TagNotFoundException, CertificateNotFoundException {
        Optional<User> optionalUser = userService.findById(userId);
        User user = optionalUser.get();
        List<Order> orders = orderService.findUserOrders(user, page);
        for (Order order : orders) {
            Link certificateLink = linkTo(methodOn(CertificateController.class).showCertificate(order.getCertificate().getId())).withRel("certificate");
            order.add(certificateLink);
        }
        Link selfLink = linkTo(methodOn(UserController.class).showUserOrders(userId, page)).withSelfRel();
        Link firstPage = linkTo(methodOn(UserController.class).showUserOrders(userId, 1)).withRel("firstPage");
        Link lastPage = linkTo(methodOn(UserController.class).showUserOrders(userId, orderService.getUserOrdersTotalPages(user))).withRel("lastPage");
        Link nextPage = linkTo(methodOn(UserController.class).showUserOrders(userId, page + 1)).withRel("nextPage");
        Link previousPage = linkTo(methodOn(UserController.class).showUserOrders(userId, page - 1)).withRel("previousPage");
        List<Link> links = new ArrayList<>();
        links.add(selfLink);
        links.add(firstPage);
        links.add(lastPage);
        links.add(nextPage);
        links.add(previousPage);
        if (page == 1) {
            links.remove(previousPage);
        } else if (page == orderService.getUserOrdersTotalPages(user)) {
            links.remove(nextPage);
        }
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(orders.size(), page, orderService.getUserOrdersTotalElements(user), orderService.getUserOrdersTotalPages(user));
        return orders.isEmpty() ? PagedModel.empty(metadata) : PagedModel.of(orders, metadata, links);
    }

    @GetMapping("/richest")
    public User showRichestUser() throws TagNotFoundException, CertificateNotFoundException {
        User richestUser = userService.findRichestUser();
        Link userOrdersLink = linkTo(methodOn(UserController.class).showUserOrders(richestUser.getId(), 1)).withRel("orders");
        Link selfLink = linkTo(methodOn(UserController.class).showRichestUser()).withSelfRel();
        richestUser.add(selfLink);
        richestUser.add(userOrdersLink);
        return richestUser;
    }

    @GetMapping("/richest/popularTag")
    public Tag showRichestUserPopularTag() {
        Tag popularTag = userService.findRichestUserPopularTag();
        Link selfLink = linkTo(methodOn(UserController.class).showRichestUserPopularTag()).withSelfRel();
        popularTag.add(selfLink);
        return popularTag;
    }
}
