package main.java;

/**
 * This is the IdentifierFormParameters class, implementing the FormParameters interface,
 * used for sending the request for a single NOTAM identifier
 *
 * @author r.jaoui
 * 
 */
public class IdentifierFormParameters implements FormParameters{
	
	private boolean bResultat;
	private String ModeAffichage;
	private String NOTAM_Langue;
	
	String[] identifier;

	/**
	 * The class constructor, that initializes all parameter fields and fills the NOTAM identifier's data
	 * 
	 * @param id the NOTAM identifier to parse into a parameters field
	 */
	public IdentifierFormParameters(String id) {
		initialize();
		identifier = new String[4];
		identifier[0] = id.substring(0, 4);
		identifier[1] = id.substring(5, 6);
		identifier[2] = id.substring(6, 10);
		identifier[3] = id.substring(11, 13);
	}


	/**
	 * The class initializer (called in the constructor or at anytime)
	 * This sets the default values of the parameters
	 * 
	 */
	public void initialize() {
		this.bResultat = true;
		this.ModeAffichage = "RESUME";
		this.NOTAM_Langue = "FR";
	}

	/**
	 * Used to display the IdentifierFormParameters in a concise way
	 * 
	 */
	@Override
	public String toString() {
		return "IdentifierFormParameters :\n\tbResultat : "+this.bResultat+
		   "\n\tModeAffichage : "+this.ModeAffichage+
		   "\n\tNOTAM_Langue : "+NOTAM_Langue+
		   "\n\tNOTAM_Mat_Notam[0][0] : "+identifier[0]+
		   "\n\tNOTAM_Mat_Notam[0][1] : "+identifier[1]+
		   "\n\tNOTAM_Mat_Notam[0][2] : "+identifier[2]+
		   "\n\tNOTAM_Mat_Notam[0][3] : "+identifier[3];
	}

	/**
	 * Used to parse the IdentifierFormParameters data
	 * 
	 */
	public String parse() {
		return "bResultat="+this.bResultat+
		      "&ModeAffichage="+this.ModeAffichage+
		      "&NOTAM_Langue="+NOTAM_Langue+
		      "&NOTAM_Mat_Notam[0][0]="+identifier[0]+
		      "&NOTAM_Mat_Notam[0][1]="+identifier[1]+
		      "&NOTAM_Mat_Notam[0][2]="+identifier[2]+
		      "&NOTAM_Mat_Notam[0][3]="+identifier[3];
	}
}
