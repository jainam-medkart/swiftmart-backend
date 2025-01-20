package com.medkart.swiftmart.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.cloudinary.Cloudinary;
import com.medkart.swiftmart.dto.ExtraImageDto;
import com.medkart.swiftmart.dto.ProductDto;
import com.medkart.swiftmart.dto.Response;
import com.medkart.swiftmart.entity.Category;
import com.medkart.swiftmart.entity.ExtraImage;
import com.medkart.swiftmart.entity.Product;
import com.medkart.swiftmart.entity.Tag;
import com.medkart.swiftmart.mapper.EntityDtoMapper;
import com.medkart.swiftmart.repository.CategoryRepo;
import com.medkart.swiftmart.repository.ExtraImageRepo;
import com.medkart.swiftmart.repository.ProductRepo;
import com.medkart.swiftmart.repository.TagRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;
    private final EntityDtoMapper entityDtoMapper;
    private final ExtraImageRepo extraImageRepo;
    private final Cloudinary cloudinary;
    private final TagRepo tagRepo;


    public Response createProduct(Long categoryId , String image, String name, String description, BigDecimal price, BigDecimal mrp, Long qty, Long productSize) {
        Category category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Category Not Found"));
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setImageUrl(image);
        product.setPrice(price);
        product.setMrp(mrp);
        product.setQty(qty);
        product.setProductSize(productSize);
        product.setCategory(category);

        productRepo.save(product);
        return Response.builder()
                .status(200)
                .message("Product Created Successfully")
                .build();
    }

    public Response updateProduct(Long productId , Long categoryId , String image, String name, String description, BigDecimal price, BigDecimal mrp, Long qty, Long productSize) {
        Product product = productRepo.findById(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));

        boolean isUpdated = false;

        if (name != null && !name.equals(product.getName())) {
            product.setName(name);
            isUpdated = true;
        }
        if (description != null && !description.equals(product.getDescription())) {
            product.setDescription(description);
            isUpdated = true;
        }
        if (image != null && !image.equals(product.getImageUrl())) {
            product.setImageUrl(image);
            isUpdated = true;
        }
        if (price != null && price.compareTo(product.getPrice()) != 0) {
            product.setPrice(price);
            isUpdated = true;
        }
        if (mrp != null && mrp.compareTo(product.getMrp()) != 0) {
            product.setMrp(mrp);
            isUpdated = true;
        }
        if (qty != null && !qty.equals(product.getQty())) {
            product.setQty(qty);
            isUpdated = true;
        }
        if (productSize != null && !productSize.equals(product.getProductSize())) {
            product.setProductSize(productSize);
            isUpdated = true;
        }

        if (isUpdated) {
            productRepo.save(product);
            return Response.builder()
                    .status(200)
                    .message("Product Updated Successfully")
                    .build();
        } else {
            return Response.builder()
                    .status(400)
                    .message("No changes detected to update the product")
                    .build();
        }
    }

    public Response deleteProduct(Long productId) {
        Product product = productRepo.findById(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));
        productRepo.delete(product);
        return Response.builder()
                .status(200)
                .message("Product Deleted Successfully")
                .build();
    }

    public Response getProductById(Long productId) {
        Product product = productRepo.findById(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));
        ProductDto productDto = entityDtoMapper.mapProductToDtoBasic(product);
        return Response.builder()
                .status(200)
                .product(productDto)
                .build();
    }

    public Response getAllProducts() {
        List<ProductDto> productList = productRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .toList();

        return Response.builder()
                .status(200)
                .productList(productList)
                .build();
    }

    public Response getProductsByCategory(Long categoryId) {
        List<Product> products = productRepo.findByCategoryId(categoryId);
        if(products.isEmpty()){
            throw new NotFoundException("No Products found for this category");
        }
        List<ProductDto> productDtoList = products.stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .productList(productDtoList)
                .build();

    }

    public Response searchProduct(String searchValue) {
        List<Product> products = productRepo.findByNameOrDescriptionContaining(searchValue, searchValue);

        if(products.isEmpty())
            throw new NotFoundException("No Products Found");

        List<ProductDto> productDtos = products.stream().map(entityDtoMapper::mapProductToDtoBasic).toList();
        return Response.builder()
                .status(200)
                .productList(productDtos)
                .build();
    }


    public Response searchByIdOrName(String searchValue) {
        Long id = null;

        // Try to parse searchValue to a Long for id matching
        try {
            id = Long.parseLong(searchValue);
        } catch (NumberFormatException ignored) {
            // If parsing fails, it means the searchValue is not numeric
        }

//        List<Product> products = productRepo.findByIdOrNameContainingIgnoreCase(id, searchValue);

        List<Product> products = productRepo.findByWsCodeContainingOrNameContainingIgnoreCase(searchValue, searchValue);

        if (products.isEmpty()) {
            throw new NotFoundException("No Products Found");
        }

        List<ProductDto> productDtos = products.stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .toList();

        return Response.builder()
                .status(200)
                .productList(productDtos)
                .build();
    }

    @Transactional
    public Response addExtraImages(Long productId, List<String> imageUrls) {
        Product product = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        List<ExtraImage> extraImages = imageUrls.stream().map(url -> {
            ExtraImage image = new ExtraImage();
            image.setUrl(url);
            image.setProduct(product);
            return image;
        }).collect(Collectors.toList());
        extraImageRepo.saveAll(extraImages);

        return Response.builder().
                status(200)
                .build();
    }

    public Response getExtraImages1(Long productId) {
        // Fetch extra images for the given productId using the ExtraImageRepo
        List<ExtraImage> extraImages = extraImageRepo.findByProductId(productId);

        // Map each ExtraImage to ExtraImageDto
        List<ExtraImageDto> imageDtos = extraImages.stream()
                .map(extraImage -> {
                    ExtraImageDto extraImageDto = new ExtraImageDto();
                    extraImageDto.setId(extraImage.getId());
                    extraImageDto.setImageUrl(extraImage.getUrl());
                    return extraImageDto;
                })
                .collect(Collectors.toList());

        // Return a Response containing the list of ExtraImageDto
        return Response.builder()
                .message("Successfully retrieved extra images")
                .status(200)
                .data(imageDtos)
                .build();
    }

    @Transactional
    public Response getExtraImages(Long productId) {
        // Fetch extra images for the given productId
        List<ExtraImage> extraImages = extraImageRepo.findByProductId(productId);

        // Map each ExtraImage to ExtraImageDto
        List<ExtraImageDto> imageDtos = extraImages.stream()
                .map(extraImage -> {
                    ExtraImageDto extraImageDto = new ExtraImageDto();
                    extraImageDto.setId(extraImage.getId());
                    extraImageDto.setImageUrl(extraImage.getUrl());
                    return extraImageDto;
                })
                .collect(Collectors.toList());

        // Return response to the caller
        return Response.builder()
                .message("Successfully retrieved extra images for productId: " + productId)
                .status(200)
                .data(imageDtos)
                .build();
    }


    @Transactional
    public Response deleteExtraImage(Long productId, Long imageId) {
        try {
            ExtraImage image = extraImageRepo.findById(imageId)
                    .filter(img -> img.getProduct().getId().equals(productId))
                    .orElseThrow(() -> new RuntimeException("Image not found or doesn't belong to the specified product"));

            extraImageRepo.delete(image);

            // Success Response
            return Response.builder()
                    .status(200)
                    .message("Image deleted successfully!")
                    .build();
        } catch (Exception e) {
            // Error Response
            return Response.builder()
                    .status(500)
                    .message("Failed to delete the image: " + e.getMessage())
                    .build();
        }
    }

