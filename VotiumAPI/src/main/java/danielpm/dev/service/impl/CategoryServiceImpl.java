package danielpm.dev.service.impl;

import danielpm.dev.entity.Category;
import danielpm.dev.repository.CategoryRepository;
import danielpm.dev.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author danielpm.dev
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public List<Category> getAllCategorys() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Optional<List<Category>> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public Category createOrUpdateCategory(Category category) {
        categoryRepository.save(category);
        return category;
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public void deleteAllCategorys() {
        categoryRepository.deleteAll();
    }
}
