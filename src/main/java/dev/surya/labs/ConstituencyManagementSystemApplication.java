package dev.surya.labs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import dev.surya.labs.entity.Constituency;
import dev.surya.labs.repository.ConstituencyRepository;

@SpringBootApplication
@ComponentScan(basePackages = "dev.surya.labs")
public class ConstituencyManagementSystemApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(ConstituencyManagementSystemApplication.class, args);
	}

	@Autowired
	private ConstituencyRepository constituencyRepository;
	
	@Override
	public void run(String... args) throws Exception {
		loadData();
	}
	
	public  void loadData() {
        String csvFile = "C:\\Sudha\\STS4Workspace\\prod0.4-election-results-management-system\\src\\main\\resources\\data.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;

            // Read each line from the CSV file
            while ((line = br.readLine()) != null) {
                // Split the line into an array of values using a comma as the delimiter
                String[] values = line.split(",");
                
                 Constituency record = new Constituency(values[0],values[1], values[2], values[3],values[4], values[5], values[6]);
       		  		constituencyRepository.save(record);
                
            }
            System.out.println("applicaiton initialized successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
