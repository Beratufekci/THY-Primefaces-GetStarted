package beratufekci.thy.primefaces.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import beratufekci.thy.primefaces.beans.abstracts.AbstractBean;
import beratufekci.thy.primefaces.beans.abstracts.MessageType;
import beratufekci.thy.primefaces.entity.Check;
import beratufekci.thy.primefaces.entity.User;
import beratufekci.thy.primefaces.service.CheckService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ManagedBean(name = "checkListController")
@ViewScoped
public class CheckListController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String password;
	
	private Check check = new Check();
	private List<Check> checks;
	
	@ManagedProperty("#{checkService}")
	private CheckService checkService;
	
	
	
    private String countryGroup;
    private List<SelectItem> countriesGroup;
	
	private Date startDate = new Date();
	private Date endDate = new Date();
	
	MessageType infoMessage = MessageType.INFO_MESSAGE;
	MessageType warnMessage = MessageType.WARN_MESSAGE;
	
	private final int MAX_SHOWABLE_LIST_SIZE = 1000;
	
	private Boolean isUserTableRendered = true;
	private Boolean isExportToExcelButtonRendered = false;
	
	private List<User> userList = new ArrayList<User>();
	
	
	
	@PostConstruct
	public void loadChecks() {
		checks = checkService.findAll();
		
		countriesGroup = new ArrayList<>();

	    SelectItemGroup europeCountries = new SelectItemGroup("Europe Countries");
	    europeCountries.setSelectItems(new SelectItem[]{
	            new SelectItem("Germany", "Germany"),
	            new SelectItem("Turkey", "Turkey"),
	            new SelectItem("Spain", "Spain")
	    });

	    SelectItemGroup americaCountries = new SelectItemGroup("America Countries");
	    americaCountries.setSelectItems(new SelectItem[]{
	            new SelectItem("United States", "United States"),
	            new SelectItem("Brazil", "Brazil"),
	            new SelectItem("Mexico", "Mexico")
	    });

	    countriesGroup.add(europeCountries);
	    countriesGroup.add(americaCountries);
	}
	
	public void save() {
		checkService.save(check);
		check = new Check();
		checks = checkService.findAll();
		AbstractBean.showMessage(infoMessage,FacesMessage.SEVERITY_INFO, "Check saved!","Check kaydedildi");
	}
	
	public void remove(Check check) {
		checkService.remove(check);
		checks = checkService.findAll();
		AbstractBean.showMessage(infoMessage,FacesMessage.SEVERITY_INFO, "Check removed!","Check silindi");
	}
	
	public void clear() {
		check = new Check();
	}
	
	public String login() {
		AbstractBean.showMessage(infoMessage,FacesMessage.SEVERITY_INFO, "User logined!","Kullanýcý giris yaptý.");
		
		return "";
    }
	
	
	
	
	
	/**
	*Created by Berat Tufekci 
	*<h1>Method Info:</h1>
	*this method will be invoked when user click the show user list button which is located in index.xhtml file
	*basically this method retrieve json data from rest api, then parse it and add queries to show in  user list which is located in index.xhtml file
	*@since 03.06.2021
	*@param Nothing
	*@return Nothing
	*@exception IOException
	*/
	public void showUserTable() {	
		 		avoidDublicatedValues();
				getInitializedPageBack();
				
				if(isSuitableDate()) {	
					try {
						URL url = new URL("http://localhost:3000/users?name=name1");
						parseJSONData(getJsonDataFromRestAPI(url));
							
					} catch (IOException e) {
						e.printStackTrace();
					}	
				}
	}
	
	private void avoidDublicatedValues() {
		userList.clear();	
	}

	private void getInitializedPageBack() {
		this.isUserTableRendered = true;
		this.isExportToExcelButtonRendered = false;		
	}

	private Boolean isSuitableDate() {
		
		if(startDate.after(endDate)) {
			AbstractBean.showMessage(infoMessage,FacesMessage.SEVERITY_INFO,"Start date must be earlier than End date","Baslangýc tarihi bitis tarihinden once olmalýdýr");
			return false;
		}
		return true;	
	}

	private String getJsonDataFromRestAPI(URL url) {

		StringBuilder jsonData = new StringBuilder();
			
		try {
			
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			
			conn.setRequestMethod("GET");
			conn.connect();		
			
			if(conn.getResponseCode() != 200) {
				throw new RuntimeException("HttpResponseCode: " +conn.getResponseCode());
			}else
			 {
			    BufferedReader in = null;
			    if (conn.getHeaderField("Content-Encoding") != null
			             && conn.getHeaderField("Content-Encoding").equals("gzip")) {
			    in = new BufferedReader(new InputStreamReader(new GZIPInputStream(conn.getInputStream())));
			    } else {
			      in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			    }
			    String inputLine;

			    while ((inputLine = in.readLine()) != null) {
			    	jsonData.append(inputLine);
			    }
			    in.close();
			 }		
			conn.disconnect();	
			
		}  catch (IOException e) {
			e.printStackTrace();
		}
		
		return jsonData.toString();
	}

	private void parseJSONData(String jsonData) {
		
			try {
				Object obj =new JSONParser().parse(jsonData);
				JSONArray jsonUsers = (JSONArray) obj;
				
				if(!isJsonUsersEmpty(jsonUsers)) {
					this.isExportToExcelButtonRendered = true;
					isSuitableTableSize(jsonUsers);	
				}
						
				jsonUsers.forEach(user -> addToUserList((JSONObject)user));
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		
	}
	
	private void addToUserList(JSONObject user) {
		Long id = (Long) user.get("id");
		String name = (String) user.get("name");
		String surname =  (String) user.get("surname");
		
		User jsonUser = new User(id, name, surname);
			
		userList.add(jsonUser);		
	}	
	
	private void isSuitableTableSize(JSONArray jsonUsers) {
		if(jsonUsers.size() > this.MAX_SHOWABLE_LIST_SIZE) {
			hideUserTable("Please export to excel to show the result","(Sorgu sonucunu gormek icin exceli cýkartýn)");
		}	
	}

	private boolean isJsonUsersEmpty(JSONArray jsonUsers) {
		if (jsonUsers.isEmpty()) {
			hideUserTable("User table is empty.", "(Aradýgýnýz kriterlere uygun kullanýcý bulunamamýstýr.)");
			return true;
		}
		return false;
	}

	private void hideUserTable(String message, String detail) {
		this.isUserTableRendered = false;
		AbstractBean.showMessage(warnMessage,FacesMessage.SEVERITY_WARN, message, detail);
	}


}
