package com.epam.rest.conttoller;

import com.epam.rest.model.Certificate;
import com.epam.rest.service.CertificateExistsException;
import com.epam.rest.service.GiftCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/certificates")
public class CertificateController {
    private GiftCertificateService giftCertificateService;

    @Autowired
    public CertificateController(GiftCertificateService giftCertificateService) {
        this.giftCertificateService = giftCertificateService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Certificate> showAllGiftCertificates() {
        return giftCertificateService.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Certificate showGiftCertificate(@PathVariable("id") long id) {
        Optional<Certificate> foundCertificate = giftCertificateService.findById(id);
        return foundCertificate.get();
    }

    @RequestMapping(method = RequestMethod.POST)
    public Certificate save(@RequestBody Certificate certificate) {
        try {
            return giftCertificateService.save(certificate);
        } catch (CertificateExistsException e) {
            return null;
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public Certificate update(@PathVariable("id") long id, @RequestBody Certificate certificate) {
        return giftCertificateService.update(id, certificate);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") long id) {
        giftCertificateService.delete(id);
    }
}
