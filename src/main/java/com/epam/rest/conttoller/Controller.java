package com.epam.rest.conttoller;

import com.epam.rest.model.GiftCertificate;
import com.epam.rest.service.GiftCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class Controller {
    private GiftCertificateService service;

    @Autowired
    public Controller(GiftCertificateService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<GiftCertificate> findAll() {
        return service.findAll();
    }
}
