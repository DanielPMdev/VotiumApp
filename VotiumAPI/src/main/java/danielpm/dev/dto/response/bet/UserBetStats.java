package danielpm.dev.dto.response.bet;

import lombok.Getter;
import lombok.Setter;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class UserBetStats {
    private String username;
    private Long totalBets;
    private Long correctBets;
    private Double winPercentage;

    public UserBetStats(String username, Long totalBets, Long correctBets) {
        this.username = username;
        this.totalBets = totalBets;
        this.correctBets = correctBets;
        this.winPercentage = totalBets > 0 ? ((double) correctBets / totalBets) * 100 : 0.0;
    }
}
