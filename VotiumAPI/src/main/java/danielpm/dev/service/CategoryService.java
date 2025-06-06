package danielpm.dev.service;

import danielpm.dev.entity.Category;

import java.util.List;
import java.util.Optional;

/**
 * @author danielpm.dev
 */
public interface CategoryService {
    
    boolean existsById(Long id);

    //Methods retrive
    List<Category> getAllCategorys();

    Optional<Category> getCategoryById(Long id);

    Optional<List<Category>> getCategoryByName(String name);

    //Methods create / update
    Category createOrUpdateCategory(Category category);

    //Methods delete
    void deleteCategoryById(Long id);

    void deleteAllCategorys();

}
