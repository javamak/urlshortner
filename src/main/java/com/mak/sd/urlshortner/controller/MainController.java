package com.mak.sd.urlshortner.controller;

import com.mak.sd.urlshortner.service.ShortenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

@RestController
public class MainController {

    @Autowired
    private ShortenService service;

    @GetMapping("/shorten")
    public String shortenURL(@RequestParam String url) {
        return service.shortenURL(url);
    }

    @GetMapping("/{id}")
    public void expandURL(@PathVariable("id") String id, HttpServletResponse response){

        try {
            String url = service.expandURL(id);
            response.sendRedirect(url);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        } catch(NoSuchElementException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
