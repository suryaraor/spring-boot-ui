package dev.surya.labs.service.impl;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.surya.labs.entity.Constituency;
import dev.surya.labs.repository.ConstituencyRepository;
import dev.surya.labs.service.ConstituencyService;

@Service
public class ConstituencyServiceImpl implements ConstituencyService{

	private ConstituencyRepository constituencyRepository;
	
	@Autowired
	private GoogleSheetUpdater googleSheetUpdater;
	
	public ConstituencyServiceImpl(ConstituencyRepository constituencyRepository) {
		super();
		this.constituencyRepository = constituencyRepository;
	}

	@Override
	public List<Constituency> getAllConstituencies() {
		List<Constituency> list =  constituencyRepository.findAll();
		list.sort(Comparator.comparing(Constituency::getTrend).thenComparing(Constituency::getLastName).thenComparing(Constituency::getName));
		return list;
	}

	@Override
	public Constituency saveConstituency(Constituency constituency) {
		return constituencyRepository.save(constituency);
	}

	@Override
	public Constituency getConstituencyById(Long id) {
		return constituencyRepository.findById(id).get();
	}

	@Override
	public Constituency updateConstituency(Constituency constituency) {
		googleSheetUpdater.updateParty(constituency);
		return constituencyRepository.save(constituency);
	}
	
	@Override
	public Constituency lead(Constituency constituency) {
		googleSheetUpdater.lead(constituency);
		return constituencyRepository.save(constituency);
	}
	
	@Override
	public Constituency won(Constituency constituency) {
		googleSheetUpdater.won(constituency);
		return constituencyRepository.save(constituency);
	}

	@Override
	public void deleteConstituencyById(Long id) {
		constituencyRepository.deleteById(id);	
	}

}
