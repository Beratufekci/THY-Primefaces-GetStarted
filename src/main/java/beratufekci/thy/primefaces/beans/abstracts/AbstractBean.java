package beratufekci.thy.primefaces.beans.abstracts;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public class AbstractBean {
	
		public static void showMessage(MessageType messageType, Severity severity ,String message,String detail) {
			
			FacesContext context = FacesContext.getCurrentInstance();
		
			context.addMessage(messageType.toString(), new FacesMessage(severity,message, detail));
	    
	}


}
