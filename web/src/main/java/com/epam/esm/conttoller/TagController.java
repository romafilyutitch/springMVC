package com.epam.esm.conttoller;

import com.epam.esm.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(method = RequestMethod.POST)
    public Tag save(@RequestBody Tag tag) {
        try {
            return tagService.save(tag);
        } catch (TagExistsException e) {
            return null;
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") long id) {
        tagService.delete(id);
    }
}
