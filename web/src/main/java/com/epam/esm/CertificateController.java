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
    private final MessageSource messageSource;

    public CertificateController(CertificateService certificateService, UserService userService, MessageSource messageSource) {
        this.certificateService = certificateService;
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public CollectionModel<Certificate> showCertificates(@RequestParam LinkedHashMap<String, String> findParams) throws PageOutOfBoundsException, ResourceNotFoundException {
        List<Certificate> certificates = certificateService.findAllWithParameters(findParams);
        for (Certificate certificate : certificates) {
            if (certificate.getTags().size() > 0) {
                Link tagsLink = linkTo(methodOn(CertificateController.class).showCertificateTagsPage(certificate.getId(), 1)).withRel("tags");
                certificate.add(tagsLink);
            }
            Link orderLink = linkTo(methodOn(CertificateController.class).showCertificateOrder(certificate.getId())).withRel("order");
            certificate.add(orderLink);
        }
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificates(findParams)).withSelfRel();
        return certificates.isEmpty() ? CollectionModel.empty(selfLink) : CollectionModel.of(certificates, selfLink);
    }

    @GetMapping("/page/{page}")
    public PagedModel<Certificate> showCertificatePage(@PathVariable int page) throws ResourceNotFoundException, PageOutOfBoundsException {
        List<Certificate> foundPage = certificateService.findPage(page);
        return makeCertificatePage(page, foundPage);
    }

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
     * Handles POST certificate request and saves posted certificate
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCertificate(@PathVariable("id") long id) throws ResourceNotFoundException {
        Certificate foundCertificate = certificateService.findById(id);
        certificateService.delete(foundCertificate);
    }

    @GetMapping("/{id}/tags")
    public PagedModel<Tag> showCertificateTags(@PathVariable("id") long id) throws ResourceNotFoundException, PageOutOfBoundsException {
        Certificate foundCertificate = certificateService.findById(id);
        List<Tag> tags = certificateService.findCertificateTagsPage(foundCertificate, 1);
        return makeTagsPage(1, foundCertificate, tags);
    }

    @GetMapping("/{id}/tags/page/{page}")
    public PagedModel<Tag> showCertificateTagsPage(@PathVariable long id, @PathVariable int page) throws ResourceNotFoundException, PageOutOfBoundsException {
        Certificate foundCertificate = certificateService.findById(id);
        List<Tag> tags = certificateService.findCertificateTagsPage(foundCertificate, page);
        return makeTagsPage(page, foundCertificate, tags);
    }

    @GetMapping("/{id}/tags/{tagId}")
    public Tag showCertificateTag(@PathVariable("id") long id, @PathVariable("tagId") long tagId) throws ResourceNotFoundException {
        Certificate foundCertificate = certificateService.findById(id);
        Tag foundTag = certificateService.findCertificateTag(foundCertificate, tagId);
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificateTag(id, tagId)).withSelfRel();
        foundTag.add(selfLink);
        return foundTag;
    }

    @PostMapping("/{id}/tags")
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

    @DeleteMapping("/{id}/tags/{tagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCertificateTag(@PathVariable("id") long id, @PathVariable("tagId") long tagId) throws ResourceNotFoundException {
        Certificate foundCertificate = certificateService.findById(id);
        Tag foundTag = certificateService.findCertificateTag(foundCertificate, tagId);
        certificateService.deleteCertificateTag(foundCertificate, foundTag);
    }

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

    @PostMapping("/{id}/order")
    public Order makeOrder(@PathVariable Long id, @RequestBody User user) throws ResourceNotFoundException, PageOutOfBoundsException {
        User foundUser = userService.findByName(user.getName());
        Certificate foundCertificate = certificateService.findById(id);
        Order savedOrder = userService.orderCertificate(foundUser, foundCertificate);
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificateOrder(id)).withSelfRel();
        savedOrder.add(selfLink);
        return savedOrder;
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Error> certificateNotFound(ResourceNotFoundException exception, Locale locale) {
        String message = messageSource.getMessage("resource.notFound", new Object[]{exception.getResourceId()}, locale);
        Error error = new Error(ErrorCode.NOT_FOUND, message);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidResourceException.class)
    public ResponseEntity<Error> invalidCertificate(InvalidResourceException exception, Locale locale) {
        String message = messageSource.getMessage("resource.invalid", new Object[]{}, locale);
        ;
        Error error = new Error(ErrorCode.INVALID, message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Error> orderNotFound(OrderNotFoundException exception, Locale locale) {
        String message = messageSource.getMessage("order.notFound", new Object[]{exception.getResourceId()}, locale);
        Error error = new Error(ErrorCode.NOT_FOUND, message);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PageOutOfBoundsException.class)
    public ResponseEntity<Error> pageOutOfBounds(PageOutOfBoundsException exception, Locale locale) {
        String message = messageSource.getMessage("page.outOfBounds", new Object[]{exception.getCurrentPage(), exception.getMinPage(), exception.getMaxPage()}, locale);
        Error error = new Error(ErrorCode.PAGE_OUT_OF_BOUNDS, message);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


    private PagedModel<Certificate> makeCertificatePage(int currentPage, List<Certificate> certificates) throws ResourceNotFoundException, PageOutOfBoundsException {
        for (Certificate certificate : certificates) {
            if (certificate.getTags().size() > 0) {
                Link tagsLink = linkTo(methodOn(CertificateController.class).showCertificateTagsPage(certificate.getId(), 1)).withRel("tags");
                certificate.add(tagsLink);
            }
            Link orderLink = linkTo(methodOn(CertificateController.class).showCertificateOrder(certificate.getId())).withRel("order");
            certificate.add(orderLink);
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
        } else if (currentPage == certificateService.getTotalPages()) {
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
        } else if (page == certificateService.getCertificateTagsTotalPages(certificate)) {
            links.remove(nextPage);
        }
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(foundTags.size(), page, certificateService.getCertificateTagsTotalElements(certificate), certificateService.getCertificateTagsTotalPages(certificate));
        return foundTags.isEmpty() ? PagedModel.empty(metadata) : PagedModel.of(foundTags, metadata, links);
    }
}
