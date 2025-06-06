package danielpm.dev.controller;

import danielpm.dev.dto.request.image.ImageUpdateRequest;
import danielpm.dev.dto.request.market.ChangeMarketDTO;
import danielpm.dev.dto.request.market.CreateMarketDTO;
import danielpm.dev.dto.request.market.MarketOptionsDTO;
import danielpm.dev.dto.request.option.CreateOptionDTO;
import danielpm.dev.entity.Market;
import danielpm.dev.entity.Event;
import danielpm.dev.entity.Option;
import danielpm.dev.service.MarketService;
import danielpm.dev.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author danielpm.dev
 */
@RestController
@RequestMapping("/api")
public class MarketController {
    
    private final MarketService marketService;
    private final EventService eventService;

    public MarketController(MarketService marketService, EventService eventService) {
        this.marketService = marketService;
        this.eventService = eventService;
    }

    /*
        GET http://localhost:8080/api/markets
     */
    @GetMapping("/markets")
    public ResponseEntity<List<Market>> findAll() {
        List<Market> marketList = marketService.getAllMarkets();
        if (marketList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(marketList);
    }

    /*
        GET http://localhost:8080/api/market/7
     */
    @GetMapping("/market/{id}")
    public ResponseEntity<Market> findById(@PathVariable Long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        return marketService.getMarketById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
        GET http://localhost:8080/api/market/7/options
     */
    @GetMapping("/market/{id}/options")
    public ResponseEntity<List<Option>> findAllOptions(@PathVariable Long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        return marketService.getMarketById(id)
                .map(market -> ResponseEntity.ok(market.getOptionList()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    

    /*
        POST http://localhost:8080/api/market
     */
    @PostMapping("/market")
    public ResponseEntity<Market> createMarket(@RequestBody CreateMarketDTO marketDTO) {
        if (marketDTO.getId() != null) //Si ya tiene un id asignado
            return ResponseEntity.badRequest().build();

        //Buscar el Event mediante el ID
        Event event = eventService.getEventById(marketDTO.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Market marketCreated = getMarket(marketDTO, event);

        marketService.createOrUpdateMarket(marketCreated);
        return ResponseEntity.ok(marketCreated);
    }

    /*
        POST http://localhost:8080/api/marketWithOptions
     */
    @PostMapping("/marketWithOptions")
    public ResponseEntity<Market> createMarket(@RequestBody MarketOptionsDTO marketDTO) {
        if (marketDTO.getId() != null) //Si ya tiene un id asignado
            return ResponseEntity.badRequest().build();

        //Buscar el Event mediante el ID
        Event event = eventService.getEventById(marketDTO.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Market marketCreated = getMarketWithOptions(marketDTO, event);

        marketService.createOrUpdateMarket(marketCreated);
        return ResponseEntity.ok(marketCreated);
    }

    /*
        PUT http://localhost:8080/api/market/3
     */
    @PutMapping("/market/{id}")
    public ResponseEntity<Market> updateMarket(@PathVariable Long id, @RequestBody ChangeMarketDTO marketDTO) {
        if (!marketService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        //Obtener el Market existente mediante el ID
        Market marketToUpdate = marketService.getMarketById(id)
                .orElseThrow(() -> new RuntimeException("Market not found"));

        marketToUpdate.setQuestion(marketDTO.getQuestion());

        Market updatedMarket = marketService.createOrUpdateMarket(marketToUpdate);
        return ResponseEntity.ok(updatedMarket);
    }

    /*
        PUT http://localhost:8080/api/marketWithOptions/3
     */
    @PutMapping("/marketWithOptions/{id}")
    public ResponseEntity<Market> updateMarket(@PathVariable Long id, @RequestBody MarketOptionsDTO marketDTO) {
        if (!marketService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        //Obtener el Market existente mediante el ID
        Market marketToUpdate = marketService.getMarketById(id)
                .orElseThrow(() -> new RuntimeException("Market not found"));

        marketToUpdate.setQuestion(marketDTO.getQuestion());

        // Elimina o reemplaza opciones anteriores si es necesario
        List<Option> updatedOptions = marketDTO.getOptions().stream()
                .map(dto -> {
                    Option option = new Option();
                    option.setId(dto.getId());
                    option.setText(dto.getText());
                    option.setPercentage(dto.getPercentage());
                    option.setIsWinner(Boolean.TRUE.equals(dto.getIsWinner()));
                    option.setMarket(marketToUpdate);
                    return option;
                })
                .collect(Collectors.toList());

        marketToUpdate.setOptionList(updatedOptions);

        Market updatedMarket = marketService.createOrUpdateMarket(marketToUpdate);
        return ResponseEntity.ok(updatedMarket);
    }


    /*
        DELETE http://localhost:8080/api/market/{identifier}
     */
    @DeleteMapping("/market/{identifier}")
    public ResponseEntity<Void> deleteMarketById(@PathVariable("identifier") Long id) {
        if (!marketService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        marketService.deleteMarketById(id);
        return ResponseEntity.noContent().build();
    }


    //AUXILIARY METHODS

    private Market getMarket(CreateMarketDTO marketDTO, Event event) {
        Market marketCreated = new Market();

        marketCreated.setId(marketDTO.getId());
        marketCreated.setQuestion(marketDTO.getQuestion());

        marketCreated.setEvent(event);
        return marketCreated;
    }

    private Market getMarketWithOptions(MarketOptionsDTO marketDTO, Event event) {
        // Crear la instancia del mercado
        Market marketCreated = new Market();
        marketCreated.setId(marketDTO.getId());
        marketCreated.setQuestion(marketDTO.getQuestion());
        marketCreated.setEvent(event); // Establecer el evento al mercado

//        BigDecimal initialPercentage = BigDecimal.valueOf(100.0)
//                .divide(BigDecimal.valueOf(marketDTO.getOptions().size()), 2, RoundingMode.HALF_UP);

        // Crear y asociar las opciones al mercado
        List<Option> options = new ArrayList<>();
        for (CreateOptionDTO optionDTO : marketDTO.getOptions()) {
            Option option = new Option();
            option.setText(optionDTO.getText());


            option.setPercentage(BigDecimal.ZERO.doubleValue());
            option.setIsWinner(optionDTO.getIsWinner());

            option.setMarket(marketCreated);
            options.add(option);
        }

        // Asociar la lista de opciones al mercado
        marketCreated.setOptionList(options);

        return marketCreated;
    }

}
