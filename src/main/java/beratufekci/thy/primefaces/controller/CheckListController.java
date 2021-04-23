package beratufekci.thy.primefaces.controller;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
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
	
	@ManagedProperty("#{checkService}")
	private CheckService checkService;
	
	private List<Check> checks;
	private List<User> userList = new ArrayList<User>();
	
	private Check check = new Check();
	
	
	@PostConstruct
	public void loadChecks() {
		checks = checkService.findAll();
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
						URL url = new URL("https://mocki.io/v1/a3f09385-42e2-44ee-9d5d-e15428e3490b");
			
						//to get json data by using specified url address
						JSONData = this.getJsonDataFromUrl(url);
							
						//to parse json object
			            this.parseJSONData(JSONData);

						//to show queryList and selected aqd record
			            this.showValues();
								
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
				}
		
	}
	
	public String getJsonDataFromUrl(URL url) {

		//inline will store the JSON data streamed in string format
		String inline = "";
			
		try {
			
			//Parse URL into HttpURLConnection in order to open the connection in order to get the JSON data
			HttpURLConnection conn;
			conn = (HttpURLConnection)url.openConnection();
			
			//Set the request to GET or POST as per the requirements
			conn.setRequestMethod("GET");
			
			//Use the connect method to create the connection bridge
			conn.connect();
			
			//Get the response status of the Rest API
			int responsecode = conn.getResponseCode();
			System.out.println("Response code is: " +responsecode);
				
			//Iterating condition to if response code is not 200 then throw a runtime exception
			//else continue the actual process of getting the JSON data
			if(responsecode != 200) {
				throw new RuntimeException("HttpResponseCode: " +responsecode);
			}else
			 {
				//Scanner functionality will read the JSON data from the stream
				Scanner sc = new Scanner(url.openStream());
				while(sc.hasNext())
				{
					inline+=sc.nextLine();
				}
				System.out.println("\nJSON Response in String format"); 
				System.out.println(inline);
				
				//Close the stream when reading the data has been finished
				sc.close();
			 }
			//Disconnect the HttpURLConnection stream
			conn.disconnect();	
			
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return inline;
	}

	public void parseJSONData(String JSONData) {
		try {
			//to convert string to json object
			JSONParser jsonParser = new JSONParser(); 
			Object obj;
			obj = jsonParser.parse(JSONData);
			JSONObject jo = (JSONObject) obj;
			
			//to get  query list from json response
			JSONArray jsonUserList = (JSONArray) jo.get("users");
			
			//to check length of json array
			if(jsonUserList.size() > 1000) {
				//to not show data table
				this.isValidUserListSize = false;
				
				//to show warn message
				AbstractBean.showMessage(MessageType.WARN_MESSAGE,"We'd prefer to not showing data table due to query list size in case of facing performance problem. You can export to excel and view content of user list.");
			}
			
			//to add json objects in arrayList called users
			jsonUserList.forEach(query -> addToUserList((JSONObject)query));
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addToUserList(JSONObject user) {
		
		User jsonUser = new User();
		
		//to set attribute to use
		jsonUser.setId((Long) user.get("id"));
		jsonUser.setName((String) user.get("name"));
		jsonUser.setSurname((String) user.get("surname"));
		
		//to add user list
		userList.add(jsonUser);
				
		
	}	
	
	public void showValues() {
		//to show userList
		System.out.println("Showing UserList : ");   
		userList.forEach(user -> System.out.println(user.getId() + " " + user.getName() + " " + user.getSurname()));
		
		//to show user list size
		System.out.println("User List size : " + userList.size() + " \n");
	}

}
