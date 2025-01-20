package com.medkart.swiftmart.controller;

import com.amazonaws.services.kms.model.NotFoundException;
import com.cloudinary.Cloudinary;
import com.medkart.swiftmart.dto.ImageUrlsRequest;
import com.medkart.swiftmart.dto.ProductDto;
import com.medkart.swiftmart.dto.Response;
import com.medkart.swiftmart.entity.Category;
import com.medkart.swiftmart.entity.ExtraImage;
import com.medkart.swiftmart.entity.Product;
import com.medkart.swiftmart.entity.Tag;
import com.medkart.swiftmart.repository.CategoryRepo;
import com.medkart.swiftmart.repository.ProductRepo;
import com.medkart.swiftmart.repository.TagRepo;
import com.medkart.swiftmart.service.CategoryService;
import com.medkart.swiftmart.service.ProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.InvalidCredentialsException;
import org.slf4j.ILoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;
    private final TagRepo tagRepo;


    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROOT_ADMIN')")
    public ResponseEntity<Response> createProduct(
            @RequestParam Long categoryId,
            @RequestParam String image,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam BigDecimal price,
            @RequestParam BigDecimal mrp,
            @RequestParam Long qty,
            @RequestParam Long productSize
    ) throws InvalidCredentialsException {
        if (categoryId == null || image.isEmpty() || name.isEmpty() || description.isEmpty() || price == null || mrp == null || qty == null || productSize == null){
            throw new InvalidCredentialsException("All Fields are Required");
        }
        return ResponseEntity.ok(productService.createProduct(categoryId, image, name, description, price, mrp, qty, productSize));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROOT_ADMIN')")
    public ResponseEntity<Response> updateProduct(
            @RequestParam Long productId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false)  String image,
            @RequestParam(required = false)  String name,
            @RequestParam(required = false)  String description,
            @RequestParam(required = false)  BigDecimal price,
            @RequestParam(required = false) BigDecimal mrp,
            @RequestParam(required = false) Long qty,
            @RequestParam(required = false) Long productSize
    ){
        return ResponseEntity.ok(productService.updateProduct(productId, categoryId, image, name, description, price,mrp , qty , productSize ));
    }

    // Refactor below two functions.
    @Transactional
    @PutMapping("/updatetg")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROOT_ADMIN')")
    public ResponseEntity<Response> updateProduct(@RequestParam Long productId, @Valid @RequestBody ProductDto productDto) {
        // Fetch the product by ID or throw exception if not found
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with ID " + productId + " Not Found"));

        // Update fields from DTO if not null
        if (productDto.getName() != null) {
            product.setName(productDto.getName());
        }
        if (productDto.getDescription() != null) {
            product.setDescription(productDto.getDescription());
        }
        if (productDto.getPrice() != null) {
            product.setPrice(productDto.getPrice());
        }
        if (productDto.getImageUrl() != null) {
            log.debug("Image URL: {}", productDto.getImageUrl());
            product.setImageUrl(productDto.getImageUrl());
        }
        if (productDto.getMrp() != null) {
            product.setMrp(productDto.getMrp());
        }
        if (productDto.getQty() != null) {
            product.setQty(productDto.getQty());
        }
        if (productDto.getProductSize() != null) {
            product.setProductSize(productDto.getProductSize());
        }

        // Update category if a valid category ID is provided
        if (productDto.getCategoryId() != null) {
            Category category = categoryRepo.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category with ID " + productDto.getCategoryId() + " Not Found"));
            product.setCategory(category);
        }

        // Update tags if present
        if (productDto.getTags() != null) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : productDto.getTags()) {
                Tag tag = tagRepo.findByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepo.save(newTag);
                        });
                tags.add(tag);
            }
            product.setTags(tags);
        }

        // Save the updated product
        Product updatedProduct = productRepo.save(product);

        // Build the response object
        Response response = Response.builder()
                .status(200)
                .message("Product Updated Successfully")
                .build();

        // Return the response entity
        return ResponseEntity.ok(response);
    }


//    public ResponseEntity<Response> createProduct(@Valid @RequestBody ProductDto productDto) {
//        // Fetch category by ID or throw exception if not found
//        Category category = categoryRepo.findById(productDto.getCategoryId())
//                .orElseThrow(() -> new NotFoundException("Category with ID " + productDto.getCategoryId() + " Not Found"));
//
//        // Create new Product and populate fields from DTO
//        Product product = new Product();
//        product.setName(productDto.getName());
//        product.setDescription(productDto.getDescription());
//        product.setPrice(productDto.getPrice());
//        product.setImageUrl(productDto.getImageUrl());
//        product.setCategory(category);
//        product.setMrp(productDto.getMrp());
//        product.setQty(productDto.getQty());
//        product.setProductSize(productDto.getProductSize());
//
//        // Map and persist tags
//        Set<Tag> tags = new HashSet<>();
//        if (productDto.getTags() != null) {
//            for (String tagName : productDto.getTags()) {
//                Tag tag = tagRepo.findByName(tagName)
//                        .orElseGet(() -> {
//                            Tag newTag = new Tag();
//                            newTag.setName(tagName);
//                            return tagRepo.save(newTag);
//                        });
//                tags.add(tag);
//            }
//        }
//
//        // Associate the tags with the product
//        product.setTags(tags);
//
//        // Save the product
//        Product savedProduct = productRepo.save(product);
//
//        // Build the response object
//        Response response = Response.builder()
//                .status(200)
//                .productId(product.getId())
//                .message("Product Created Successfully")
//                .build();
//
//        // Return the response entity
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/createtg")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROOT_ADMIN')")
    public ResponseEntity<Response> createProduct(@Valid @RequestBody ProductDto productDto) {
        try {
            Response response = productService.createProduct(productDto);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Response.builder()
                            .status(400)
                            .message(e.getMessage())
                            .build()
            );
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Response.builder()
                            .status(404)
                            .message(e.getMessage())
                            .build()
            );
        }
    }


//    @PostMapping("/add-extra-image/{productId}")
//    @PreAuthorize("hasAuthority('ADMIN')")
//    public ResponseEntity<Response> deleteProduct(@PathVariable("productId") Long productId, @RequestParam String imageUrl) {
//
//    }

    @DeleteMapping("/delete/{productId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROOT_ADMIN')")
    public ResponseEntity<Response> deleteProduct(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(productService.deleteProduct(productId));
    }

    @GetMapping("/id/{productId}")
    public ResponseEntity<Response> getProductById(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/get-all")
    public ResponseEntity<Response> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/get-by-category-id/{categoryId}")
    public ResponseEntity<Response> getProductsByCategory(@PathVariable Long categoryId){
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @GetMapping("/search")
    public ResponseEntity<Response> searchForProduct(@RequestParam String searchValue){
        return ResponseEntity.ok(productService.searchByIdOrName(searchValue));
    }

    @PostMapping("/{productId}/add-images")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROOT_ADMIN')")
    public ResponseEntity<Response> addExtraImages(
            @PathVariable Long productId,
            @RequestBody ImageUrlsRequest request
    ) {
//        productService.addExtraImages(productId, request.getImageUrls());
        return ResponseEntity.ok(productService.addExtraImages(productId, request.getImageUrls()));
    }

    @GetMapping("/{productId}/images")
    public ResponseEntity<Response> getExtraImages(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getExtraImages(productId));
    }
    @DeleteMapping("/{productId}/delete-image/{imageId}")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Response> deleteExtraImage(@PathVariable Long productId, @PathVariable Long imageId) {
        return ResponseEntity.ok(productService.deleteExtraImage(productId, imageId));
    }
}
