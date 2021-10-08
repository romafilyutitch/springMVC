package com.epam.rest.conttoller;

import com.epam.rest.model.GiftCertificate;
import com.epam.rest.service.CertificateExistsException;
import com.epam.rest.service.GiftCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/GiftCertificates")
public class GiftCertificateController {
    private GiftCertificateService giftCertificateService;

    @Autowired
    public GiftCertificateController(GiftCertificateService giftCertificateService) {
        this.giftCertificateService = giftCertificateService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<GiftCertificate> showAllGiftCertificates() {
        return giftCertificateService.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public GiftCertificate showGiftCertificate(@PathVariable("id") long id) {
        Optional<GiftCertificate> foundCertificate = giftCertificateService.findById(id);
        return foundCertificate.get();
    }

    @RequestMapping(method = RequestMethod.POST)
    public GiftCertificate save(@RequestBody GiftCertificate certificate) {
        try {
            return giftCertificateService.save(certificate);
        } catch (CertificateExistsException e) {
            return null;
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public GiftCertificate update(@PathVariable("id") long id, @RequestBody GiftCertificate certificate) {
        return giftCertificateService.update(id, certificate);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") long id) {
        giftCertificateService.delete(id);
    }
}