//    Cloudinary Implementation

//    @Transactional
//    public Response deleteExtraImage(Long productId, Long imageId) {
//        // Fetch the ExtraImage by productId and imageId from the database
//        ExtraImage extraImage = extraImageRepo.findById(imageId)
//                .orElseThrow(() -> new RuntimeException("Extra image not found for the given product"));
//
//        // Verify the image belongs to the correct product
//        if (!extraImage.getProduct().getId().equals(productId)) {
//            throw new RuntimeException("Image does not belong to the specified product");
//        }
//
//        // Delete the image from Cloudinary using its publicId
//        try {
//            cloudinary.uploader().destroy(extraImage.getPublicId(), ObjectUtils.emptyMap());
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to delete the image from Cloudinary: " + e.getMessage());
//        }
//
//        // Delete the ExtraImage record from the database
//        extraImageRepo.delete(extraImage);
//
//        return Response.builder()
//                .message("Successfully deleted extra image: " + extraImage.getId())
//                .status(200)
//                .build();
//    }

    @Transactional
    public void updateExtraImage(Long productId, Long imageId, String newImageUrl) {
        ExtraImage image = extraImageRepo.findById(imageId)
                .filter(img -> img.getProduct().getId().equals(productId))
                .orElseThrow(() -> new RuntimeException("Image not found or doesn't belong to the specified product"));
        image.setUrl(newImageUrl);
        extraImageRepo.save(image);
    }


    public Response createProduct(ProductDto productDto) {
        // Validate required fields
        validateProductDto(productDto);

        // Fetch category by ID or throw exception if not found
        Category category = categoryRepo.findById(productDto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category with ID " + productDto.getCategoryId() + " Not Found"));

        // Create new Product and populate fields from DTO
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImageUrl(productDto.getImageUrl());
        product.setCategory(category);
        product.setMrp(productDto.getMrp());
        product.setQty(productDto.getQty());
        product.setProductSize(productDto.getProductSize());

        // Map and persist tags
        Set<Tag> tags = mapTags(productDto.getTags());
        product.setTags(tags);

        // Save the product
        Product savedProduct = productRepo.save(product);

        // Build the response object
        return Response.builder()
                .status(200)
                .productId(savedProduct.getId())
                .message("Product Created Successfully")
                .build();
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

//        if(productDto.getMrp().compareTo(productDto.getPrice()) > 0) {
//            throw new IllegalArgumentException("Sales Price must be less than MRP");
//        }
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


}
