package service.core;


import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Endpoint;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Implementation of Quotation Service for Dodgy Drivers Insurance Company
 * A jmDNS was implemented to register dodgydrivers service
 *
 *
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public class Quoter extends AbstractQuotationService {
        // All references are to be prefixed with an DD (e.g. DD001000)
        public static final String PREFIX = "DD";
        public static final String COMPANY = "Dodgy Drivers Corp.";

        /**
         * Quote generation:
         * 5% discount per penalty point (3 points required for qualification)
         * 50% penalty for <= 3 penalty points
         * 10% discount per year no claims
         */
        @WebMethod
        public Quotation generateQuotation(ClientInfo info) {
                // Create an initial quotation between 800 and 1000
                double price = generatePrice(800, 200);

                // 5% discount per penalty point (3 points required for qualification)
                int discount = (info.points > 3) ? 5*info.points:-50;

                // Add a no claims discount
                discount += getNoClaimsDiscount(info);

                // Generate the quotation and send it back
                return new Quotation(COMPANY, generateReference(PREFIX), (price * (100-discount)) / 100);
        }

        private int getNoClaimsDiscount(ClientInfo info) {
                return 10*info.noClaims;
        }

        public static void main(String[] args) {
                try {
                        Endpoint endpoint = Endpoint.create(new Quoter());
                        HttpServer server = HttpServer.create(new InetSocketAddress(9003), 5);
                        server.setExecutor(Executors.newFixedThreadPool(5));
                        HttpContext context = server.createContext("/quotation");
                        endpoint.publish(context);
                        server.start();
                        //register dodgydrivers
                        // by jmDNS
                        JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());
                        String host= "localhost";
                        //pass the host of service of container if there is
                        if(args.length!=0) {
                                host = args[0];
                        }
                        String config = "path=http://"+host+":9003/quotation" +
                                        "?wsdl";
                        ServiceInfo dodgydriversService = ServiceInfo.create(
                                        "_quote._tcp.local.", "dodgydrivers",
                                        9003, config);
                        System.out.println(dodgydriversService);
                        jmdns.registerService(dodgydriversService);
                        Thread.sleep(1000000000);
                        jmdns.unregisterAllServices();
                } catch (Exception e) {
                        e.printStackTrace();
                } }

}
