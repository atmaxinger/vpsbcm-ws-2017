package at.ac.tuwien.complang.vpsbcm.robnur.spacebased;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.customergui.CustomerGUI;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.OrderService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.ConfigServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.DeliverStorageServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.DeliveryStorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.OrderServiceImpl;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

public class SpaceCustomerGUI {


    public static void main(String[] args) throws URISyntaxException {

        int myPort = 10001;

        URI serverUri = new URI("xvsm://localhost:9876");

        MzsCore myCore = null;
        boolean succeeded = false;
        Random random = new Random();

        do {
            myPort = random.nextInt(50000)+10000;
            try {
                myCore = DefaultMzsCore.newInstance(myPort);
                succeeded = true;
            } catch (Throwable e) {
                System.err.println("trying next port...");
            }
        } while (!succeeded);

        System.out.println("MY PORT: " + myPort);


        ConfigService configService = new ConfigServiceImpl(serverUri);
        OrderService orderService = new OrderServiceImpl(serverUri);
        DeliveryStorageService deliveryStorageService = new DeliverStorageServiceImpl(myCore.getConfig().getSpaceUri());

        CustomerGUI.configService = configService;
        CustomerGUI.orderService = orderService;
        CustomerGUI.deliveryStorageService = deliveryStorageService;
        CustomerGUI.address = myCore.getConfig().getSpaceUri().toString();

        CustomerGUI customerGUI = new CustomerGUI();
        customerGUI.execute(args);
    }
}
