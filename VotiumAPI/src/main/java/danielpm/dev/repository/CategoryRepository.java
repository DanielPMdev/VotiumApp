package danielpm.dev.repository;

import danielpm.dev.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author danielpm.dev@gmail.com
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<List<Category>> findByName(String name);
}
