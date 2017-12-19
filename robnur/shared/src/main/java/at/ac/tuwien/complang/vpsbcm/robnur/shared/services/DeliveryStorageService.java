package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Bouquet;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableBasket;

import java.util.List;

public abstract class DeliveryStorageService {

    private StorageService.Callback<List<VegetableBasket>> vegetableBasketsChanged;
    private StorageService.Callback<List<Bouquet>> bouqetsChanged;

    protected void notifyVegetableBasketsChanged() {
        if(vegetableBasketsChanged != null) {
            vegetableBasketsChanged.handle(readAllVegetableBaskets());
        }
    }

    protected void notifyBouqetsChanged() {
        if(bouqetsChanged != null) {
            bouqetsChanged.handle(readAllBouqets());
        }
    }

    public void onVegetableBasketsChanged(StorageService.Callback<List<VegetableBasket>> vegetableBasketsChanged) {
        this.vegetableBasketsChanged = vegetableBasketsChanged;
    }

    public void onBouqetsChanged(StorageService.Callback<List<Bouquet>> bouquetsChanged) {
        this.bouqetsChanged = bouqetsChanged;
    }

    public abstract List<VegetableBasket> readAllVegetableBaskets();
    public abstract List<Bouquet> readAllBouqets();
}
