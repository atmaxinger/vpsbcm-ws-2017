package at.ac.tuwien.complang.vpsbcm.robnur.spacebased;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.customergui.CustomerGUI;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.OrderService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.ConfigServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.OrderServiceImpl;

import java.net.URI;
import java.net.URISyntaxException;

public class SpaceCustomerGUI {

    public static void main(String[] args) throws URISyntaxException {
        URI serverUri = new URI("xvsm://localhost:9876");

        ConfigService configService = new ConfigServiceImpl(serverUri);
        OrderService orderService = new OrderServiceImpl(serverUri);

        CustomerGUI.configService = configService;
        CustomerGUI.orderService = orderService;
        CustomerGUI.address = "WASINED";

        CustomerGUI customerGUI = new CustomerGUI();
        customerGUI.execute(args);
    }
}
