package guru.springframework.msscbeerservice.web.controller;

import guru.springframework.msscbeerservice.services.BeerService;
import guru.springframework.msscbeerservice.web.model.BeerDto;
import guru.springframework.msscbeerservice.web.model.BeerPagedList;
import guru.springframework.msscbeerservice.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Created by jt on 2019-05-12.
 */
@RequiredArgsConstructor
@RequestMapping("/api/v1/beer")
@RestController
public class BeerController {

    private final Integer DEFAULT_PAGE_NUM = 0;
    private final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerService beerService;

    @GetMapping()
    public ResponseEntity<BeerPagedList> getBeers(@RequestParam(name = "pageNum", required = false) Integer pageNum,
                                                  @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                                  @RequestParam(name = "beerName", required = false) String beerName,
                                                  @RequestParam(name = "beerStyle", required = false) BeerStyleEnum beerStyleEnum) {

        // Set default paging info if not provided
        if(pageNum == null || pageNum < 0) pageNum = DEFAULT_PAGE_NUM;
        if(pageSize == null || pageSize < 0) pageSize = DEFAULT_PAGE_SIZE;

        // Return the paged results
        BeerPagedList results = beerService.listBeers(beerName, beerStyleEnum, PageRequest.of(pageNum, pageSize));
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @GetMapping("/{beerId}")
    public ResponseEntity<BeerDto> getBeerById(@PathVariable("beerId") UUID beerId){
        return new ResponseEntity<>(beerService.getById(beerId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity saveNewBeer(@RequestBody @Validated BeerDto beerDto){
        return new ResponseEntity<>(beerService.saveNewBeer(beerDto), HttpStatus.CREATED);
    }

    @PutMapping("/{beerId}")
    public ResponseEntity updateBeerById(@PathVariable("beerId") UUID beerId, @RequestBody @Validated BeerDto beerDto){
        return new ResponseEntity<>(beerService.updateBeer(beerId, beerDto), HttpStatus.NO_CONTENT);
    }

}
