package org.example.ixtisaslar.controllers;

import org.example.ixtisaslar.dtos.sliderdtos.SliderCreateDto;
import org.example.ixtisaslar.dtos.sliderdtos.SliderDto;
import org.example.ixtisaslar.dtos.sliderdtos.SliderUpdateDto;
import org.example.ixtisaslar.payloads.ApiResponse;
import org.example.ixtisaslar.services.SliderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/slider")
public class SliderController {
    @Autowired
    private SliderService sliderService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> create(@ModelAttribute SliderCreateDto sliderCreate) {
        ApiResponse response = sliderService.create(sliderCreate);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        ApiResponse response = sliderService.delete(id);
        return  new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PutMapping(value = "/{id}",consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse> update(@ModelAttribute SliderUpdateDto sliderUpdate, @PathVariable Long id) {
        ApiResponse response = sliderService.update(sliderUpdate,id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<SliderDto>> getAll() {
        List <SliderDto> response = sliderService.getAllSliders();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
