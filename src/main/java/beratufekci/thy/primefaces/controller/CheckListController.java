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
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import javax.annotation.PostConstruct;
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
	
	private Date startDate = new Date();
	private Date endDate = new Date();
	
	private Boolean isValidUserListSize = true;
	
    private String countryGroup;
    private List<SelectItem> countriesGroup;
	
	@ManagedProperty("#{checkService}")
	private CheckService checkService;
	
	private List<Check> checks;
	private List<User> userList = new ArrayList<User>();
	
	private Check check = new Check();
	
	
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
		AbstractBean.showMessage(MessageType.INFO_MESSAGE, "Check saved!");
	}
	
	public void remove(Check check) {
		checkService.remove(check);
		checks = checkService.findAll();
		AbstractBean.showMessage(MessageType.INFO_MESSAGE, "Check removed!");
	}
	
	public void clear() {
		check = new Check();
	}
	
	public String login() {
		AbstractBean.showMessage(MessageType.INFO_MESSAGE, "User logined!");
		
		return "";
    }
	
	
	
	//to show user list
	public void showUserList(){
		
				Long startTime = System.currentTimeMillis();
			
		 		//to clean the data table and avoid dublicate value for list called queries
				userList.clear();
				
				//to show initiliazed page back
				this.isValidUserListSize = true;
				
				if(startDate.after(endDate)) {		
					//to show info message
					AbstractBean.showMessage(MessageType.INFO_MESSAGE,"Start date must be earlier than End date");   
				}else {		
					//inline will store the JSON data streamed in string format
					String JSONData = "";
						
					try {
						//the url address which json data is stored in 
						URL url = new URL("http://localhost:3000/users");
			
						//to get json data by using specified url address
						JSONData = this.getJsonDataFromUrl(url);
							
						//to parse json object
			            this.parseJSONData(JSONData);

						//to show queryList and selected aqd record
			            this.showValues();
			            
			            Long endTime = System.currentTimeMillis();
			            
			            System.out.println("Total time: " + (0.001*(endTime - startTime)) + " seconds");
								
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
				}
		
	}
	
	public String getJsonDataFromUrl(URL url) {

		//inline will store the JSON data streamed in string format
		StringBuilder sb = new StringBuilder();
			
		try {
			
			//Parse URL into HttpURLConnection in order to open the connection in order to get the JSON data
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			
			//Set the request to GET or POST as per the requirements
			conn.setRequestMethod("GET");
			
			//Use the connect method to create the connection bridge
			conn.connect();		
			//Iterating condition to if response code is not 200 then throw a runtime exception
			//else continue the actual process of getting the JSON data
			
			if(conn.getResponseCode() != 200) {
				throw new RuntimeException("HttpResponseCode: " +conn.getResponseCode());
			}else
			 {
				
				long startTime = System.currentTimeMillis();
				
			    BufferedReader in = null;
			    if (conn.getHeaderField("Content-Encoding") != null
			             && conn.getHeaderField("Content-Encoding").equals("gzip")) {
			    in = new BufferedReader(new InputStreamReader(new GZIPInputStream(conn.getInputStream())));
			    } else {
			      in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			    }
			    String inputLine;

			    while ((inputLine = in.readLine()) != null) {
			    	sb.append(inputLine);
			    }
			    in.close();
			    
				long endTime = System.currentTimeMillis();
				
				System.out.println("it lasted: " + (0.001 * (endTime - startTime)) + " seconds");
			 }		
				
			//Disconnect the HttpURLConnection stream
			conn.disconnect();	
			
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb.toString();
	}

	public void parseJSONData(String JSONData) {
		
			try {
				//to convert string to json object
				Object obj =new JSONParser().parse(JSONData);
				
				//to get  query list from json response
				JSONArray jsonUserList = (JSONArray) obj;
				
				//to check length of json array
				if(jsonUserList.size()> 1000) {
					//to not show data table
					this.isValidUserListSize = false;
					
					//to show warn message
					AbstractBean.showMessage(MessageType.WARN_MESSAGE,"We'd prefer not to show data table due to query list size in case of facing performance problem. You can export to excel and view content of user list.");
				}
				
				//to add json objects in arrayList called users
				jsonUserList.forEach(user -> addToUserList((JSONObject)user));
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
	}
	
	public void addToUserList(JSONObject user) {
		//to set values before user constructor
		Long id = (Long) user.get("id");
		String name = (String) user.get("name");
		String surname =  (String) user.get("surname");
		
		//to generate user instance
		User jsonUser = new User(id, name, surname);
			
		//to add user list
		userList.add(jsonUser);				
	}	
	
	public void showValues() {
		//to show user list size
		System.out.println("User List size : " + userList.size());
	}

}
