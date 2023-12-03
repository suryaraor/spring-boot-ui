package dev.surya.labs.controller;

import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import dev.surya.labs.entity.Constituency;
import dev.surya.labs.service.ConstituencyService;
import jakarta.servlet.http.HttpSession;

@Controller
public class ConstituencyController {

	private ConstituencyService constituencyService;

	public ConstituencyController(ConstituencyService constituencyService) {
		super();
		this.constituencyService = constituencyService;
	}

	// handler method to handle list home and return mode and view
	@GetMapping("/home")
	public String listConstituencies(Model model) {
		List<Constituency> list = constituencyService.getAllConstituencies();
		model.addAttribute("home", list);
		return "home";
	}

	// handler method to handle list home and return mode and view
	@GetMapping("/results")
	public String results(HttpSession session, Model model) {
		List<Constituency> list = constituencyService.getAllConstituencies();
		model.addAttribute("home", list);

		Map<String, List<Constituency>> groupedByAge = list.stream()
				.collect(Collectors.groupingBy(Constituency::getTrend));

		// Sort the map by trend in descending order
		Map<String, List<Constituency>> sortedByTrendDescending = groupedByAge.entrySet().stream()
				.sorted(Map.Entry.<String, List<Constituency>>comparingByKey().reversed())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		for (String key : sortedByTrendDescending.keySet()) {
			if (!key.equals("")) {
				DecimalFormat decimalFormat = new DecimalFormat("#.#");
				int tbd=0;
				if(groupedByAge.get("")!=null) {
					tbd = groupedByAge.get("").size();
				}
				int count= groupedByAge.get(key).size();
				Double number = 100 * (((groupedByAge.get(key).size() * 1.0) / (list.size() - tbd)));
				// Format the number
				String formattedNumber = decimalFormat.format(number);
				if (key.equalsIgnoreCase("INC") || key.equalsIgnoreCase("MIM")) {
					model.addAttribute(key + "Percent", "width: " + formattedNumber + "%;color: white");
				} else {
					model.addAttribute(key + "Percent", "width: " + formattedNumber + "%;");
				}
				if(number <= 5.0) {
					model.addAttribute(key + "Label", key );
				} else if(number <= 6.0) {
					model.addAttribute(key + "Label", key + " " +count);
				}
				else {
					model.addAttribute(key + "Label", key + " " +count+" ("+ formattedNumber + "%)");
				}
				
			}

			// session.setAttribute("messages", new ArrayList<>());
		}
		List<String> messages = (List) session.getAttribute("messages");
		model.addAttribute("messages", messages);
		session.setAttribute("messages", new ArrayList<>());
		String dateTime = (String)session.getAttribute("lastUpdated");
		model.addAttribute("lastUpdated", dateTime);
		return "results";
	}

	@GetMapping("/home/new")
	public String createConstituencyForm(Model model) {

		// create constituency object to hold constituency form data
		Constituency constituency = new Constituency();
		model.addAttribute("constituency", constituency);
		return "create_constituency";

	}

	@PostMapping("/home")
	public String saveConstituency(@ModelAttribute("constituency") Constituency constituency) {
		constituencyService.saveConstituency(constituency);
		return "redirect:/home";
	}

	@GetMapping("/home/lead/{id}/{party}")
	public String lead(HttpSession session, Model model, @PathVariable Long id, @PathVariable String party) {
		// get constituency from database by id
		Constituency existingConstituency = constituencyService.getConstituencyById(id);
		existingConstituency.setId(id);
		existingConstituency.setLead(party);
		existingConstituency.setWon("");

		// save updated constituency object
		constituencyService.lead(existingConstituency);
		List<String> messages = (List) session.getAttribute("messages");
		if (messages == null || messages.isEmpty()) {
			messages = new ArrayList<>();

		}
		
		String message = formatParty(party) + " has won at " + existingConstituency.getName();
		if(!message.contains("OTH")) {
			messages.add(message);
		}
		session.setAttribute("messages", messages);
		session.setAttribute("lastUpdated", currentTime());
		return "redirect:/home";
	}

	@GetMapping("/home/won/{id}/{party}")
	public String won(HttpSession session, @PathVariable Long id, @PathVariable String party) {
		// get constituency from database by id
		Constituency existingConstituency = constituencyService.getConstituencyById(id);
		existingConstituency.setId(id);
		existingConstituency.setWon(party);
		existingConstituency.setLead("");
		// save updated constituency object
		constituencyService.won(existingConstituency);
		List<String> messages = (List) session.getAttribute("messages");
		if (messages == null || messages.isEmpty()) {
			messages = new ArrayList<>();

		}
		String message = formatParty(party) + " has won at " + existingConstituency.getName();
		if(!message.contains("OTH")) {
			messages.add(message);
		}
		
		session.setAttribute("messages", messages);
		
		session.setAttribute("lastUpdated", currentTime());
		return "redirect:/home";
	}

	private String formatParty(String party) {
		String response = party;
		switch(party) {
		case "BRS": return "BRS";
		case "INC": return "Congress";
		case "BJP": return "BJP";
		case "MIM": return "MIM";
		case "OTH": return "OTH";
		
		}
		return response;
	}

	private String currentTime() {
		// Get the current date and time with the system's default time zone
        ZonedDateTime currentDateTime = ZonedDateTime.now();

        // Specify the desired time zone (Asia/Kolkata for India)
        TimeZone indiaTimeZone = TimeZone.getTimeZone("Asia/Kolkata");

        // Convert the ZonedDateTime to the specified time zone
        ZonedDateTime currentDateTimeInIndia = currentDateTime.withZoneSameInstant(indiaTimeZone.toZoneId());

        // Define the desired date-time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm:ss a z");

        // Format the current date and time in the specified time zone
        String formattedDateTime = currentDateTimeInIndia.format(formatter);

        // Print the formatted date, time, and time zone
        return "Latest results: "+formattedDateTime;
	}

	@PostMapping("/home/{id}")
	public String updateConstituency(@PathVariable Long id, @ModelAttribute("constituency") Constituency constituency,
			Model model) {

		// get constituency from database by id
		Constituency existingConstituency = constituencyService.getConstituencyById(id);
		existingConstituency.setId(id);
		existingConstituency.setName(constituency.getName());
		existingConstituency.setLastName(constituency.getLastName());
		existingConstituency.setLead(constituency.getLead());

		// save updated constituency object
		constituencyService.updateConstituency(existingConstituency);

		return "redirect:/home";
	}

	// handler method to handle delete constituency request

	@GetMapping("/home/{id}")
	public String deleteConstituency(@PathVariable Long id) {
		constituencyService.deleteConstituencyById(id);
		return "redirect:/home";
	}

}
