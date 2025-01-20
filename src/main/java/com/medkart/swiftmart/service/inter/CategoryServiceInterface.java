package com.medkart.swiftmart.service.inter;

import com.medkart.swiftmart.dto.CategoryDto;
import com.medkart.swiftmart.dto.Response;

public interface CategoryServiceInterface {

    public Response createCategory(CategoryDto categoryDto);
    public Response updateCategory(CategoryDto categoryDto);
    public Response deleteCategory(CategoryDto categoryDto);
    public Response getAllCategories();
    public Response getCategoryById(String categoryId);
    public Response updateCategory(Long id, CategoryDto categoryDto);

}
