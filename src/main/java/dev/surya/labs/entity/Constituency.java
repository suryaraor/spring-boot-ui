package dev.surya.labs.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "home")
public class Constituency {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "first_name", nullable = false)
	private String name;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "lead")
	private String lead;
	
	@Column(name = "won")
	private String won;
	
	@Column(name = "margin")
	private String margin;
	
	@Column(name = "round")
	private String round;
	
	public Constituency() {
		
	}
	
	public Constituency(String id, String name, String lastName, String lead, String won, String margin, String round) {
		super();
		this.id = new Long(id);
		this.name = name;
		this.lastName = lastName;
		this.lead = lead;
		this.won = won;
		this.margin  = margin;
		this.round = round;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLead() {
		return lead;
	}
	public void setLead(String lead) {
		this.lead = lead;
	}

	public String getWon() {
		return won;
	}

	public void setWon(String won) {
		this.won = won;
	}
	
	

	public String getMargin() {
		return margin;
	}

	public void setMargin(String margin) {
		this.margin = margin;
	}

	public String getRound() {
		return round;
	}

	public void setRound(String round) {
		this.round = round;
	}

	public String getMessage() {
		if(won!=null && !won.trim().equals("")) {
			return won+ " Won with " + margin +" margin";
		}else if(lead!=null && !lead.trim().equals("")) {
			return lead+ " Leading with " + margin +" margin at round "+round;
		}else {
			return "TBD";
		}
	}
	
	public String getTrend() {
		if(won!=null && !won.trim().equals("")) {
			return won+ "";
		}else if(lead!=null && !lead.trim().equals("")) {
			return lead+ "";
		}else {
			return "";
		}
	}
}
