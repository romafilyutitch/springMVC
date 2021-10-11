package com.epam.esm.conttoller;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.service.CertificateExistsException;
import com.epam.esm.service.CertificateNotFoundException;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.TagService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/certificates")
public class CertificateController {
    private CertificateService certificateService;
    private TagService tagService;
    private MessageSource messageSource;

    public CertificateController(CertificateService certificateService, TagService tagService, MessageSource messageSource) {
        this.certificateService = certificateService;
        this.tagService = tagService;
        this.messageSource = messageSource;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Certificate>> showCertificates(@RequestParam(required = false) String name,
                                                              @RequestParam(required = false) String tagName,
                                                              @RequestParam(required = false) boolean sortName,
                                                              @RequestParam(required = false) boolean sortDate) {
        List<Certificate> certificates = name == null ? tagName == null ? certificateService.findAll() : certificateService.findByTagName(tagName) : certificateService.searchByName(name);
        certificateService.sortByNameThenDate(certificates, sortName, sortDate);
        if (certificates.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(certificates, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Certificate> showCertificate(@PathVariable("id") long id) throws CertificateNotFoundException {
        return new ResponseEntity<>(certificateService.findById(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Certificate> saveCertificate(@RequestBody Certificate certificate) throws CertificateExistsException {
        return new ResponseEntity<>(certificateService.save(certificate), HttpStatus.CREATED);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity<Certificate> updateCertificate(@PathVariable("id") long id, @RequestBody Certificate certificate) throws CertificateNotFoundException {
        return new ResponseEntity<>(certificateService.update(id, certificate), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCertificate(@PathVariable("id") long id) throws CertificateNotFoundException {
        certificateService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

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

    @RequestMapping(value = "/{id}/tags/{tagId}", method = RequestMethod.GET)
    public ResponseEntity<Tag> showCertificateTag(@PathVariable("id") long id, @PathVariable("tagId") long tagId) throws CertificateNotFoundException {
        Certificate certificate = certificateService.findById(id);
        Optional<Tag> optionalTag = tagService.findById(tagId);
        if (optionalTag.isPresent()) {
            return new ResponseEntity<>(optionalTag.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{id}/tags", method = RequestMethod.POST)
    public Certificate addTagToCertificate(@PathVariable("id") long id, @RequestBody List<Tag> tags) throws CertificateNotFoundException {
        return certificateService.addTags(id, tags);
    }

    @RequestMapping(value = "/{id}/tags/{tagId}", method = RequestMethod.DELETE)
    public void deleteCertificateTag(@PathVariable("id") long id, @PathVariable("tagId") long tagId) {
        tagService.delete(tagId);
    }

    @ExceptionHandler(CertificateNotFoundException.class)
    public ResponseEntity<Error> certificateNotFound(CertificateNotFoundException exception, Locale locale) {
        String message = messageSource.getMessage("certificate.notFound", new Object[]{exception.getCertificateId()}, locale);
        long code = HttpStatus.NOT_FOUND.value() + exception.getCode();
        Error error = new Error(code, message);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({CertificateExistsException.class})
    public ResponseEntity<Error> certificateExists(CertificateExistsException exception, Locale locale) {
        String message = messageSource.getMessage("certificate.exists", new Object[]{}, locale);
        long code = HttpStatus.BAD_REQUEST.value() + exception.getCode();
        Error error = new Error(code, message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
