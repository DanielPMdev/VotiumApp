package danielpm.dev.controller;


import danielpm.dev.dto.request.image.ImageUpdateRequest;
import danielpm.dev.dto.request.option.ChangeOptionDTO;
import danielpm.dev.dto.request.option.CreateOptionDTO;
import danielpm.dev.dto.request.option.WinnerOptionDTO;
import danielpm.dev.dto.response.event.ResponseEventDTO;
import danielpm.dev.dto.response.option.ResponseOptionDTO;
import danielpm.dev.entity.Bet;
import danielpm.dev.entity.Option;
import danielpm.dev.entity.Market;
import danielpm.dev.service.BetService;
import danielpm.dev.service.OptionService;
import danielpm.dev.service.MarketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author danielpm.dev
 */
@RestController
@RequestMapping("/api")
public class OptionController {

    private final OptionService optionService;
    private final MarketService marketService;
    private final BetService betService;

    public OptionController(OptionService optionService, MarketService marketService, BetService betService) {
        this.optionService = optionService;
        this.marketService = marketService;
        this.betService = betService;
    }

    /*
        GET http://localhost:8080/api/options
     */
    @GetMapping("/options")
    public ResponseEntity<List<Option>> findAll() {
        List<Option> optionList = optionService.getAllOptions();
        if (optionList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(optionList);
    }

    /*
        GET http://localhost:8080/api/option/7
     */
    @GetMapping("/option/{id}")
    public ResponseEntity<ResponseOptionDTO> findById(@PathVariable Long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }


        return optionService.getOptionById(id)
                .map(option -> ResponseEntity.ok(new ResponseOptionDTO(
                        option.getId(),
                        option.getText(),
                        option.getIsWinner(),
                        option.getPercentage(),
                        option.getMarket() != null ? option.getMarket().getId() : null,
                        option.getMarket() != null && option.getMarket().getEvent() != null ? option.getMarket().getEvent().getId() : null
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
        GET http://localhost:8080/api/option/7/bets
     */
    @GetMapping("/option/{id}/bets")
    public ResponseEntity<List<Bet>> findAllVotes(@PathVariable Long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        return optionService.getOptionById(id)
                .map(option -> ResponseEntity.ok(option.getBetList()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    

    /*
        POST http://localhost:8080/api/option
     */
    @PostMapping("/option")
    public ResponseEntity<Option> createOption(@RequestBody CreateOptionDTO optionDTO) {
        if (optionDTO.getId() != null) //Si ya tiene un id asignado
            return ResponseEntity.badRequest().build();

        //Buscar el Market mediante el ID
        Market market = marketService.getMarketById(optionDTO.getMarketId())
                .orElseThrow(() -> new RuntimeException("Market not found"));

        Option optionCreated = getOptionCreated(optionDTO, market);

        optionService.createOrUpdateOption(optionCreated);
        return ResponseEntity.ok(optionCreated);
    }

    /*
        PUT http://localhost:8080/api/option/3
     */
    @PutMapping("/option/{id}")
    public ResponseEntity<Option> updateOption(@PathVariable Long id, @RequestBody ChangeOptionDTO optionDTO) {
        if (!optionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        //Obtener el Optiono existente mediante el ID
        Option optionToUpdate = optionService.getOptionById(id)
                .orElseThrow(() -> new RuntimeException("Option not found"));

        optionToUpdate.setText(optionDTO.getText());

        Option updatedOption = optionService.createOrUpdateOption(optionToUpdate);
        return ResponseEntity.ok(updatedOption);
    }

    // /option/" + optionId + "/winner"

    /*
        PUT http://localhost:8080/api/option/3/winner
     */
    @PutMapping("/option/{id}/winner")
    public ResponseEntity<Option> setOptionAsWinner(@PathVariable Long id, @RequestBody WinnerOptionDTO optionDTO) {
        if (!optionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        //Obtener el Optiono existente mediante el ID
        Option optionToUpdate = optionService.getOptionById(id)
                .orElseThrow(() -> new RuntimeException("Option not found"));

        optionToUpdate.setIsWinner(optionDTO.getIsWinner());

        betService.markBetsAsWin(id);

        // Obtener todas las opciones del mismo mercado, la ganadora que ya hemos marcado y las perdedoras
        optionService.getOptionsByMarketId(optionDTO.getMarketId())
                .ifPresent(options -> {
                    options.stream()
                            .filter(opt -> !opt.getId().equals(id)) // Filtrar las opciones perdedoras
                            .forEach(opt -> betService.markBetsAsLose(opt.getId()));
                });

        Option updatedOption = optionService.createOrUpdateOption(optionToUpdate);
        return ResponseEntity.ok(updatedOption);
    }

    /*
        DELETE http://localhost:8080/api/option/{identifier}
     */
    @DeleteMapping("/option/{identifier}")
    public ResponseEntity<Void> deleteOptionById(@PathVariable("identifier") Long id) {
        if (!optionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        optionService.deleteOptionById(id);
        return ResponseEntity.noContent().build();
    }


    //AUXILIARY METHODS

    private Option getOptionCreated(CreateOptionDTO optionDTO, Market market) {
        Option optionCreated = new Option();

        optionCreated.setId(optionDTO.getId());
        optionCreated.setText(optionDTO.getText());

        //optionCreated.setIsWinner(optionDTO.getIsWinner());
        optionCreated.setIsWinner(false);

        //optionCreated.setPercentage(optionDTO.getPercentage());
        optionCreated.setPercentage(BigDecimal.ZERO.doubleValue());

        optionCreated.setMarket(market);
        return optionCreated;
    }
}
