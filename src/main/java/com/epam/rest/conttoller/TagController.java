package com.epam.rest.conttoller;

import com.epam.rest.model.Tag;
import com.epam.rest.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {
    private TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Tag> showAllTags() {
        return tagService.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Tag showTag(@PathVariable("id") long id) {
        return tagService.findById(id).get();
    }
}
