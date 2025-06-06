package danielpm.dev.controller;


import danielpm.dev.dto.request.category.FullCategoryDTO;
import danielpm.dev.entity.Category;
import danielpm.dev.service.CategoryService;
import danielpm.dev.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * @author danielpm.dev
 */
@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /*
                GET http://localhost:8080/api/categories
        */
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> findAll() {
        List<Category> categoryList = categoryService.getAllCategorys();
        if (categoryList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categoryList);
    }

    /*
        GET http://localhost:8080/api/category/7
     */
    @GetMapping("/category/{id}")
    public ResponseEntity<Category> findAllById(@PathVariable Long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
        POST http://localhost:8080/api/category
     */
    @PostMapping("/category")
    public ResponseEntity<Category> createCategory(@RequestBody FullCategoryDTO categoryDTO) {
        // Si ya tiene un ID asignado, no permitimos crearlo (esto es correcto para un POST)
        if (categoryDTO.getId() != null)
            return ResponseEntity.badRequest().build();

        Category categoryCreated = new Category();

        categoryCreated.setName(categoryDTO.getName());

        categoryService.createOrUpdateCategory(categoryCreated);
        return ResponseEntity.ok(categoryCreated);
    }

    /*
        PUT http://localhost:8080/api/category/3
     */
    @PutMapping("/category/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody FullCategoryDTO categoryDTO) {
        if (!categoryService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Obtener la Category existente desde el servicio para el ID y el Event
        Category categoryToUpdate = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        categoryToUpdate.setName(categoryDTO.getName());

        Category updatedCategory = categoryService.createOrUpdateCategory(categoryToUpdate);
        return ResponseEntity.ok(updatedCategory);
    }

    /*
        DELETE http://localhost:8080/api/category/{identifier}
     */
    @DeleteMapping("/category/{identifier}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable("identifier") Long id) {
        if (!categoryService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }
}
