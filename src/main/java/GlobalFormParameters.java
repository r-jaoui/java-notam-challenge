package main.java;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This is the GlobalFormParameters class, implementing the FormParameters interface,
 * used for sending the request to gather NOTAM identifiers
 *
 * @author r.jaoui
 * 
 */
public class GlobalFormParameters implements FormParameters{
	private boolean bResultat;
	private String ModeAffichage;
	private LocalDateTime FIR_Date;
	private String FIR_Langue;
	private int FIR_Duree;
	private int FIR_CM_REGLE;
	private int FIR_CM_GPS;
	private int FIR_CM_INFO_COMP;
	private int FIR_NivMin;
	private int FIR_NivMax;
	private String FIR_Tab_Fir0;


	/**
	 * The class constructor, that initializes all parameter fields
	 * 
	 */
	public GlobalFormParameters() {
		this.initialize();
	}

	/**
	 * Mutator for the FIR_Date field
	 * 
	 * @param date the date FIR_Date will be changed to
	 */
	public void setFIR_Date(LocalDateTime date) {
		this.FIR_Date = date;
	}

	/**
	 * Mutator for the FIR_Duree field
	 * 
	 * @param duree the int FIR_Duree will be changed to
	 */
	public void setFIR_Duree(int duree) {
		this.FIR_Duree = duree;
	}

	/**
	 * Mutator for the FIR_NivMin field
	 * 
	 * @param NivMin the int FIR_NivMin will be changed to
	 */
	public void setFIR_NivMin(int NivMin) {
		this.FIR_NivMin = NivMin;
	}

	/**
	 * Mutator for the FIR_NivMax field
	 * 
	 * @param NivMax the int FIR_NivMax will be changed to
	 */
	public void setFIR_NivMax(int NivMax) {
		this.FIR_NivMax = NivMax;
	}

	/**
	 * The class initializer (called in the constructor or at anytime).
	 * Sets the default values of the parameters
	 * 
	 */
	public void initialize() {
		this.bResultat = true;
		this.ModeAffichage = "RESUME";
		this.FIR_Date = LocalDateTime.now();
		this.FIR_Langue = "FR";
		this.FIR_Duree = 12;
		this.FIR_CM_REGLE = 1;
		this.FIR_CM_GPS = 2;
		this.FIR_CM_INFO_COMP = 2;
		this.FIR_NivMin = 0;
		this.FIR_NivMax = 20;
		this.FIR_Tab_Fir0 = "LFBB";
	}

	/**
	 * Used to display the GlobalFormParameters in a concise way
	 * 
	 */
	@Override
	public String toString() {
		return "GlobalFormParameters :\n\tbResultat : "+this.bResultat+
			  "\n\tModeAffichage : "+this.ModeAffichage+
			  "\n\tFIR_Date_DATE : "+this.FIR_Date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))+
			  "\n\tFIR_Date_HEURE : "+this.FIR_Date.format(DateTimeFormatter.ofPattern("HH:mm"))+
			  "\n\tFIR_Langue : "+this.FIR_Langue+
			  "\n\tFIR_Duree : "+this.FIR_Duree+
			  "\n\tFIR_CM_REGLE : "+this.FIR_CM_REGLE+
			  "\n\tFIR_CM_GPS : "+this.FIR_CM_GPS+
			  "\n\tFIR_CM_INFO_COMP : "+this.FIR_CM_INFO_COMP+
			  "\n\tFIR_NivMin : "+this.FIR_NivMin+
			  "\n\tFIR_NivMax : "+this.FIR_NivMax+
			  "\n\tFIR_Tab_Fir[0] : "+this.FIR_Tab_Fir0;
	}


	/**
	 * Used to parse the GlobalFormParameters data
	 * 
	 */
	public String parse() {
		return "bResultat="+this.bResultat+
			  "&ModeAffichage="+this.ModeAffichage+
			  "&FIR_Date_DATE="+this.FIR_Date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))+
			  "&FIR_Date_HEURE="+this.FIR_Date.format(DateTimeFormatter.ofPattern("HH:mm"))+
			  "&FIR_Langue="+this.FIR_Langue+
			  "&FIR_Duree="+this.FIR_Duree+
			  "&FIR_CM_REGLE="+this.FIR_CM_REGLE+
			  "&FIR_CM_GPS="+this.FIR_CM_GPS+
			  "&FIR_CM_INFO_COMP="+this.FIR_CM_INFO_COMP+
			  "&FIR_NivMin="+this.FIR_NivMin+
			  "&FIR_NivMax="+this.FIR_NivMax+
			  "&FIR_Tab_Fir[0]="+this.FIR_Tab_Fir0;
	}
}
