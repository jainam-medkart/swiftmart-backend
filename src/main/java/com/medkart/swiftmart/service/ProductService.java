package com.medkart.swiftmart.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.medkart.swiftmart.dto.ProductDto;
import com.medkart.swiftmart.dto.Response;
import com.medkart.swiftmart.entity.Category;
import com.medkart.swiftmart.entity.Product;
import com.medkart.swiftmart.mapper.EntityDtoMapper;
import com.medkart.swiftmart.repository.CategoryRepo;
import com.medkart.swiftmart.repository.ProductRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;
    private final EntityDtoMapper entityDtoMapper;

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

        List<Product> products = productRepo.findByIdOrNameContainingIgnoreCase(id, searchValue);

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


}
