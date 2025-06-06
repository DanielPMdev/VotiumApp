package danielpm.dev.service.impl;

import danielpm.dev.entity.Event;
import danielpm.dev.entity.Market;
import danielpm.dev.repository.MarketRepository;
import danielpm.dev.service.MarketService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author danielpm.dev
 */
@Service
public class MarketServiceImpl implements MarketService {

    private final MarketRepository marketRepository;

    public MarketServiceImpl(MarketRepository marketRepository) {
        this.marketRepository = marketRepository;
    }

    @Override
    public boolean existsById(Long id) {
        return marketRepository.existsById(id);
    }

    @Override
    public List<Market> getAllMarkets() {
        return marketRepository.findAll();
    }

    @Override
    public Optional<Market> getMarketById(Long id) {
        return marketRepository.findById(id);
    }

    @Override
    public Optional<List<Market>> getMarketByQuestion(String question) {
        return marketRepository.findMarketByQuestion(question);
    }

    @Override
    public Optional<List<Market>> getMarketsByEvent(Event event) {
        return marketRepository.findMarketByEvent(event);
    }

    @Override
    public Market createOrUpdateMarket(Market market) {
        marketRepository.save(market);
        return market;
    }

    @Override
    public void deleteMarketById(Long id) {
        marketRepository.deleteById(id);
    }

    @Override
    public void deleteAllMarkets() {
        marketRepository.deleteAll();
    }
}
