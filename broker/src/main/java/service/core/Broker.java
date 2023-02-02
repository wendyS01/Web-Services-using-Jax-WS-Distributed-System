package service.core;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.*;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

/**
 * Implementation of the broker service that uses the Service Registry.
 * Broker listen to the services which are registered on the jsDns
 *
 *
 *
 */
@WebService
@SOAPBinding(style=Style.DOCUMENT,use= Use.LITERAL)
public class Broker {
        //use the hashset to store the urls
        static HashSet<String> urls = new HashSet<>();
        @WebMethod
        public LinkedList<Quotation> getQuotations(ClientInfo info) throws MalformedURLException {
                LinkedList<Quotation> quotations = new LinkedList<Quotation>();
                for (String url : urls) {
//                        System.out.println(url);
                        URL wsdlUrl = new URL(url);
                        QName serviceName = new QName("http://core.service/", "QuoterService");
                        Service service = Service.create(wsdlUrl, serviceName);
                        QName portName = new QName("http://core.service/", "QuoterPort");
                        QuoterService quotationService = service.getPort(portName, QuoterService.class);
                        Quotation quotation = quotationService.generateQuotation(info);
                        quotations.add(quotation);
                }
                return quotations;
        }

        public static void main(String[] args) {
                try {
                        Endpoint endpoint = Endpoint.create(new Broker());
                        HttpServer server = HttpServer.create(new InetSocketAddress(9000), 5);
                        server.setExecutor(Executors.newFixedThreadPool(5));
                        HttpContext context = server.createContext("/broker");
                        endpoint.publish(context);
                        server.start();
                        //listen the service which has already registered on
                        // JmDns
                        JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());
                        jmdns.addServiceListener("_quote._tcp.local.", new advertiseServiceListener());
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public static class advertiseServiceListener implements ServiceListener {
                @Override
                public void serviceAdded(ServiceEvent event) {
                        System.out.println("Service added: " + event.getInfo());
                }
                @Override
                public void serviceRemoved(ServiceEvent event) {
                        System.out.println("Service removed: " + event.getInfo());
                }
                @Override
                public void serviceResolved(ServiceEvent event) {
                        System.out.println("Service resolved: " + event.getInfo());
                        String path = event.getInfo().getURLs()[0];
//                        System.out.println(path);
                        if (path != null && path.contains("?wsdl")) {
                                urls.add(path);
                        }
                }

        }
}

