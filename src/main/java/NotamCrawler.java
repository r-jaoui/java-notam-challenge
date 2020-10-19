package main.java;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the main class of the program
 *
 * @author r.jaoui
 * 
 */
public class NotamCrawler {


	/**
	 * Is the program in DEBUG mode ?
	 */
	public static final boolean DEBUG = false;
	/**
	 * Should the results be saved on a .csv file ?
	 */
	public static final boolean SAVE = true;

	/**
	 * The URL for the global NOTAM identifier search
	 */
	private static final String globalUrl = "http://notamweb.aviation-civile.gouv.fr/Script/IHM/Bul_FIR.php?FIR_Langue=FR";
	
	/**
	 * The URL for the specific NOTAM identifier search
	 */
	private static final String IdentifierUrl = "http://notamweb.aviation-civile.gouv.fr/Script/IHM/Bul_Notam.php?NOTAM_Langue=FR";

	/**
	 * The regex pattern to locate a NOTAM identifier
	 */
	private static final String IdentifierRegex = "[A-Z]{4}-[A-Z][0-9]{4}/[0-9]{2}";

	/**
	 * The char that defines the separator in the CSV files (for european standards, use ';', for USA/UK standards, use ',')
	 */
	private static final char CSVSeparator = ';';

	/**
	 * Program main method 
	 * 
	 * @param args are the main arguments received as the program is launched
	 */
	public static void main(final String[] args){
		try {
			System.out.println("_________________________  NOTAM CRAWLER _________________________\n");
			
			boolean modeCrawler = true;
			
			Scanner sc = new Scanner(System.in);
			String line;

			//Does the user want to use the crawler mode ?
			do {
				System.out.print("Do you want to manually enter a NOTAM identifier to lookup (y/n) ? : ");
				line = sc.nextLine();
				System.out.println();
			}
			while(!line.equals("y") && !line.equals("n"));
			
			modeCrawler = line.equals("n");
			
			System.out.println();
			
			BufferedWriter writer = null;
			
			if(!modeCrawler) {
				
				//Not Crawling mode : the NOTAM identifier has to be specified by the user
				
				String identifier;
				do {
					System.out.print("What NOTAM identifier do you want to lookup ? : ");
					identifier = sc.nextLine();
					System.out.println();
				}
				while(!Pattern.compile(IdentifierRegex).matcher(identifier).find());

				IdentifierFormParameters identifierParams = new IdentifierFormParameters(identifier);
				String identifierResponse = sendRequest(IdentifierUrl, identifierParams);
				
				//Verification that the response corresponds to an existing NOTAM identifier, otherwise this is notified to the user before ending the program
				boolean exists = IdentifierExists(identifierResponse);

				//If the program is used in SAVE mode, the received data is saved on a .csv file
				if(SAVE && exists) {
					System.out.println("Creating CSV File.\n");
					File dir = new File("output");
					if(!dir.exists()) dir.mkdir();
					String adress = "output/NOTAM_DATA_"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss-ss"))+".csv";
					File file = new File(adress);
					System.out.println("\tCSV File Created (\""+adress+"\")\n");
					writer = new BufferedWriter(new FileWriter(file));
					writer.write("code"+CSVSeparator+"Q"+CSVSeparator+"A"+CSVSeparator+"B"+CSVSeparator+"D"+CSVSeparator+"E"+CSVSeparator+"F"+CSVSeparator+"G\n");
				}
				
				System.out.println("Identifier :"+identifier);
				if(exists) {
					//Getting all fields and displaying them
					System.out.println("\tQ : "+findField(identifierResponse, "Q")+"");
					if(DEBUG) {
						System.out.println("\tA : "+findField(identifierResponse, "A")+"\n"+
										   "\tB : "+findField(identifierResponse, "B")+"\n"+
										   "\tD : "+findField(identifierResponse, "D")+"\n"+
										   "\tE : "+findField(identifierResponse, "E")+"\n"+
										   "\tF : "+findField(identifierResponse, "F")+"\n"+
										   "\tG : "+findField(identifierResponse, "G")+"\n");
					}
					System.out.println();
					if(SAVE) {
						//Saving all fields onto the .csv file
						writer.write(identifier+CSVSeparator+
								findField(identifierResponse, "Q")+CSVSeparator+
								findField(identifierResponse, "A")+CSVSeparator+
								findField(identifierResponse, "B")+CSVSeparator+
								findField(identifierResponse, "D")+CSVSeparator+
								findField(identifierResponse, "E")+CSVSeparator+
								findField(identifierResponse, "F")+CSVSeparator+
								findField(identifierResponse, "G")+"\n");
					}

					if(SAVE) {
						//Closing the BufferedWriter
						writer.close();
						System.out.println("\tCSV File closed\n");
					}
				}
				else {
					System.err.println("The identifier is unknown...\n");
				}
			}
			else {
				//Crawling mode : the NOTAM identifiers will be looked up by the program
				
				//Creation of the global request parameters
				GlobalFormParameters params = new GlobalFormParameters();
				
				System.out.println("Main request parameters created.\n");
				
				if(DEBUG) {
					System.out.println(params+"\n");
				}

				//Sending the request
				String globalResponse = sendRequest(globalUrl, params);
				
				System.out.println("Main Response recieved.\n");

				//Finding all identifier matches
				ArrayList<String> matches = findIdentifiers(globalResponse);
				
				System.out.println("\tResponse parsed ("+matches.size()+" identifiers found)\n");

				//If the program is used in SAVE mode, the received data is saved on a .csv file
				if(SAVE) {
					System.out.println("Creating CSV File.\n");
					File dir = new File("output");
					if(!dir.exists()) dir.mkdir();
					String adress = "output/NOTAM_DATA_"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss-ss"))+".csv";
					File file = new File(adress);
					System.out.println("\tCSV File Created (\""+adress+"\")\n");
					writer = new BufferedWriter(new FileWriter(file));
					writer.write("code"+CSVSeparator+"Q"+CSVSeparator+"A"+CSVSeparator+"B"+CSVSeparator+"D"+CSVSeparator+"E"+CSVSeparator+"F"+CSVSeparator+"G\n");
				}

				//looping through all matches and launching another request to gather all fields for each of them
				for(int i = 0; i < matches.size(); i++) {
					String match = matches.get(i);
					IdentifierFormParameters identifierParams = new IdentifierFormParameters(match);
					String identifierResponse = sendRequest(IdentifierUrl, identifierParams);
					System.out.println("("+(i+1)+"/"+matches.size()+") Identifier :"+match);
					boolean exists = IdentifierExists(identifierResponse);
					//Verification that the response for this specific identifier corresponds to an existing NOTAM identifier, otherwise this is notified to the user and continuing the loop
					if(exists) {
						//Getting all fields and displaying them
						System.out.println("\tQ : "+findField(identifierResponse, "Q")+"");
						if(DEBUG) {
							System.out.println("\tA : "+findField(identifierResponse, "A")+"\n"+
											   "\tB : "+findField(identifierResponse, "B")+"\n"+
											   "\tD : "+findField(identifierResponse, "D")+"\n"+
											   "\tE : "+findField(identifierResponse, "E")+"\n"+
											   "\tF : "+findField(identifierResponse, "F")+"\n"+
											   "\tG : "+findField(identifierResponse, "G")+"\n");
						}
						System.out.println();
						if(SAVE) {
							//Saving all fields onto the .csv file
							writer.write(match+CSVSeparator+
									findField(identifierResponse, "Q")+CSVSeparator+
									findField(identifierResponse, "A")+CSVSeparator+
									findField(identifierResponse, "B")+CSVSeparator+
									findField(identifierResponse, "D")+CSVSeparator+
									findField(identifierResponse, "E")+CSVSeparator+
									findField(identifierResponse, "F")+CSVSeparator+
									findField(identifierResponse, "G")+"\n");
						}
					}
					else {
						System.err.println("The identifier is unknown...\n");
					}
				}
				
				if(SAVE) {
					//Closing the BufferedWriter
					writer.close();
					System.out.println("\tCSV File closed\n");
				}
			}

			//Terminating the program
			sc.close();
			System.out.println("Program terminated.\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a request to the website (to gather all NOTAM identifiers to be
	 * manipulated or to lookup for a single iedntifier's data)
	 * 
	 * @param URL the URL the request must be sent to
	 * @param params are the parameters of the search (<code> FormParameters </code>)
	 * @return the HTLM response of the site as a <code> String </code> 
	 * @throws IOException if the request can't be sent (non existent URL...)
	 */
	public static String sendRequest(String URL, FormParameters params) throws IOException{
		//Creating the URL object
		URL obj = new URL(URL);
		HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setDoOutput(true);
		OutputStream os = httpURLConnection.getOutputStream();

		/*Writing the request parameters onto the URL connection's Stream (the parse() method exists since all FormParameters
		 *classes implement the FormParameters interface, in which the parse() function is defined
		 */
		
		os.write(params.parse().getBytes());
		os.flush();
		os.close();

		String result = "";
		int responseCode = httpURLConnection.getResponseCode();
		
		//For each request, a maximum of 10000 trials is allowed
		int trials = 0;
		while(trials < 10000) {
			if(responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
				String inputLine;

				//Writing the response on the result String
				while ((inputLine = in .readLine()) != null) {
					result += inputLine+"\n";
				}
				
				in.close();

				break;
			}
			else {
				//If the request isn't valid (not HTTP_OK), then we launch it again and increment the number of trials
				trials++;
				System.err.println("Connection trial n°"+trials+" failed...");
				
				obj = new URL(URL);
				httpURLConnection = (HttpURLConnection) obj.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				os = httpURLConnection.getOutputStream();
				
				os.write(params.parse().getBytes());
				os.flush();
				os.close();
				
				responseCode = httpURLConnection.getResponseCode();
			}
		}
		
		/*returning the result (if the 10000 trials weren't enough, the returned String is empty, and
		 *if the request is the global one, no identifier will be found,
		 *if the request is for an identifier, the IdentifierExists(String HTMLMessage) method will make sure that this request isn't valid
		 */
		return result;
	}

	/**
	 * Parses the HTML file received to find all NOTAM identifiers
	 * 
	 * @param HTMLMessage is the message received from the site
	 * @return the list of identifiers as an <code> ArrayList </code> 
	 * 
	 */
	public static ArrayList<String> findIdentifiers(String HTMLMessage){
		Pattern pattern = Pattern.compile(IdentifierRegex);
		Matcher matcher = pattern.matcher(HTMLMessage);

		//To find all identifier, we match, the identifier pattern of the full received HTML page
		ArrayList<String> matches = new ArrayList<String>();

		while(matcher.find()) {
			String identifier = matcher.group(0);
			if(!matches.contains(identifier)) matches.add(matcher.group(0));
			else continue;
		}

		return matches;
	}

	/**
	 * Verifies that the received HTML page corresponds to an existing NOTAM identifier's reponse
	 * 
	 * @param HTMLMessage the HTML page received from the site
	 * @return If the page corresponds to an existing NOTAM identifier
	 * 
	 */
	public static boolean IdentifierExists(String HTMLMessage){
		/*To verify that the response corresponds to an existing NOTAM identifier, we look for the number of results,
		 *and if that number is less than 1 or if a match isn't found, then the identifier doesn't exist.
		 */
		Pattern pattern = Pattern.compile("Nombre de NOTAM :[ ]+[0-9]+");
		Matcher matcher = pattern.matcher(HTMLMessage);

		if(matcher.find()) {
			return Integer.parseInt(matcher.group(0).substring(18).replace(" ",  "")) > 0;
		}
		return false;
	}

	/**
	 * Find a specific field on a received HTML page
	 * 
	 * @param HTMLMessage the message received from the site
	 * @param field the field to search (for instance, "Q", "A", "B"...)
	 * @return the field that was searched, on a single line
	 * 
	 */
	public static String findField(String HTMLMessage, String field){
		Pattern pattern = Pattern.compile("<font class='NOTAM-ENT'>"+field+"[)] </font><font class='NOTAM-CORPS'>[^<>]+<");
		Matcher matcher = pattern.matcher(HTMLMessage);

		String result = "";
		if(matcher.find()) {
			String r = matcher.group(0);
			result = r.substring(60, r.length() - 1).replace("\n", " ").replace(Character.toString(CSVSeparator), ".");
		}

		return result;
	}
}