package com.epam.esm.conttoller;

import com.epam.esm.conttoller.exception.Error;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

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
     * @param findParams find parameters.
     * @return controller response in JSON format and OK or NOT FOUND status code
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Certificate>> showCertificates(@RequestParam(required = false) LinkedHashMap<String, String> findParams) {
        List<Certificate> certificates = findParams.isEmpty() ? certificateService.findAll() : certificateService.findAllWithParameters(findParams);
        return certificates.isEmpty() ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(certificates, HttpStatus.OK);
    }

    /**
     * Handles GET certificate with passed id request
     * @param id if of certificate that need to be found
     * @return controller response in JSON format and OK status code
     * @throws CertificateNotFoundException if there is not certificate with passed id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Certificate> showCertificate(@PathVariable("id") long id) throws CertificateNotFoundException {
        return new ResponseEntity<>(certificateService.findById(id), HttpStatus.OK);
    }

    /**
     * Handles POST certificate request and saves posted certificate
     * @param certificate certificate that need to be saved
     * @return controller response in JSON format and CREATED status code
     * @throws CertificateExistsException if there is other certificate with passed certificate name
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Certificate> saveCertificate(@RequestBody Certificate certificate) throws CertificateExistsException {
        return new ResponseEntity<>(certificateService.save(certificate), HttpStatus.CREATED);

    }

    /**
     * Handles POST certificate with passed id request and updates certificate
     * @param id id of certificate that need to be updated
     * @param certificate certificate data that need to update
     * @return controller response in JSON format and OK status code
     * @throws CertificateNotFoundException if certificate with passed id not found
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity<Certificate> updateCertificate(@PathVariable("id") long id, @RequestBody Certificate certificate) throws CertificateNotFoundException {
        return new ResponseEntity<>(certificateService.update(id, certificate), HttpStatus.OK);
    }

    /**
     * Handles DELETE certificate with passed id and deletes it
     * @param id if of certificate that need to be deleted
     * @return NO CONTENT status code
     * @throws CertificateNotFoundException if there is not certificate with passed id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCertificate(@PathVariable("id") long id) throws CertificateNotFoundException {
        certificateService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Handles GET certificate tags request and show tags
     * @param id certificate whose tags need to be found
     * @return controller response in JSON format and OK or NOT FOUND status code
     * @throws CertificateNotFoundException if there is not certificate with passed id
     */
    @RequestMapping(value = "/{id}/tags", method = RequestMethod.GET)
    public ResponseEntity<List<Tag>> showCertificateTags(@PathVariable("id") long id) throws CertificateNotFoundException {
        Certificate foundCertificate = certificateService.findById(id);
        List<Tag> tags = foundCertificate.getTags();
        if (tags.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(tags, HttpStatus.OK);
        }
    }

    /**
     * Handles GET certificate tag and show certificate tag
     * @param id if of certificate that need to be found
     * @param tagId if of certificate tag that need to be found
     * @return controller response in JSON format and OK status code
     * @throws CertificateNotFoundException if there is no certificate with passed id
     * @throws TagNotFoundException if there is not tag with passed id
     */
    @RequestMapping(value = "/{id}/tags/{tagId}", method = RequestMethod.GET)
    public ResponseEntity<Tag> showCertificateTag(@PathVariable("id") long id, @PathVariable("tagId") long tagId) throws CertificateNotFoundException, TagNotFoundException {
        return new ResponseEntity<>(certificateService.findCertificateTag(id, tagId), HttpStatus.OK);
    }

    /**
     * Handles POST certificate tags request and add new tags to certificate
     * @param id if of certificate to which need to add new tags
     * @param tags list of tags that need to be added to certificate
     * @return controller response in JSON format and OK status code
     * @throws CertificateNotFoundException if there is no certificate with passed id
     */
    @RequestMapping(value = "/{id}/tags", method = RequestMethod.POST)
    public ResponseEntity<Certificate> addTagToCertificate(@PathVariable("id") long id, @RequestBody List<Tag> tags) throws CertificateNotFoundException {
        return new ResponseEntity<>(certificateService.addTags(id, tags), HttpStatus.OK);
    }

    /**
     * Handles DELETE certificate tag request and deletes certificate tag
     * @param id if of certificate that contains needed tag
     * @param tagId id of tag that need to be deleted
     * @throws TagNotFoundException if there is no tag with passed id
     * @throws CertificateNotFoundException if there is no certificate with passed id
     */
    @RequestMapping(value = "/{id}/tags/{tagId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCertificateTag(@PathVariable("id") long id, @PathVariable("tagId") long tagId) throws TagNotFoundException, CertificateNotFoundException {
        certificateService.deleteCertificateTag(id, tagId);
    }

    /**
     * Exception handlers methods that handles CertificateNotFoundException if
     * exception occurs in other methods and response with localized message
     * @param exception exception that occur in controller
     * @param locale client locale
     * @return controller custom localized error response in JSON format
     */
    @ExceptionHandler(CertificateNotFoundException.class)
    public ResponseEntity<Error> certificateNotFound(CertificateNotFoundException exception, Locale locale) {
        String message = messageSource.getMessage("certificate.notFound", new Object[]{exception.getCertificateId()}, locale);
        long code = HttpStatus.NOT_FOUND.value() + exception.getCode();
        Error error = new Error(code, message);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    /**
     * Exception handlers methods that handles CertificateExistsException if
     * exception occurs in other methods and response with localized message
     * @param exception exception that occur in controller
     * @param locale client locale
     * @return controller custom localized error response in JSON format
     */
    @ExceptionHandler(CertificateExistsException.class)
    public ResponseEntity<Error> certificateExists(CertificateExistsException exception, Locale locale) {
        String message = messageSource.getMessage("certificate.exists", new Object[]{}, locale);
        long code = HttpStatus.BAD_REQUEST.value() + exception.getCode();
        Error error = new Error(code, message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception handlers methods that handles TagNotFoundException if
     * exception occurs in other methods and response with localized message
     * @param exception exception that occur in controller
     * @param locale client locale
     * @return controller custom localized error response in JSON format
     */
    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<Error> tagNotFound(TagNotFoundException exception, Locale locale) {
        String message = messageSource.getMessage("tag.notFound", new Object[]{exception.getTagId()}, locale);
        long code = HttpStatus.NOT_FOUND.value() + TagNotFoundException.getCode();
        Error error = new Error(code, message);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

}
