package danielpm.dev.service.impl;

import danielpm.dev.entity.Market;
import danielpm.dev.entity.Option;
import danielpm.dev.repository.OptionRepository;
import danielpm.dev.service.EventService;
import danielpm.dev.service.OptionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author danielpm.dev
 */
@Service
public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;
    private final EventService eventService;

    public OptionServiceImpl(OptionRepository optionRepository, EventService eventService) {
        this.optionRepository = optionRepository;
        this.eventService = eventService;
    }

    @Override
    public boolean existsById(Long id) {
        return optionRepository.existsById(id);
    }

    @Override
    public List<Option> getAllOptions() {
        return optionRepository.findAll();
    }

    @Override
    public Optional<Option> getOptionById(Long id) {
        return optionRepository.findById(id);
    }

    @Override
    public Optional<List<Option>> getOptionByText(String text) {
        return optionRepository.findAllByText(text);
    }

    @Override
    public Optional<List<Option>> getOptionsByMarketId(Long marketId) {
        eventService.closeExpiredEvents();
        return optionRepository.findAllByMarket_Id(marketId);
    }

    @Override
    public Optional<List<Option>> getOptionByIsWinner(boolean isWinner) {
        return optionRepository.findAllByIsWinner(isWinner);
    }

    @Override
    public Option createOrUpdateOption(Option option) {
        optionRepository.save(option);
        return option;
    }

    @Override
    public void deleteOptionById(Long id) {
        optionRepository.deleteById(id);
    }

    @Override
    public void deleteAllOptions() {
        optionRepository.deleteAll();
    }
}
