package danielpm.dev.dto.request.category;

import danielpm.dev.entity.Category;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class FullCategoryDTO {
    private Long id;

    private String name;

    // Constructor vacío para deserialización
    public FullCategoryDTO() {

    }

    // Constructor para mapear desde la entidad Category
    public FullCategoryDTO(Category category) {
        this.id = category.getId();

        this.name = category.getName();

    }
}
