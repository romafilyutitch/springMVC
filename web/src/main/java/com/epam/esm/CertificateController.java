package com.epam.esm;

import com.epam.esm.Error;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.service.CertificateNotFoundException;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.TagNotFoundException;
import com.epam.esm.validation.InvalidCertificateException;
import com.epam.esm.validation.InvalidTagException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    private final MessageSource messageSource;

    @Autowired
    public CertificateController(CertificateService certificateService, MessageSource messageSource) {
        this.certificateService = certificateService;
        this.messageSource = messageSource;
    }

    /**
     * Handles GET certificates request and shows all certificates.
     * If passed name param then finds certificates by part of name.
     * If passed tagName param then finds certificates by tag name
     *
     * @param findParams find parameters.
     * @return controller response in JSON format and OK or NOT FOUND status code
     */
    @GetMapping
    public CollectionModel<Certificate> showCertificates(@RequestParam(required = false) LinkedHashMap<String, String> findParams) throws CertificateNotFoundException, TagNotFoundException {
        List<Certificate> certificates = findParams.isEmpty() ? certificateService.findAll() : certificateService.findAllWithParameters(findParams);
        for (Certificate certificate : certificates) {
            if (certificate.getTags().size() > 0) {
                Link tagsLink = linkTo(methodOn(CertificateController.class).showCertificateTags(certificate.getId())).withRel("tags");
                certificate.add(tagsLink);
            }
        }
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificates(findParams)).withSelfRel();
        return certificates.isEmpty() ? CollectionModel.empty(selfLink) : CollectionModel.of(certificates, selfLink);
    }

    /**
     * Handles GET certificate with passed id request
     *
     * @param id if of certificate that need to be found
     * @return controller response in JSON format and OK status code
     * @throws CertificateNotFoundException if there is not certificate with passed id
     */
    @GetMapping("/{id}")
    public Certificate showCertificate(@PathVariable("id") long id) throws CertificateNotFoundException, TagNotFoundException {
        Certificate foundCertificate = certificateService.findById(id);
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificate(id)).withSelfRel();
        foundCertificate.add(selfLink);
        if (foundCertificate.getTags().size() > 0) {
            Link tagsLink = linkTo(methodOn(CertificateController.class).showCertificateTags(id)).withRel("tags");
            foundCertificate.add(tagsLink);
        }
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
    public Certificate saveCertificate(@RequestBody Certificate certificate) throws InvalidCertificateException, CertificateNotFoundException, TagNotFoundException {
        Certificate savedCertificate = certificateService.save(certificate);
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificate(savedCertificate.getId())).withRel("saved");
        savedCertificate.add(selfLink);
        if (savedCertificate.getTags().size() > 0) {
            Link tagLink = linkTo(methodOn(CertificateController.class).showCertificateTags(savedCertificate.getId())).withRel("tags");
            savedCertificate.add(tagLink);
        }
        return savedCertificate;
    }

    /**
     * Handles POST certificate with passed id request and updates certificate
     *
     * @param id          id of certificate that need to be updated
     * @param certificate certificate data that need to update
     * @return controller response in JSON format and OK status code
     * @throws CertificateNotFoundException if certificate with passed id not found
     */
    @PostMapping("/{id}")
    public Certificate updateCertificate(@PathVariable("id") long id, @RequestBody Certificate certificate) throws CertificateNotFoundException, InvalidCertificateException, TagNotFoundException {
        Certificate updatedCertificate = certificateService.update(id, certificate);
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificate(id)).withSelfRel();
        if (updatedCertificate.getTags().size() > 0) {
            Link tagsLink = linkTo(methodOn(CertificateController.class).showCertificateTags(id)).withRel("tags");
            updatedCertificate.add(tagsLink);
        }
        return certificate;
    }

    /**
     * Handles DELETE certificate with passed id and deletes it
     *
     * @param id if of certificate that need to be deleted
     * @return NO CONTENT status code
     * @throws CertificateNotFoundException if there is not certificate with passed id
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCertificate(@PathVariable("id") long id) throws CertificateNotFoundException {
        certificateService.delete(id);
    }

    /**
     * Handles GET certificate tags request and show tags
     *
     * @param id certificate whose tags need to be found
     * @return controller response in JSON format and OK or NOT FOUND status code
     * @throws CertificateNotFoundException if there is not certificate with passed id
     */
    @GetMapping("/{id}/tags")
    public CollectionModel<Tag> showCertificateTags(@PathVariable("id") long id) throws CertificateNotFoundException, TagNotFoundException {
        Certificate foundCertificate = certificateService.findById(id);
        List<Tag> tags = foundCertificate.getTags();
        for (Tag tag : tags) {
            Link tagLink = linkTo(methodOn(CertificateController.class).showCertificateTag(foundCertificate.getId(), tag.getId())).withRel("tag");
            tag.add(tagLink);
        }
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificateTags(id)).withSelfRel();
        return tags.isEmpty() ? CollectionModel.empty(selfLink) : CollectionModel.of(tags, selfLink);
    }

    /**
     * Handles GET certificate tag and show certificate tag
     *
     * @param id    if of certificate that need to be found
     * @param tagId if of certificate tag that need to be found
     * @return controller response in JSON format and OK status code
     * @throws CertificateNotFoundException if there is no certificate with passed id
     * @throws TagNotFoundException         if there is not tag with passed id
     */
    @GetMapping("/{id}/tags/{tagId}")
    public Tag showCertificateTag(@PathVariable("id") long id, @PathVariable("tagId") long tagId) throws CertificateNotFoundException, TagNotFoundException {
        Tag foundTag = certificateService.findCertificateTag(id, tagId);
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificateTag(id, tagId)).withSelfRel();
        foundTag.add(selfLink);
        return foundTag;
    }

    /**
     * Handles POST certificate tags request and add new tags to certificate
     *
     * @param id   if of certificate to which need to add new tags
     * @param tags list of tags that need to be added to certificate
     * @return controller response in JSON format and OK status code
     * @throws CertificateNotFoundException if there is no certificate with passed id
     */
    @PostMapping("/{id}/tags")
    public Certificate addTagToCertificate(@PathVariable("id") long id, @RequestBody List<Tag> tags) throws CertificateNotFoundException, InvalidTagException, TagNotFoundException {
        Certificate updatedCertificate = certificateService.addTags(id, tags);
        Link selfLink = linkTo(methodOn(CertificateController.class).showCertificate(updatedCertificate.getId())).withSelfRel();
        updatedCertificate.add(selfLink);
        if (updatedCertificate.getTags().size() > 0) {
            Link tagsLink = linkTo(methodOn(CertificateController.class).showCertificateTags(id)).withRel("tags");
            updatedCertificate.add(tagsLink);
        }
        return updatedCertificate;
    }

    /**
     * Handles DELETE certificate tag request and deletes certificate tag
     *
     * @param id    if of certificate that contains needed tag
     * @param tagId id of tag that need to be deleted
     * @throws TagNotFoundException         if there is no tag with passed id
     * @throws CertificateNotFoundException if there is no certificate with passed id
     */
    @DeleteMapping("/{id}/tags/{tagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCertificateTag(@PathVariable("id") long id, @PathVariable("tagId") long tagId) throws TagNotFoundException, CertificateNotFoundException {
        certificateService.deleteCertificateTag(id, tagId);
    }

    /**
     * Exception handlers methods that handles CertificateNotFoundException if
     * exception occurs in other methods and response with localized message
     *
     * @param exception exception that occur in controller
     * @param locale    client locale
     * @return controller custom localized error response in JSON format
     */
    @ExceptionHandler(CertificateNotFoundException.class)
    public ResponseEntity<Error> certificateNotFound(CertificateNotFoundException exception, Locale locale) {
        String message = messageSource.getMessage("certificate.notFound", new Object[]{exception.getCertificateId()}, locale);
        String code = HttpStatus.NOT_FOUND.value() + exception.getCode();
        Error error = new Error(code, message);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Exception handlers methods that handles TagNotFoundException if
     * exception occurs in other methods and response with localized message
     *
     * @param exception exception that occur in controller
     * @param locale    client locale
     * @return controller custom localized error response in JSON format
     */
    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<Error> tagNotFound(TagNotFoundException exception, Locale locale) {
        String message = messageSource.getMessage("tag.notFound", new Object[]{exception.getTagId()}, locale);
        String code = HttpStatus.NOT_FOUND.value() + exception.getCode();
        Error error = new Error(code, message);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Exception handler method to handle InvalidCertificateException if
     * exception occurs in other methods and response with localized message.
     *
     * @param exception exception that occur in controller
     * @param locale    client locale
     * @return controller custom localized error response in JSON format
     */
    @ExceptionHandler(InvalidCertificateException.class)
    public ResponseEntity<Error> invalidCertificate(InvalidCertificateException exception, Locale locale) {
        String message = messageSource.getMessage("certificate.invalid", new Object[]{}, locale);
        String code = HttpStatus.BAD_REQUEST.value() + exception.getCode();
        Error error = new Error(code, message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception handler methods to handle InvalidTagException if
     * exception occurs in other methods and response with localized message.
     *
     * @param exception exception that occur in controller
     * @param locale    client locale
     * @return controller custom localized response in JSON format
     */
    @ExceptionHandler(InvalidTagException.class)
    public ResponseEntity<Error> invalidTag(InvalidTagException exception, Locale locale) {
        String message = messageSource.getMessage("tag.invalid", new Object[]{}, locale);
        String code = HttpStatus.BAD_REQUEST.value() + exception.getCode();
        Error error = new Error(code, message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
