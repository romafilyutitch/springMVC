package com.epam.rest.conttoller;

import com.epam.rest.model.Entity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class Controller {

    @RequestMapping(method = RequestMethod.GET)
    public Entity hello() {
        return new Entity();
    }
}
