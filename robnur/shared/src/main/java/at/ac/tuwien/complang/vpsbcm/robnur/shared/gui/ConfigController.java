package at.ac.tuwien.complang.vpsbcm.robnur.shared.gui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.CultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class ConfigController {
    public TableView<VegetablePlantCultivationInformation> tvVegetables;
    public TableColumn<VegetablePlantCultivationInformation, String> tcVegetablesType;
    public TableColumn<CultivationInformation, Float> tcVegetablesGrowthRate;
    public TableColumn<CultivationInformation, Integer> tcVegetablesUpgradeLevel;
    public TableColumn<VegetablePlantCultivationInformation, Integer> tcVegetablesMaxHarvests;
    public TableColumn<CultivationInformation, Integer> tcVegetablesHarvests;
    public TableColumn<CultivationInformation, Integer> tcVegetablesSoil;
    public TableColumn<CultivationInformation, Integer> tcVegetablesWater;
    public TableColumn<CultivationInformation, Integer> tcVegetablesFertilizer;
    public TableColumn<CultivationInformation, String> tcVegetablesResearchRobots;
    public TableColumn<CultivationInformation, Integer> tcVegetablesVulnerability;
    public TableColumn<CultivationInformation, String> tcVegetablesPrice;


    public TableView<FlowerPlantCultivationInformation> tvFlowers;
    public TableColumn<FlowerPlantCultivationInformation, String> tcFlowersType;
    public TableColumn<CultivationInformation, Float> tcFlowersGrowthRate;
    public TableColumn<CultivationInformation, Integer> tcFlowersUpgradeLevel;
    public TableColumn<CultivationInformation, Integer> tcFlowersHarvests;
    public TableColumn<CultivationInformation, Integer> tcFlowersSoil;
    public TableColumn<CultivationInformation, Integer> tcFlowersWater;
    public TableColumn<CultivationInformation, Integer> tcFlowersFertilizer;
    public TableColumn<CultivationInformation, String> tcFlowersResearchRobots;
    public TableColumn<CultivationInformation, Integer> tcFlowersVulnerability;
    public TableColumn<CultivationInformation, String> tcFlowersPrice;


    private ConfigService configService = RobNurGUI.configService;


    private String prettyPrintRobots(List<String> robots) {
        String str = "";
        for(int i=0; i<robots.size(); i++) {
            str += robots.get(i);
            if(i+1 != robots.size()) {
                str += ", ";
            }
        }
        return str;
    }

    private String formatPrice(int priceInCents) {
        String s = String.format("€ %.02f", ((float)priceInCents/100.0f));
        return s;
    }

    @FXML
    public void initialize() {
        initVegeTable();
        initFlowerTable();
    }


    private synchronized void setVegeTableData(List<VegetablePlantCultivationInformation> cis) {
        ObservableList<VegetablePlantCultivationInformation> obs = tvVegetables.getItems();
        obs.clear();
        obs.addAll(cis);
    }

    private void initVegeTable() {
        setVegeTableData(configService.readAllVegetablePlantCultivationInformation(null));
        configService.onVegetableCultivationInformationChanged(this::setVegeTableData);

        tcVegetablesType.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getVegetableType().toString()));
        tcVegetablesGrowthRate.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(column.getValue().getGrowthRate()));
        tcVegetablesUpgradeLevel.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(column.getValue().getUpgradeLevel()));
        tcVegetablesMaxHarvests.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(column.getValue().getRemainingNumberOfHarvests()));
        tcVegetablesHarvests.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(column.getValue().getHarvest()));
        tcVegetablesSoil.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(column.getValue().getSoilAmount()));
        tcVegetablesWater.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(column.getValue().getWaterAmount()));
        tcVegetablesFertilizer.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(column.getValue().getFertilizerAmount()));
        tcVegetablesResearchRobots.setCellValueFactory(column -> new ReadOnlyStringWrapper(prettyPrintRobots(column.getValue().getResearchRobots())));
        tcVegetablesVulnerability.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(column.getValue().getVulnerability()));
        tcVegetablesPrice.setCellValueFactory(column -> new ReadOnlyStringWrapper(formatPrice(column.getValue().getPrice())));
    }


    private synchronized void setFlowerTableData(List<FlowerPlantCultivationInformation> cis) {
        ObservableList<FlowerPlantCultivationInformation> obs = tvFlowers.getItems();
        obs.clear();
        obs.addAll(cis);
    }

    private void initFlowerTable() {
        setFlowerTableData(configService.readAllFlowerPlantCultivationInformation(null));
        configService.onFlowerCultivationInformationChanged(this::setFlowerTableData);

        tcFlowersType.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getFlowerType().toString()));
        tcFlowersGrowthRate.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(column.getValue().getGrowthRate()));
        tcFlowersUpgradeLevel.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(column.getValue().getUpgradeLevel()));
        tcFlowersHarvests.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(column.getValue().getHarvest()));
        tcFlowersSoil.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(column.getValue().getSoilAmount()));
        tcFlowersWater.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(column.getValue().getWaterAmount()));
        tcFlowersFertilizer.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(column.getValue().getFertilizerAmount()));
        tcFlowersResearchRobots.setCellValueFactory(column -> new ReadOnlyStringWrapper(prettyPrintRobots(column.getValue().getResearchRobots())));
        tcFlowersVulnerability.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(column.getValue().getVulnerability()));
        tcFlowersPrice.setCellValueFactory(column -> new ReadOnlyStringWrapper(formatPrice(column.getValue().getPrice())));
    }
}
