package beratufekci.thy.primefaces.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import beratufekci.thy.primefaces.entity.Check;
import beratufekci.thy.primefaces.repository.CheckRepository;

@Service
public class InitDbService {

	@Autowired
	private CheckRepository checkRepository;

	@PostConstruct
	public void init() {
		System.out.println("*** INIT DATABASE START ***");
		{
			Check check = new Check();
			check.setName("LinkedIn");
			check.setUrl("https://www.linkedin.com/in/berat-tufekci-097b0511b/");
			checkRepository.save(check);
		}
		{
			Check check = new Check();
			check.setName("Github");
			check.setUrl("https://github.com/Beratufekci");
			checkRepository.save(check);
		}
		System.out.println("*** INIT DATABASE FINISH ***");
	}
}
