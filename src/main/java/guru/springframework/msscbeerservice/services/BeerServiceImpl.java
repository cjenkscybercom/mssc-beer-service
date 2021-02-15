package guru.springframework.msscbeerservice.services;

import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.repositories.BeerRepository;
import guru.springframework.msscbeerservice.web.controller.NotFoundException;
import guru.springframework.msscbeerservice.web.mappers.BeerMapper;
import guru.springframework.msscbeerservice.web.model.BeerDto;
import guru.springframework.msscbeerservice.web.model.BeerPagedList;
import guru.springframework.msscbeerservice.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by jt on 2019-06-06.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BeerServiceImpl implements BeerService {
    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Cacheable(cacheNames = "beerListCache", condition = "#showInventoryOnHand == false")
    @Override
    public BeerPagedList listBeers(String beerName, BeerStyleEnum beerStyle, PageRequest pageRequest, Boolean showInventoryOnHand) {

        Page<Beer> beerPage;

        // Get either by name, style, or both
        if(!StringUtils.isEmpty(beerName) && !StringUtils.isEmpty(beerStyle)) {
            beerPage = beerRepository.findAllByBeerNameAndBeerStyle(beerName, beerStyle, pageRequest);
        } else if (!StringUtils.isEmpty(beerName)) {
            beerPage = beerRepository.findAllByBeerName(beerName, pageRequest);
        } else if(!StringUtils.isEmpty(beerStyle)) {
            beerPage = beerRepository.findAllByBeerStyle(beerStyle, pageRequest);
        } else {
            beerPage = beerRepository.findAll(pageRequest);
        }

        if(showInventoryOnHand) {
            return new BeerPagedList(
                    beerPage.getContent().stream()
                            .map(beerMapper::beerToBeerDtoWithInventoryOnHand)
                            .collect(Collectors.toList()),
                    beerPage.getPageable(),
                    beerPage.getTotalElements()
            );
        } else {
            return new BeerPagedList(
                    beerPage.getContent().stream()
                            .map(beerMapper::beerToBeerDto)
                            .collect(Collectors.toList()),
                    beerPage.getPageable(),
                    beerPage.getTotalElements()
            );
        }

    }

    @Cacheable(cacheNames = "beerCache", condition = "#showInventoryOnHand == false", key = "#beerId")
    @Override
    public BeerDto getById(UUID beerId, Boolean showInventoryOnHand) {

        Beer result = beerRepository.findById(beerId).orElseThrow(NotFoundException::new);

        if(showInventoryOnHand) {
            log.debug("Showing inventory on hand for beer " + beerId);
            return beerMapper.beerToBeerDtoWithInventoryOnHand(result);
        } else {
            return beerMapper.beerToBeerDto(result);
        }

    }

    @Cacheable(cacheNames = "beerUpcCache", condition = "#showInventoryOnHand == false", key = "#beerUpcId")
    @Override
    public BeerDto getByUpcId(String beerUpcId, Boolean showInventoryOnHand) {

        Beer result = beerRepository.findByUpc(beerUpcId).orElseThrow(NotFoundException::new);

        if(showInventoryOnHand) {
            log.debug("Showing inventory on hand for beer UPC " + beerUpcId);
            return beerMapper.beerToBeerDtoWithInventoryOnHand(result);
        } else {
            return beerMapper.beerToBeerDto(result);
        }

    }

    @Override
    public BeerDto saveNewBeer(BeerDto beerDto) {
        return beerMapper.beerToBeerDto(beerRepository.save(beerMapper.beerDtoToBeer(beerDto)));
    }

    @Override
    public BeerDto updateBeer(UUID beerId, BeerDto beerDto) {
        Beer beer = beerRepository.findById(beerId).orElseThrow(NotFoundException::new);

        beer.setBeerName(beerDto.getBeerName());
        beer.setBeerStyle(beerDto.getBeerStyle().name());
        beer.setPrice(beerDto.getPrice());
        beer.setUpc(beerDto.getUpc());

        return beerMapper.beerToBeerDto(beerRepository.save(beer));
    }
}
