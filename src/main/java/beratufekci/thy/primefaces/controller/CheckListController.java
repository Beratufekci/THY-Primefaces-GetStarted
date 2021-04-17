package beratufekci.thy.primefaces.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
	
	@ManagedProperty("#{checkService}")
	private CheckService checkService;
	
	private List<Check> checks;
	private List<User> users = new ArrayList<User>();
	
	private Check check = new Check();
	
	
	@PostConstruct
	public void loadChecks() {
		checks = checkService.findAll();
	}
	
	public void save() {
		checkService.save(check);
		check = new Check();
		checks = checkService.findAll();
		FacesContext.getCurrentInstance().addMessage
			(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Check saved!", null));
	}
	
	public void remove(Check check) {
		checkService.remove(check);
		checks = checkService.findAll();
		FacesContext.getCurrentInstance().addMessage
			(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Check removed!", null));
	}
	
	public void clear() {
		check = new Check();
	}
	
	public String login() {
		FacesContext.getCurrentInstance().addMessage
		(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "User logined!", null));
		
		return "";
    }	
	
	public void getDataFromJSONFiles(){
			
		try {
			
			users.clear();
			
			JSONParser jsonParser = new JSONParser();
			FileReader reader = new FileReader("C:\\Users\\pc\\Desktop\\THY\\Primefaces\\Demo\\src\\main\\resources//jsonFiles//THY-Users.json");
			Object obj = jsonParser.parse(reader);
			JSONObject jo =(JSONObject) obj;
			JSONArray userList = (JSONArray) jo.get("THYuser");
			
			userList.forEach(user -> parseUserObject((JSONObject)user));
			
			
			System.out.println("Showing userList : ");
			users.forEach(userfe -> System.out.println(userfe.getName() + " " + userfe.getSurname()));
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} catch(ParseException e) {
			e.printStackTrace();
		}
		
	}

	public void parseUserObject(JSONObject user) {
		
		User jsonUser = new User();
		
		jsonUser.setName((String) user.get("name"));
		jsonUser.setSurname((String) user.get("surname"));
		
		users.add(jsonUser);
				
		
	}	

}
