package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Bouquet;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableBasket;

import java.util.List;

public abstract class DeliveryStorageService {
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DeliveryStorageService.class);


    private StorageService.Callback<List<VegetableBasket>> vegetableBasketsChanged;
    private StorageService.Callback<List<Bouquet>> bouqetsChanged;

    protected void notifyVegetableBasketsChanged() {
        logger.debug(String.format("vegetable baskets changed"));
        List<VegetableBasket> baskets = readAllVegetableBaskets();
        for(VegetableBasket basket : baskets) {
            logger.debug(String.format("  %s", basket.getId()));
        }

        if(vegetableBasketsChanged != null) {
            vegetableBasketsChanged.handle(baskets);
        }
    }

    protected void notifyBouqetsChanged() {
        logger.debug(String.format("bouqets changed"));
        List<Bouquet> bouquets = readAllBouquets();
        for(Bouquet bouquet : bouquets) {
            logger.debug(String.format("  %s", bouquet.getId()));
        }

        if(bouqetsChanged != null) {
            bouqetsChanged.handle(readAllBouquets());
        }
    }

    public void onVegetableBasketsChanged(StorageService.Callback<List<VegetableBasket>> vegetableBasketsChanged) {
        this.vegetableBasketsChanged = vegetableBasketsChanged;
    }

    public void onBouqetsChanged(StorageService.Callback<List<Bouquet>> bouqetsChanged) {
        this.bouqetsChanged = bouqetsChanged;
    }

    /**
     * read all vegetable baskets
     * @return a list of all vegetable baskets, null if unsuccessful
     */
    public abstract List<VegetableBasket> readAllVegetableBaskets();

    /**
     * read all  bouquets
     * @return a list of all bouquets, null if unsuccessful
     */
    public abstract List<Bouquet> readAllBouquets();
}
