package com.epam.rest.conttoller;

import com.epam.rest.model.Certificate;
import com.epam.rest.model.Tag;
import com.epam.rest.service.CertificateExistsException;
import com.epam.rest.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/certificates")
public class CertificateController {
    private CertificateService certificateService;

    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Certificate> showAllCertificates() {
        return certificateService.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Certificate showCertificate(@PathVariable("id") long id) {
        Optional<Certificate> foundCertificate = certificateService.findById(id);
        return foundCertificate.get();
    }

    @RequestMapping(method = RequestMethod.POST)
    public Certificate saveCertificate(@RequestBody Certificate certificate) {
        try {
            return certificateService.save(certificate);
        } catch (CertificateExistsException e) {
            return null;
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public Certificate updateCertificate(@PathVariable("id") long id, @RequestBody Certificate certificate) {
        return certificateService.update(id, certificate);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteCertificate(@PathVariable("id") long id) {
        certificateService.delete(id);
    }

    @RequestMapping(value = "/{id}/tags", method = RequestMethod.GET)
    public List<Tag> showCertificateTags(@PathVariable("id") long id) {
        Optional<Certificate> optionalCertificate = certificateService.findById(id);
        Certificate foundCertificate = optionalCertificate.get();
        return foundCertificate.getTags();
    }

    @RequestMapping(value = "/{id}/tags/{tagId}", method = RequestMethod.GET)
    public Tag showCertificateTag(@PathVariable("id") long id, @PathVariable("tagId") long tagId) {
        Optional<Certificate> optionalCertificate = certificateService.findById(id);
        Certificate foundCertificate = optionalCertificate.get();
        List<Tag> tags = foundCertificate.getTags();
        Optional<Tag> optionalTag = tags.stream().filter(tag -> tag.getId().equals(tagId)).findAny();
        return optionalTag.get();
    }


}
