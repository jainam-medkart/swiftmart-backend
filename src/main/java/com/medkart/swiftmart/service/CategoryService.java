package com.medkart.swiftmart.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.medkart.swiftmart.dto.CategoryDto;
import com.medkart.swiftmart.dto.Response;
import com.medkart.swiftmart.entity.Category;
import com.medkart.swiftmart.mapper.EntityDtoMapper;
import com.medkart.swiftmart.repository.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final EntityDtoMapper entityDtoMapper;
    private final CategoryRepo categoryRepo;

    public Response createCategory(CategoryDto categoryDto) {
        // Check if category with the same name already exists
        if (categoryRepo.existsByName(categoryDto.getName())) {
            return Response.builder()
                    .status(400)
                    .message("Category with the same name already exists")
                    .build();
        }

        if(categoryDto.getImage() == null || categoryDto.getName().isEmpty()){
            return Response.builder()
                    .status(400)
                    .message("All Fields are required")
                    .build();
        }

        // Create and save the new category
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setImage(categoryDto.getImage());
        categoryRepo.save(category);

        return Response.builder()
                .status(200)
                .message("Category Created Successfully")
                .build();
    }

    public Response updateCategory(CategoryDto categoryDto) {
        Category category = categoryRepo.findById(categoryDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryDto.getId()));
        category.setName(categoryDto.getName());
        categoryRepo.save(category);
        return Response.builder()
                .status(200)
                .message("Category Updated Successfully")
                .build();
    }

    public Response deleteCategory(CategoryDto categoryDto) {
        Category category = categoryRepo.findById(categoryDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryDto.getId()));
        categoryRepo.delete(category);
        return Response.builder()
                .status(200)
                .message("Category Deleted Successfully")
                .build();
    }

    public Response getAllCategories() {
        List<Category> categories = categoryRepo.findAll();
        List<CategoryDto> categoryDtos = categories.stream().map(category -> entityDtoMapper.mapCategoryToDtoBasic(category)).toList();

        return Response.builder()
                .status(200)
                .categoryList(categoryDtos)
                .build();
    }


    public Response getCategoryById(Long categoryId) {
        Category category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Category Not Found"));
        CategoryDto categoryDto = entityDtoMapper.mapCategoryToDtoBasic(category);
        return Response.builder()
                .status(200)
                .category(categoryDto)
                .build();
    }

    public Response deleteCategory(Long categoryId) {
        Category category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Category Not Found"));
        categoryRepo.delete(category);
        return Response.builder()
                .status(200)
                .message("Category was deleted successfully")
                .build();
    }

    public Response updateCategory(Long categoryId, CategoryDto categoryRequest) {
        Category category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Category Not Found"));
        category.setName(categoryRequest.getName());
        category.setImage(categoryRequest.getImage());
        categoryRepo.save(category);
        return Response.builder()
                .status(200)
                .message("category updated successfully")
                .build();
    }

}
