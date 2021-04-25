package beratufekci.thy.primefaces.entity;

import lombok.Data;

@Data
public class User {
	
	private Long id;
	private String name;
	private String surname;
	
	public User() {}
	
	public User(Long id, String name, String surname) {
		this.id = id;
		this.name = name;
		this.surname = surname;
	}

}
