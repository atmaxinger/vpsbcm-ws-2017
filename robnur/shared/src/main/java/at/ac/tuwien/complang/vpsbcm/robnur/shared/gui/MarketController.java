package at.ac.tuwien.complang.vpsbcm.robnur.shared.gui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Bouquet;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Idable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableBasket;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.MarketService;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class MarketController {
    
    public TableView<Idable> tvMarket;
    public TableColumn<Idable, Integer> tcIndex;
    public TableColumn<Idable, String> tcBasketId;
    public TableColumn<Idable, String> tcType;
    public TableColumn<Idable, Button> tcInfo;
    public TableColumn<Idable, Button> tcBuy;


    private MarketService marketService = RobNurGUI.marketService;

    private void updateData(List<VegetableBasket> vegetableBasketList, List<Bouquet> bouquetList) {
        ObservableList<Idable> obs = tvMarket.getItems();
        obs.clear();

        obs.addAll(vegetableBasketList);
        obs.addAll(bouquetList);
    }


    private String getType(Idable i) {
        if(i instanceof VegetableBasket) {
            return "Gemüsekorb";
        }
        else if(i instanceof Bouquet) {
            return "Blumenstrauß";
        }

        return "unbekannt";
    }

    @FXML
    public void initialize() {
        updateData(marketService.readAllVegetableBaskets(), marketService.readAllBouquets());
        marketService.onMarketChanged(this::updateData);

        tcIndex.setSortable(false);
        tcIndex.setCellValueFactory(column-> new ReadOnlyObjectWrapper<>(tvMarket.getItems().indexOf(column.getValue())+1));

        tcBasketId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getId()));
        tcType.setCellValueFactory(column -> new ReadOnlyStringWrapper(getType(column.getValue())));

        tcInfo.setCellValueFactory(column -> {
            Button btn = new Button("Infos");
            return new ReadOnlyObjectWrapper<>(btn);
        });

        tcBuy.setCellValueFactory(column -> {
            Button btn = new Button("Kaufen");
            return new ReadOnlyObjectWrapper<>(btn);
        });
    }
}
