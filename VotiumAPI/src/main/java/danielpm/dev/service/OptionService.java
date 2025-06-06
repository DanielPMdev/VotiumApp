package danielpm.dev.service;

import danielpm.dev.entity.Event;
import danielpm.dev.entity.Market;
import danielpm.dev.entity.Option;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author danielpm.dev
 */
public interface OptionService {

    boolean existsById(Long id);

    //Methods retrive
    List<Option> getAllOptions();

    Optional<Option> getOptionById(Long id);

    Optional<List<Option>> getOptionByText(String text);

    Optional<List<Option>> getOptionsByMarketId(Long marketId);

    Optional<List<Option>> getOptionByIsWinner(boolean isWinner);

    //OTROS POSIBLES METODOS DE BUSQUEDA...

    //Methods create / update
    Option createOrUpdateOption(Option option);

    //Methods delete
    void deleteOptionById(Long id);

    void deleteAllOptions();
    
}
