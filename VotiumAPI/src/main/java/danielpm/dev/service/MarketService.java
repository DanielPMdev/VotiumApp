package danielpm.dev.service;

import danielpm.dev.entity.Event;
import danielpm.dev.entity.Market;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author danielpm.dev
 */
public interface MarketService {

    boolean existsById(Long id);

    //Methods retrive
    List<Market> getAllMarkets();

    Optional<Market> getMarketById(Long id);

    Optional<List<Market>> getMarketByQuestion(String question);

    Optional<List<Market>> getMarketsByEvent(Event event);

    //OTROS POSIBLES METODOS DE BUSQUEDA...

    //Methods create / update
    Market createOrUpdateMarket(Market market);

    //Methods delete
    void deleteMarketById(Long id);

    void deleteAllMarkets();

}
