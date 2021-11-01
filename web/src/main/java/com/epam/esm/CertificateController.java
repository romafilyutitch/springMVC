package com.epam.esm;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.OrderNotFoundException;
import com.epam.esm.service.PageOutOfBoundsException;
import com.epam.esm.service.ResourceNotFoundException;
import com.epam.esm.service.UserService;
import com.epam.esm.validation.InvalidResourceException;
import org.springframework.context.MessageSource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

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

    public CertificateController(CertificateService certificateService, UserService userService) {
        this.certificateService = certificateService;
        this.userService = userService;
    }

    /**
     * REST API method to find certificates
     * that match passed find parameters
     * @param findParameters map of parameters keys and values that filter certificates
     * @return list of certificate that match passed parameters
     * @throws PageOutOfBoundsException if page is less then one and greater then pages amount
     * @throws ResourceNotFoundException if certificate is not found
     */
    @GetMapping("/find")
    public CollectionModel<Certificate> findCertificateWithParameters(@RequestParam LinkedHashMap<String, String> findParameters) throws PageOutOfBoundsException, ResourceNotFoundException {
        List<Certificate> foundCertificates = certificateService.findAllWithParameters(findParameters);
        for (Certificate certificate : foundCertificates) {
            if (certificate.getTags().size() > 0) {
                Link tagsLink = linkTo(methodOn(CertificateController.class).showCertificateTags(certificate.getId())).withRel("tags");
                certificate.add(tagsLink);
            }
            Link orderLink = linkTo(methodOn(CertificateController.class).showCertificateOrder(certificate.getId())).withRel("order");
            certificate.add(orderLink);
        }
        Link selfLink = linkTo(methodOn(CertificateController.class).findCertificateWithParameters(findParameters)).withSelfRel();
        return foundCertificates.isEmpty() ? CollectionModel.empty(selfLink) : CollectionModel.of(foundCertificates, selfLink);
    }

    /**
     * Show certificate first page
     * @return list of certificate on first page
     * @throws PageOutOfBoundsException if page number is less then one and greater than pages amount
     * @throws ResourceNotFoundException if there certificate is not found
     */
    @GetMapping
    public PagedModel<Certificate> showCertificates() throws PageOutOfBoundsException, ResourceNotFoundException {
        List<Certificate> foundPage = certificateService.findPage(1);
        return makeCertificatePage(1, foundPage);
    }

    /**
     * Finds certificates on specified page
     * @param page of certificates
     * @return list of certificate on passed page
     * @throws ResourceNotFoundException if certificate is not found
     * @throws PageOutOfBoundsException if page number is less then 1 and greater then pages amount
     */
    @GetMapping("/page/{page}")
    public PagedModel<Certificate> showCertificatePage(@PathVariable int page) throws ResourceNotFoundException, PageOutOfBoundsException {
        List<Certificate> foundPage = certificateService.findPage(page);
        return makeCertificatePage(page, foundPage);
    }

    /**
     * Finds certificate that has passed id
     * @param id of certificate that need to be found
     * @return certificate that has passed id
     * @throws ResourceNotFoundException if certificate is not found
     * @throws PageOutOfBoundsException if page number is less then one and greater then pages amount
     */
    @GetMapping("/{id}")
    public Certificate showCertificate(@PathVariable("id") long id) throws ResourceNotFoundException, PageOutOfBoundsException {
        Certificate foundCertificate = certificateService.findById(id);
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificate(id)).withSelfRel();
        foundCertificate.add(selfLink);
        if (foundCertificate.getTags().size() > 0) {
            Link tagsLink = linkTo(methodOn(CertificateController.class).showCertificateTagsPage(id, 1)).withRel("tags");
            foundCertificate.add(tagsLink);
        }
        Link orderLink = linkTo(methodOn(CertificateController.class).showCertificateOrder(foundCertificate.getId())).withRel("order");
        foundCertificate.add(orderLink);
        return foundCertificate;
    }

    /**
     * Creates new certificate
     *
     * @param certificate certificate that need to be saved
     * @return controller response in JSON format and CREATED status code
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Certificate saveCertificate(@RequestBody Certificate certificate) throws InvalidResourceException, ResourceNotFoundException, PageOutOfBoundsException {
        Certificate savedCertificate = certificateService.save(certificate);
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificate(savedCertificate.getId())).withSelfRel();
        savedCertificate.add(selfLink);
        if (savedCertificate.getTags().size() > 0) {
            Link tagLink = linkTo(methodOn(CertificateController.class).showCertificateTagsPage(savedCertificate.getId(), 1)).withRel("tags");
            savedCertificate.add(tagLink);
        }
        return savedCertificate;
    }

    /**
     * Updates saved certificate
     * @param id id of certificate that need to be updated
     * @param certificate certificate values that need to be founded
     * @return updated certificate
     * @throws ResourceNotFoundException if certificate is not found
     * @throws InvalidResourceException if certificate is invalid
     * @throws PageOutOfBoundsException if page number is less then one and greater then pages amount
     */
    @PostMapping("/{id}")
    public Certificate updateCertificate(@PathVariable("id") long id, @RequestBody Certificate certificate) throws ResourceNotFoundException, InvalidResourceException, PageOutOfBoundsException {
        Certificate foundCertificate = certificateService.findById(id);
        certificate.setId(foundCertificate.getId());
        Certificate updatedCertificate = certificateService.update(certificate);
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificate(id)).withSelfRel();
        updatedCertificate.add(selfLink);
        if (updatedCertificate.getTags().size() > 0) {
            Link tagsLink = linkTo(methodOn(CertificateController.class).showCertificateTagsPage(id, 1)).withRel("tags");
            updatedCertificate.add(tagsLink);
        }
        return updatedCertificate;
    }

    /**
     * Deletes certificate that has passed id
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
     * @param id id of certificate whose tags need to be found
     * @return list of certificate tags on first page
     * @throws ResourceNotFoundException if certificate is not found
     * @throws PageOutOfBoundsException if page number is less then one and greater then pages amount
     */
    @GetMapping("/{id}/tags")
    public PagedModel<Tag> showCertificateTags(@PathVariable("id") long id) throws ResourceNotFoundException, PageOutOfBoundsException {
        Certificate foundCertificate = certificateService.findById(id);
        List<Tag> tags = certificateService.findCertificateTagsPage(foundCertificate, 1);
        return makeTagsPage(1, foundCertificate, tags);
    }

    /**
     * Finds passed certificate tags on passed page
     * @param id id of certificate whose tags need to be found
     * @param page page of certificate's tags that need to be found
     * @return list of pages on passed page
     * @throws ResourceNotFoundException if certificate is not found
     * @throws PageOutOfBoundsException if page number is less then one and greater then pages amount
     */
    @GetMapping("/{id}/tags/page/{page}")
    public PagedModel<Tag> showCertificateTagsPage(@PathVariable long id, @PathVariable int page) throws ResourceNotFoundException, PageOutOfBoundsException {
        Certificate foundCertificate = certificateService.findById(id);
        List<Tag> tags = certificateService.findCertificateTagsPage(foundCertificate, page);
        return makeTagsPage(page, foundCertificate, tags);
    }

    /**
     * Finds certificate tag
     * @param id id of certificate whose tag need to be found
     * @param tagId id of tag that need to be found
     * @return found tag
     * @throws ResourceNotFoundException if certificate or tag is not found
     */
    @GetMapping("/{id}/tags/{tagId}")
    public Tag showCertificateTag(@PathVariable("id") long id, @PathVariable("tagId") long tagId) throws ResourceNotFoundException {
        Certificate foundCertificate = certificateService.findById(id);
        Tag foundTag = certificateService.findCertificateTag(foundCertificate, tagId);
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificateTag(id, tagId)).withSelfRel();
        foundTag.add(selfLink);
        return foundTag;
    }

    /**
     * Adds new tags to certificate
     * @param id id of certificate that need to be added new certificate
     * @param tags that need to be added to certificate
     * @return certificate with added tags
     * @throws ResourceNotFoundException if certificate is not found
     * @throws InvalidResourceException if passed tag is invalid
     * @throws PageOutOfBoundsException if page number is less then one and greater then page number
     */
    @PostMapping("/{id}/tags")
    @ResponseStatus(HttpStatus.CREATED)
    public Certificate addTagToCertificate(@PathVariable("id") long id, @RequestBody List<Tag> tags) throws ResourceNotFoundException, InvalidResourceException, PageOutOfBoundsException {
        Certificate foundCertificate = certificateService.findById(id);
        Certificate updatedCertificate = certificateService.addTags(foundCertificate, tags);
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificate(updatedCertificate.getId())).withSelfRel();
        updatedCertificate.add(selfLink);
        if (updatedCertificate.getTags().size() > 0) {
            Link tagsLink = linkTo(methodOn(CertificateController.class).showCertificateTagsPage(id, 1)).withRel("tags");
            updatedCertificate.add(tagsLink);
        }
        return updatedCertificate;
    }

    /**
     * Deletes certificate tags
     * @param id  id of certificate whose tag need to be deleted
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
     * @param id id of certificate whose order need to be found
     * @return certificate order
     * @throws ResourceNotFoundException if certificate or order is not found
     * @throws PageOutOfBoundsException if page number is less then 1 and greater then pages amount
     */
    @GetMapping("/{id}/order")
    public Order showCertificateOrder(@PathVariable Long id) throws ResourceNotFoundException, PageOutOfBoundsException {
        Certificate foundCertificate = certificateService.findById(id);
        Order foundOrder = certificateService.findCertificateOrder(foundCertificate);
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificateOrder(id)).withSelfRel();
        Link certificateLink = linkTo(methodOn(CertificateController.class).showCertificate(foundCertificate.getId())).withRel("certificate");
        foundOrder.add(selfLink);
        foundOrder.add(certificateLink);
        return foundOrder;
    }

    /**
     * Makes certificate order
     * @param id id of certificate that need to be ordered
     * @param user the need to ordered certificate
     * @return made certificate order
     * @throws ResourceNotFoundException if certificate is not found
     * @throws PageOutOfBoundsException if page number is less then one and greater then pages amount
     */
    @PostMapping("/{id}/order")
    public Order makeOrder(@PathVariable Long id, @RequestBody User user) throws ResourceNotFoundException, PageOutOfBoundsException {
        Certificate foundCertificate = certificateService.findById(id);
        User foundUser = userService.findById(user.getId());
        Order savedOrder = userService.orderCertificate(foundUser, foundCertificate);
        Link userLink = linkTo(methodOn(UserController.class).showUser(foundUser.getId())).withRel("user");
        Link certificateLink = linkTo(methodOn(CertificateController.class).showCertificate(foundCertificate.getId())).withRel("certificate");
        savedOrder.add(userLink, certificateLink);
        return savedOrder;
    }

    private PagedModel<Certificate> makeCertificatePage(int currentPage, List<Certificate> certificates) throws ResourceNotFoundException, PageOutOfBoundsException {
        for (Certificate certificate : certificates) {
            if (certificate.getTags().size() > 0) {
                Link tagsLink = linkTo(methodOn(CertificateController.class).showCertificateTagsPage(certificate.getId(), 1)).withRel("tags");
                certificate.add(tagsLink);
            }
            Link orderLink = linkTo(methodOn(CertificateController.class).showCertificateOrder(certificate.getId())).withRel("order");
            Link certificateLink = linkTo(methodOn(CertificateController.class).showCertificate(certificate.getId())).withRel("certificate");
            certificate.add(orderLink, certificateLink);
        }
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificatePage(currentPage)).withSelfRel();
        Link firstPageLink = linkTo(methodOn(CertificateController.class).showCertificatePage(1)).withRel("firstPage");
        Link lastPageLink = linkTo(methodOn(CertificateController.class).showCertificatePage(certificateService.getTotalPages())).withRel("lastPage");
        Link nextPageLink = linkTo(methodOn(CertificateController.class).showCertificatePage(currentPage + 1)).withRel("nextPage");
        Link previousPageLink = linkTo(methodOn(CertificateController.class).showCertificatePage(currentPage - 1)).withRel("previousPage");
        List<Link> links = new ArrayList<>();
        links.add(selfLink);
        links.add(firstPageLink);
        links.add(lastPageLink);
        links.add(previousPageLink);
        links.add(nextPageLink);
        if (currentPage == 1) {
            links.remove(previousPageLink);
        }
        if (currentPage == certificateService.getTotalPages()) {
            links.remove(nextPageLink);
        }
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(certificates.size(), currentPage, certificateService.getTotalElements(), certificateService.getTotalPages());
        return certificates.isEmpty() ? PagedModel.empty(metadata) : PagedModel.of(certificates, metadata, links);
    }

    private PagedModel<Tag> makeTagsPage(int page, Certificate certificate, List<Tag> foundTags) throws ResourceNotFoundException, PageOutOfBoundsException {
        for (Tag tag : foundTags) {
            Link tagLink = linkTo(methodOn(CertificateController.class).showCertificateTag(certificate.getId(), tag.getId())).withRel("tag");
            tag.add(tagLink);
        }
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificateTagsPage(certificate.getId(), page)).withSelfRel();
        Link firstPage = linkTo(methodOn(CertificateController.class).showCertificateTagsPage(certificate.getId(), 1)).withRel("firstPage");
        Link lastPage = linkTo(methodOn(CertificateController.class).showCertificateTagsPage(certificate.getId(), certificateService.getCertificateTagsTotalPages(certificate))).withRel("lastPage");
        Link previousPage = linkTo(methodOn(CertificateController.class).showCertificateTagsPage(certificate.getId(), page - 1)).withRel("previousPage");
        Link nextPage = linkTo(methodOn(CertificateController.class).showCertificateTagsPage(certificate.getId(), page + 1)).withRel("nextPage");
        List<Link> links = new ArrayList<>();
        links.add(selfLink);
        links.add(firstPage);
        links.add(lastPage);
        links.add(previousPage);
        links.add(nextPage);
        if (page == 1) {
            links.remove(previousPage);
        }
        if (page == certificateService.getCertificateTagsTotalPages(certificate)) {
            links.remove(nextPage);
        }
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(foundTags.size(), page, certificateService.getCertificateTagsTotalElements(certificate), certificateService.getCertificateTagsTotalPages(certificate));
        return foundTags.isEmpty() ? PagedModel.empty(metadata) : PagedModel.of(foundTags, metadata, links);
    }
}
