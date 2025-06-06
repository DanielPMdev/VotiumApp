package danielpm.dev.dto.request.image;

import lombok.Getter;
import lombok.Setter;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class ImageUpdateRequest {
    private String imageUrl;

    public ImageUpdateRequest() {
    }

    public ImageUpdateRequest(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
