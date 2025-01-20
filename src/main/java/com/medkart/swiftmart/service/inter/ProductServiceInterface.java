package com.medkart.swiftmart.service.inter;

import com.medkart.swiftmart.dto.ProductDto;
import com.medkart.swiftmart.dto.ResultDTO;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductServiceInterface {

    ResultDTO<?> createProduct(ProductDto productDto);

    ResultDTO<?> updateProduct(Long productId, ProductDto productDto);

    ResultDTO<?> deleteProduct(Long productId);

    ResultDTO<?> getProductById(Long productId);

    ResultDTO<?> getAllProducts(int page, int pageSize);

    ResultDTO<?> getAllProductsByCategoryId(Long categoryId);

//    ResultDTO<?> getProductsByCategory(Long categoryId);

    ResultDTO<?> searchByIdOrName(String searchValue);

    ResultDTO<?> addExtraImages(Long productId, List<String> imageUrls);

    ResultDTO<?> getExtraImages(Long productId);

//    ResultDTO<?> deleteExtraImages(Long productId, Long imageId);

    ResultDTO<?> deleteExtraImage(Long productId, Long imageId);
}
