package service.core;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;

public class Client {
        public static void main(String[] args) {
                try {
                        String host;
                        if(args.length == 0) {
                                host = "localhost";
                        } else {
                                host = args[0];
                        }
                        int port = 9000;
                        System.out.println("Please wait for a few seconds!");
                        //Sleep to wait for the services haven been registered
                        Thread.sleep(20000);
                        //make the request to the broker
                        URL wsdlUrl = new URL("http://" + host + ":" + port + "/broker?wsdl");
                        QName serviceName = new QName("http://core.service/", "BrokerService");
                        Service service = Service.create(wsdlUrl, serviceName);
                        QName portName = new QName("http://core.service/", "BrokerPort");
                        BrokerService quotationService = service.getPort(portName, BrokerService.class);
                        for (ClientInfo info : clients) {
                                displayProfile(info);
                                for(Quotation quotation :
                                    quotationService.getQuotations(info)) {
                                        displayQuotation(quotation);
                                }
                                System.out.println("\n");
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                } }

        /**
         * Display the client info nicely.
         *
         * @param info
         */
        public static void displayProfile(ClientInfo info) {
                System.out.println("|=================================================================================================================|");
                System.out.println("|                                     |                                     |                                     |");
                System.out.println(
                                "| Name: " + String.format("%1$-29s", info.name) +
                                                " | Gender: " + String.format("%1$-27s", (info.gender==ClientInfo.MALE?"Male":"Female")) +
                                                " | Age: " + String.format("%1$-30s", info.age)+" |");
                System.out.println(
                                "| License Number: " + String.format("%1$-19s", info.licenseNumber) +
                                                " | No Claims: " + String.format("%1$-24s", info.noClaims+" years") +
                                                " | Penalty Points: " + String.format("%1$-19s", info.points)+" |");
                System.out.println("|                                     |                                     |                                     |");
                System.out.println("|=================================================================================================================|");
        }

        /**
         * Display a quotation nicely - note that the assumption is that the quotation will follow
         * immediately after the profile (so the top of the quotation box is missing).
         *
         * @param quotation
         */
        public static void displayQuotation(Quotation quotation) {
                System.out.println(
                                "| Company: " + String.format("%1$-26s", quotation.company) +
                                                " | Reference: " + String.format("%1$-24s", quotation.reference) +
                                                " | Price: " + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.price))+" |");
                System.out.println("|=================================================================================================================|");
        }

        /**
         * Test Data
         */
        public static final ClientInfo[] clients = {
                        new ClientInfo("Niki Collier", ClientInfo.FEMALE, 43, 0, 5, "PQR254/1"),
                        new ClientInfo("Old Geeza", ClientInfo.MALE, 65, 0, 2, "ABC123/4"),
                        new ClientInfo("Hannah Montana", ClientInfo.FEMALE, 16, 10, 0, "HMA304/9"),
                        new ClientInfo("Rem Collier", ClientInfo.MALE, 44, 5, 3, "COL123/3"),
                        new ClientInfo("Jim Quinn", ClientInfo.MALE, 55, 4, 7, "QUN987/4"),
                        new ClientInfo("Donald Duck", ClientInfo.MALE, 35, 5, 2, "XYZ567/9")
        };
}
