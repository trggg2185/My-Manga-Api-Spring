package com.example.mymangaapp.mymangaapp.mapper;

import com.example.mymangaapp.mymangaapp.dto.request.CategoryRequest;
import com.example.mymangaapp.mymangaapp.dto.response.CategoryResponse;
import com.example.mymangaapp.mymangaapp.entity.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mangas", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category toCategory(CategoryRequest request);

    CategoryResponse toCategoryResponse(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mangas", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategoryFromRequest(@MappingTarget Category category, CategoryRequest request);
}
