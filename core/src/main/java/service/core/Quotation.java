package service.core;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Class to store the quotations returned by the quotation services
 * 
 * @author Rem
 *
 */

@WebService
public class Quotation {
	public Quotation(String company, String reference, double price) {
		this.company = company;
		this.reference = reference;
		this.price = price;
		
	}
	
	public String company;
	public String reference;
	public double price;

	public Quotation() {}
}
