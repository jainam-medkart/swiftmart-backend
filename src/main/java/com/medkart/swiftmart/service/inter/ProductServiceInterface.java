package com.medkart.swiftmart.service.inter;

import com.medkart.swiftmart.dto.Response;

import java.math.BigDecimal;
import java.util.List;

public interface ProductServiceInterface {

    public Response createProduct(Long categoryId , String image, String name, String description, BigDecimal price, BigDecimal mrp, Long qty, Long productSize);

    public Response updateProduct(Long productId , Long categoryId , String image, String name, String description, BigDecimal price, BigDecimal mrp, Long qty, Long productSize);

    public Response deleteProduct(Long productId);

    public Response getProductById(Long productId);

    public Response getAllProducts();

    public Response getAllProductsByCategoryId(Long categoryId);

    public Response getProductsByCategory(Long categoryId);

    public Response searchByIdOrName(String searchValue);

    public Response addExtraImages(Long productId , List<String> imageUrls);

    public Response getExtraImages(Long productId);

    public Response deleteExtraImages(Long productId);






}
