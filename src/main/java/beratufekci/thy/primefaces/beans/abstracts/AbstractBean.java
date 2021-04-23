package beratufekci.thy.primefaces.beans.abstracts;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public class AbstractBean {
	
	public static void showMessage(MessageType severityType ,String message) {
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		if(severityType == MessageType.WARN_MESSAGE) {
			context.addMessage("warnMessage", new FacesMessage(FacesMessage.SEVERITY_WARN,message, null));
		}else if(severityType == MessageType.INFO_MESSAGE) {
			context.addMessage("infoMessage", new FacesMessage(FacesMessage.SEVERITY_INFO,message, null));
		}
		
	}

}
