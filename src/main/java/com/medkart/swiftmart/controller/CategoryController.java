package com.medkart.swiftmart.controller;

import com.medkart.swiftmart.dto.CategoryDto;
import com.medkart.swiftmart.dto.Response;
import com.medkart.swiftmart.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROOT_ADMIN')")
    public ResponseEntity<Response> createCategory(@RequestBody CategoryDto categoryDto) {
        Response response = categoryService.createCategory(categoryDto);

        HttpStatus status = response.getStatus() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }


    @GetMapping("/get-all")
    public ResponseEntity<Response> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PutMapping("/update/{categoryId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROOT_ADMIN')")
    public ResponseEntity<Response> updateCategory(@PathVariable("categoryId") Long categoryId, @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId , categoryDto));
    }

    @DeleteMapping("/delete/{categoryId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROOT_ADMIN')")

    public ResponseEntity<Response> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }

    @GetMapping("/id/{categoryId}")
    public ResponseEntity<Response> getCategoryById(@PathVariable("categoryId") Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }
}
