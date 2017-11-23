package at.ac.tuwien.complang.vpsbcm.robnur.spacebased;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.gui.RobNurGUI;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.gui.StorageController;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.StorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.StorageServiceImpl;
import org.mozartspaces.core.MzsCoreException;

import java.net.URI;
import java.net.URISyntaxException;

public class GUI {
    public static void main(String[] args) throws URISyntaxException, MzsCoreException {
        StorageService storageService = new StorageServiceImpl(new URI("xvsm://localhost:9876"));

        RobNurGUI.storageService = storageService;
        RobNurGUI.configService = new ConfigService();

        RobNurGUI robNurGUI = new RobNurGUI();
        robNurGUI.execute(args);

    }
}
