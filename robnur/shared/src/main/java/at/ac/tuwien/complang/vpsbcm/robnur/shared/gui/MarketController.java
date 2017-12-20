package at.ac.tuwien.complang.vpsbcm.robnur.shared.gui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Bouquet;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.EndProduct;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Idable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableBasket;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.MarketService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class MarketController {
    
    public TableView<EndProduct> tvMarket;
    public TableColumn<EndProduct, Integer> tcIndex;
    public TableColumn<EndProduct, String> tcBasketId;
    public TableColumn<EndProduct, String> tcType;
    public TableColumn<EndProduct, Button> tcInfo;
    public TableColumn<EndProduct, Button> tcBuy;
    public TableColumn<EndProduct, String> tcPackRobot;


    private MarketService marketService = RobNurGUI.marketService;

    private synchronized void updateData(List<VegetableBasket> vegetableBasketList, List<Bouquet> bouquetList) {
        ObservableList<EndProduct> obs = tvMarket.getItems();
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
        tcIndex.setCellValueFactory(column-> new ReadOnlyObjectWrapper<>(column.getTableView().getItems().indexOf(column.getValue())+1));

        tcBasketId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getId()));
        tcType.setCellValueFactory(column -> new ReadOnlyStringWrapper(getType(column.getValue())));

        tcPackRobot.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getPackingRobotIdsAsString()));

        tcInfo.setCellValueFactory(column -> {
            Button btn = new Button("Infos");

            btn.setOnAction(event -> {
                EndProductInformationDialog bi = new EndProductInformationDialog();
                bi.show(column.getValue());
            });

            return new ReadOnlyObjectWrapper<>(btn);
        });

        tcBuy.setCellValueFactory(column -> {
            Button btn = new Button("Kaufen");

            btn.setOnAction(event -> {
                EndProduct product = column.getValue();
                if(product instanceof VegetableBasket) {
                    marketService.sellVegetableBasket((VegetableBasket) product);
                } else if(product instanceof Bouquet) {
                    marketService.sellBouquet((Bouquet) product);
                }
            });

            return new ReadOnlyObjectWrapper<>(btn);
        });
    }
}
