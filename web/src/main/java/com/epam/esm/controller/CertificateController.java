package com.epam.esm.controller;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.page.CertificateLinksBuilder;
import com.epam.esm.page.UserLinksBuilder;
import com.epam.esm.service.*;
import com.epam.esm.validation.InvalidResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Certificate REST controller.
 * Supplies REST API and handles request from client.
 * Consumes requests and produces responses in JSON format
 */
@RestController
@RequestMapping("/certificates")
public class CertificateController {
    private final CertificateService certificateService;
    private final UserService userService;
    private final CertificateLinksBuilder certificateLinksBuilder;
    private final UserLinksBuilder userLinksBuilder;

    @Autowired
    public CertificateController(CertificateService certificateService, UserService userService, CertificateLinksBuilder certificateLinksBuilder, UserLinksBuilder userLinksBuilder) {
        this.certificateService = certificateService;
        this.userService = userService;
        this.certificateLinksBuilder = certificateLinksBuilder;
        this.userLinksBuilder = userLinksBuilder;
    }

    /**
     * Finds certificates that match passed parameters
     * @param parameters find parameters (tagsNames, partOfName, partOfDescription, sortByName, sortByDate, offset, limit)
     * @param offset pagination offset
     * @param limit pagination limit
     * @return found certificates
     * @throws ResourceNotFoundException if certificate not found
     * @throws PageOutOfBoundsException if offset is greater that total elements
     * @throws InvalidPageException if offset or limit is invalid
     */
    @GetMapping
    public PagedModel<Certificate> showCertificates(@RequestParam(required = false) LinkedHashMap<String, String> parameters,
                                                         @RequestParam(defaultValue = "0") int offset,
                                                         @RequestParam(defaultValue = "10") int limit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        List<Certificate> foundCertificates = certificateService.findAllWithParameters(parameters, offset, limit);
        return certificateLinksBuilder.buildPageLinks(foundCertificates, parameters, offset, limit);
    }

