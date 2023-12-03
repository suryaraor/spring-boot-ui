package dev.surya.labs.service;

import java.util.List;

import dev.surya.labs.entity.Constituency;

public interface ConstituencyService {
	List<Constituency> getAllConstituencies();
	
	Constituency saveConstituency(Constituency constituency);
	
	Constituency getConstituencyById(Long id);
	
	Constituency updateConstituency(Constituency constituency);
	Constituency lead(Constituency constituency);
	Constituency won(Constituency constituency);
	
	void deleteConstituencyById(Long id);
}
