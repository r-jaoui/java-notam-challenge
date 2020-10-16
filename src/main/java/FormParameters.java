package main.java;

/**
 * This is the FormParameters interface, that is used for
 * parsing form parameters onto a field that can be sent
 * via the URL requests
 *
 * @author r.jaoui
 * 
 */
public interface FormParameters {
	
	/**
	 * The parse method, called to get the form parameters field to be sent
	 * with the request
	 * 
	 * @return the parameters field
	 */
	public String parse();
}