    /**
     * Finds certificate that has passed id
     *
     * @param id of certificate that need to be found
     * @return certificate that has passed id
     * @throws ResourceNotFoundException if certificate is not found
     * @throws PageOutOfBoundsException  if page number is less then one and greater then pages amount
     */
    @GetMapping("/{id}")
    public Certificate showCertificate(@PathVariable("id") long id) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        Certificate foundCertificate = certificateService.findById(id);
        return certificateLinksBuilder.buildLinks(foundCertificate);
    }

    /**
     * Creates new certificate
     *
     * @param certificate certificate that need to be saved
     * @return controller response in JSON format and CREATED status code
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Certificate saveCertificate(@RequestBody Certificate certificate) throws InvalidResourceException, ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        Certificate savedCertificate = certificateService.save(certificate);
        return certificateLinksBuilder.buildLinks(savedCertificate);
    }

    /**
     * Updates saved certificate
     *
     * @param id          id of certificate that need to be updated
     * @param certificate certificate values that need to be founded
     * @return updated certificate
     * @throws ResourceNotFoundException if certificate is not found
     * @throws InvalidResourceException  if certificate is invalid
     * @throws PageOutOfBoundsException  if page number is less then one and greater then pages amount
     */
    @PostMapping("/{id}")
    public Certificate updateCertificate(@PathVariable("id") long id, @RequestBody Certificate certificate) throws ResourceNotFoundException, InvalidResourceException, PageOutOfBoundsException, InvalidPageException {
        Certificate foundCertificate = certificateService.findById(id);
        certificate.setId(foundCertificate.getId());
        Certificate updatedCertificate = certificateService.update(certificate);
        return certificateLinksBuilder.buildLinks(updatedCertificate);
    }

    /**
     * Deletes certificate that has passed id
     *
     * @param id id of certificate that need to be deleted
     * @throws ResourceNotFoundException if certificate not found
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCertificate(@PathVariable("id") long id) throws ResourceNotFoundException {
        Certificate foundCertificate = certificateService.findById(id);
        certificateService.delete(foundCertificate);
    }

    /**
     * Finds passed certificate tags on first page
     *
     * @param id id of certificate whose tags need to be found
     * @return list of certificate tags on first page
     * @throws ResourceNotFoundException if certificate is not found
     * @throws PageOutOfBoundsException  if page number is less then one and greater then pages amount
     */
    @GetMapping("/{id}/tags")
    public PagedModel<Tag> showCertificateTags(@PathVariable("id") long id, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        Certificate foundCertificate = certificateService.findById(id);
        List<Tag> tags = certificateService.findCertificateTagsPage(foundCertificate, offset, limit);
        return certificateLinksBuilder.buildCertificateTagsPage(foundCertificate, tags, offset, limit);
    }

    /**
     * Finds certificate tag
     *
     * @param id    id of certificate whose tag need to be found
     * @param tagId id of tag that need to be found
     * @return found tag
     * @throws ResourceNotFoundException if certificate or tag is not found
     */
    @GetMapping("/{id}/tags/{tagId}")
    public Tag showCertificateTag(@PathVariable("id") long id, @PathVariable("tagId") long tagId) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        Certificate foundCertificate = certificateService.findById(id);
        Tag foundTag = certificateService.findCertificateTag(foundCertificate, tagId);
        return certificateLinksBuilder.buildTagLinks(foundCertificate, foundTag);
    }

    /**
     * Adds new tags to certificate
     *
     * @param id   id of certificate that need to be added new certificate
     * @param tags that need to be added to certificate
     * @return certificate with added tags
     * @throws ResourceNotFoundException if certificate is not found
     * @throws InvalidResourceException  if passed tag is invalid
     * @throws PageOutOfBoundsException  if page number is less then one and greater then page number
     */
    @PostMapping("/{id}/tags")
    @ResponseStatus(HttpStatus.CREATED)
    public Certificate addTagToCertificate(@PathVariable("id") long id, @RequestBody List<Tag> tags) throws ResourceNotFoundException, InvalidResourceException, PageOutOfBoundsException, InvalidPageException {
        Certificate foundCertificate = certificateService.findById(id);
        Certificate updatedCertificate = certificateService.addTags(foundCertificate, tags);
        return certificateLinksBuilder.buildLinks(updatedCertificate);
    }

    /**
     * Deletes certificate tags
     *
     * @param id    id of certificate whose tag need to be deleted
     * @param tagId if of tag that need to be deleted
     * @throws ResourceNotFoundException if certificate or tag is not found
     */
    @DeleteMapping("/{id}/tags/{tagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCertificateTag(@PathVariable("id") long id, @PathVariable("tagId") long tagId) throws ResourceNotFoundException {
        Certificate foundCertificate = certificateService.findById(id);
        Tag foundTag = certificateService.findCertificateTag(foundCertificate, tagId);
        certificateService.deleteCertificateTag(foundCertificate, foundTag);
    }

    /**
     * Finds certificate order
     *
     * @param id id of certificate whose order need to be found
     * @return certificate order
     * @throws ResourceNotFoundException if certificate or order is not found
     * @throws PageOutOfBoundsException  if page number is less then 1 and greater then pages amount
     */
    @GetMapping("/{id}/orders")
    public PagedModel<Order> showCertificateOrders(@PathVariable Long id, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        Certificate foundCertificate = certificateService.findById(id);
        List<Order> foundOrders = certificateService.findCertificateOrders(foundCertificate, offset, limit);
        return certificateLinksBuilder.buildCertificateOrdersPage(foundCertificate, foundOrders, offset, limit);
    }

    /**
     * Finds certificate order with passed id
     * @param id certificate id
     * @param orderId order id
     * @return certificate order that ahas passed id
     * @throws ResourceNotFoundException if certificate or order is not found
     * @throws PageOutOfBoundsException if page offset is out of bounds
     * @throws InvalidPageException  in page offset or limit is negative
     */
    @GetMapping("/{id}/orders/{orderId}")
    public Order showCertificateOrder(@PathVariable long id, @PathVariable long orderId) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        Certificate foundCertificate = certificateService.findById(id);
        Order foundOrder = certificateService.findCertificateOrder(foundCertificate, orderId);
        User foundUser = userService.findOrderUser(foundOrder);
        return userLinksBuilder.buildUserOrderLinks(foundUser, foundOrder);
    }

    /**
     * Makes certificate order
     *
     * @param id   id of certificate that need to be ordered
     * @param user the need to ordered certificate
     * @return made certificate order
     * @throws ResourceNotFoundException if certificate is not found
     * @throws PageOutOfBoundsException  if page number is less then one and greater then pages amount
     */
    @PostMapping("/{id}/orders")
    public Order makeOrder(@PathVariable Long id, @RequestBody User user) throws ResourceNotFoundException, PageOutOfBoundsException, InvalidPageException {
        Certificate foundCertificate = certificateService.findById(id);
        User foundUser = userService.findById(user.getId());
        Order savedOrder = userService.orderCertificate(foundUser, foundCertificate);
        return userLinksBuilder.buildUserOrderLinks(foundUser, savedOrder);
    }
}
