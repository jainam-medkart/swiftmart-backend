package com.medkart.swiftmart.service.impl;

import com.medkart.swiftmart.dto.ExtraImageDto;
import com.medkart.swiftmart.dto.ProductDto;
import com.medkart.swiftmart.dto.ResultDTO;
import com.medkart.swiftmart.entity.Category;
import com.medkart.swiftmart.entity.ExtraImage;
import com.medkart.swiftmart.entity.Product;
import com.medkart.swiftmart.entity.Tag;
import com.medkart.swiftmart.exception.NotFoundException;
import com.medkart.swiftmart.mapper.EntityDtoMapper;
import com.medkart.swiftmart.repository.CategoryRepo;
import com.medkart.swiftmart.repository.ExtraImageRepo;
import com.medkart.swiftmart.repository.ProductRepo;
import com.medkart.swiftmart.repository.TagRepo;
import com.medkart.swiftmart.service.inter.ProductServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductServiceInterface {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final TagRepo tagRepo;
    private final EntityDtoMapper entityDtoMapper;
    private final ExtraImageRepo extraImageRepo;

    // Create
    @Override
    public ResultDTO<?> createProduct(ProductDto productDto) {
        try {
            validateProductDto(productDto);

            if (productRepo.existsByName(productDto.getName())){
                return ResultDTO.builder()
                        .status(409)
                        .message("Product with name " + productDto.getName() + " already exists.")
                        .build();
            }

            Category category = categoryRepo.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category with ID " + productDto.getCategoryId() + " not found"));

            Set<Tag> tags = mapTags(productDto.getTags());

            Product product = new Product();
            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            product.setPrice(productDto.getPrice());
            product.setImageUrl(productDto.getImageUrl());
            product.setCategory(category);
            product.setMrp(productDto.getMrp());
            product.setQty(productDto.getQty());
            product.setProductSize(productDto.getProductSize());
            product.setTags(tags);

            Product savedProduct = productRepo.save(product);

            return ResultDTO.builder()
                    .status(201) // HTTP 201 Created
                    .message("Product created successfully")
                    .data(savedProduct)
                    .build();

        } catch (IllegalArgumentException e) {
            return ResultDTO.builder()
                    .status(400) // HTTP 400 Bad Request
                    .message(e.getMessage())
                    .build();

        } catch (NotFoundException e) {
            return ResultDTO.builder()
                    .status(404) // HTTP 404 Not Found
                    .message(e.getMessage())
                    .build();

        } catch (Exception e) {
            return ResultDTO.builder()
                    .status(500) // HTTP 500 Internal Server Error
                    .message("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }

    private void validateProductDto(ProductDto productDto) {
        if (productDto.getName() == null || productDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be blank");
        }

        if (productDto.getDescription() == null || productDto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Product description cannot be blank");
        }

        if (productDto.getPrice() == null || productDto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }

        if (productDto.getMrp() == null || productDto.getMrp().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("MRP must be greater than 0");
        }



        if (productDto.getQty() == null || productDto.getQty() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        if (productDto.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID is required");
        }

        if (productDto.getMrp().compareTo(productDto.getPrice()) < 0) {
            throw new IllegalArgumentException("Sales Price must be less than or equal to MRP");
        }
    }
    private Set<Tag> mapTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        if (tagNames != null) {
            for (String tagName : tagNames) {
                Tag tag = tagRepo.findByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepo.save(newTag);
                        });
                tags.add(tag);
            }
        }
        return tags;
    }

    // Update
    @Override
    public ResultDTO<?> updateProduct(Long productId, ProductDto productDto) {
        try {
            // Fetch the product by ID or throw an exception if not found
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Product with ID " + productId + " Not Found"));

            // Update fields from DTO if they are not null
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

            // Update the category if a valid category ID is provided
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

            Product updatedProduct = productRepo.save(product);

            return ResultDTO.builder()
                    .status(200) // HTTP 200 OK
                    .message("Product Updated Successfully")
                    .data(updatedProduct) // Optionally include updated product details
                    .build();

        } catch (NotFoundException e) {
            // Handle not found exceptions for product or category
            return ResultDTO.builder()
                    .status(404) // HTTP 404 Not Found
                    .message(e.getMessage())
                    .build();

        } catch (Exception e) {
            // Handle unexpected errors
            return ResultDTO.builder()
                    .status(500) // HTTP 500 Internal Server Error
                    .message("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }

    // Delete
    @Override
    public ResultDTO<?> deleteProduct(Long productId) {
        try {
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Product with ID " + productId + " Not Found"));

            productRepo.delete(product);

            return ResultDTO.builder()
                    .status(200) // HTTP 200 OK
                    .message("Product Deleted Successfully")
                    .build();

        } catch (NotFoundException e) {
            return ResultDTO.builder()
                    .status(404) // HTTP 404 Not Found
                    .message(e.getMessage())
                    .build();

        } catch (Exception e) {
            return ResultDTO.builder()
                    .status(500) // HTTP 500 Internal Server Error
                    .message("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }

    // Fetching
    @Override
    public ResultDTO<?> getProductById(Long productId) {
        try {
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Product with ID " + productId + " Not Found"));

            ProductDto productDto = entityDtoMapper.mapProductToDtoBasic(product);

            return ResultDTO.builder()
                    .status(200) // HTTP 200 OK
                    .message("Product Retrieved Successfully")
                    .data(productDto)
                    .build();

        } catch (NotFoundException e) {
            return ResultDTO.builder()
                    .status(404) // HTTP 404 Not Found
                    .message(e.getMessage())
                    .build();

        } catch (Exception e) {
            return ResultDTO.builder()
                    .status(500) // HTTP 500 Internal Server Error
                    .message("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }
    @Override
    public ResultDTO<?> getAllProducts(int page, int pageSize) {
        // Fetch products with pagination
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<Product> productPage = productRepo.findAll(pageable);

        // Map entities to DTOs
        List<ProductDto> productList = productPage.getContent().stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .collect(Collectors.toList());

        // Build the ResultDTO
        return ResultDTO.builder()
                .status(200)  // HTTP 200 OK
                .message("Products Retrieved Successfully")
                .data(productList)  // Set the product list as data
                .totalPage(productPage.getTotalPages())  // Set total pages for pagination
                .totalElements(productPage.getTotalElements())  // Set total elements for pagination
                .build();
    }
    @Override
    public ResultDTO<?> getAllProductsByCategoryId(Long categoryId) {
        List<Product> products = productRepo.findByCategoryId(categoryId);

        if (products.isEmpty()) {
            throw new NotFoundException("No products found for this category");
        }

        List<ProductDto> productDtoList = products.stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .collect(Collectors.toList());

        return ResultDTO.builder()
                .status(200)  // HTTP 200 OK
                .message("Products retrieved successfully for category ID " + categoryId)
                .data(productDtoList)
                .build();
    }

    @Override
    public ResultDTO<?> searchByIdOrName(String searchValue) {
        Long id = null;

        try {
            id = Long.parseLong(searchValue);
        } catch (NumberFormatException ignored) {
            // Not Numeric
        }

        List<Product> products = productRepo.findByWsCodeContainingOrNameContainingIgnoreCase(searchValue, searchValue);

        if (products.isEmpty()) {
            throw new NotFoundException("No Products Found");
        }

        List<ProductDto> productDtos = products.stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .collect(Collectors.toList());

        return ResultDTO.builder()
                .status(200)  // HTTP 200 OK
                .message("Products found successfully")
                .data(productDtos)
                .build();
    }


    @Override
    public ResultDTO<?> addExtraImages(Long productId, List<String> imageUrls) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        // Map image URLs to ExtraImage entities
        List<ExtraImage> extraImages = imageUrls.stream().map(url -> {
            ExtraImage image = new ExtraImage();
            image.setUrl(url);
            image.setProduct(product);
            return image;
        }).collect(Collectors.toList());

        extraImageRepo.saveAll(extraImages);

        return ResultDTO.builder()
                .status(200)  // HTTP 200 OK
                .message("Extra images added successfully")
                .build();
    }


    @Override
    public ResultDTO<?> getExtraImages(Long productId) {
        List<ExtraImage> extraImages = extraImageRepo.findByProductId(productId);

        if (extraImages.isEmpty()) {
            throw new NotFoundException("No Extra Images were Found for productId: " + productId);
        }

        List<ExtraImageDto> imageDtos = extraImages.stream()
                .map(extraImage -> ExtraImageDto.builder()
                        .id(extraImage.getId())
                        .imageUrl(extraImage.getUrl())
                        .build())
                .collect(Collectors.toList());

        return ResultDTO.builder()
                .status(200)
                .message("Successfully retrieved extra images for productId: " + productId)
                .data(imageDtos)
                .build();
    }

    @Override
    public ResultDTO<?> deleteExtraImage(Long productId, Long imageId) {
        try {
            ExtraImage image = extraImageRepo.findById(imageId)
                    .filter(img -> img.getProduct().getId().equals(productId))
                    .orElseThrow(() -> new RuntimeException("Image not found or doesn't belong to the specified product"));

            extraImageRepo.delete(image);

            return ResultDTO.builder()
                    .status(200)
                    .message("Image deleted successfully!")
                    .build();
        } catch (Exception e) {
            return ResultDTO.builder()
                    .status(500)
                    .message("Failed to delete the image: " + e.getMessage())
                    .build();
        }
    }

//    @Override
//    public ResultDTO<?> deleteExtraImage(Long productId, Long imageId) {
//
//    }


}


// Extra Code
//    @Override
//    public ResultDTO<?> getProductsByCategory(Long categoryId) {
//        List<Product> products = productRepo.findByCategoryId(categoryId);
//
//        if (products.isEmpty()) {
//            throw new NotFoundException("No products found for this category");
//        }
//
//        List<ProductDto> productDtoList = products.stream()
//                .map(entityDtoMapper::mapProductToDtoBasic)
//                .collect(Collectors.toList());
//
//        return ResultDTO.builder()
//                .status(200)  // HTTP 200 OK
//                .message("Products retrieved successfully for category ID " + categoryId)
//                .data(productDtoList)
//                .build();
//    }