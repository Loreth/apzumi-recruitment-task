package pl.kamilprzenioslo.apzumi.controllers;

import static pl.kamilprzenioslo.apzumi.config.ResourceIdentifiers.ID;
import static pl.kamilprzenioslo.apzumi.config.ResourceIdentifiers.POST;
import static pl.kamilprzenioslo.apzumi.config.ResourceIdentifiers.POST_FETCH_REQUEST;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.kamilprzenioslo.apzumi.dtos.Post;
import pl.kamilprzenioslo.apzumi.exceptions.EntityNotFoundException;
import pl.kamilprzenioslo.apzumi.services.PostService;
import pl.kamilprzenioslo.apzumi.validation.PatchRequest;

@RequiredArgsConstructor
@RequestMapping(POST)
@RestController
public class PostController {
  private final PostService service;

  @GetMapping(ID)
  public Post getById(@PathVariable int id) {
    return service
        .findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @GetMapping
  public List<Post> getAll(@RequestParam(required = false, defaultValue = "") String title) {
    return service.findAll(title);
  }

  @PatchMapping(ID)
  public Post patch(@PathVariable int id, @Validated(PatchRequest.class) @RequestBody Post post) {
    return service.patch(id, post);
  }

  @DeleteMapping(ID)
  public void delete(@PathVariable int id) {
    try {
      service.deleteById(id);
    } catch (EntityNotFoundException ex) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
    }
  }

  @ResponseStatus(HttpStatus.ACCEPTED)
  @PostMapping(POST_FETCH_REQUEST)
  public void fetchPostsRequest() {
    service.fetchAndReplaceAllPosts();
  }
}
