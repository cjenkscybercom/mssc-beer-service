package guru.springframework.msscbeerservice.web.mappers;

import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.services.BeerService;
import guru.springframework.msscbeerservice.services.inventory.BeerInventoryService;
import guru.springframework.msscbeerservice.web.model.BeerDto;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * We augment the mapping by using the beerInventoryService to update the qty on hand
 * for the DTOs returned for a given beer
 */
public abstract class BeerMapperDecorator implements BeerMapper {

    @Autowired
    private BeerMapper delegate;

    @Autowired
    private BeerInventoryService beerInventoryService;

    @Autowired
    public void setDelegate(BeerMapper delegate) {
        this.delegate = delegate;
    }

    @Autowired
    public void setBeerInventoryService(BeerInventoryService beerInventoryService) {
        this.beerInventoryService = beerInventoryService;
    }

    @Override
    public BeerDto beerToBeerDto(Beer beer) {
        BeerDto beerDto  = delegate.beerToBeerDto(beer);
        Integer qtyOnHand = beerInventoryService.getOnHandInventory(beer.getId());
        beerDto.setQuantityOnHand(qtyOnHand);
        return beerDto;
    }

}
