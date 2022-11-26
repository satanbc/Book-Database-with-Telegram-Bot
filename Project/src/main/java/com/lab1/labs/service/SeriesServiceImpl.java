package com.lab1.labs.service;

import com.lab1.labs.Entities.Series;
import com.lab1.labs.dao.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeriesServiceImpl implements SeriesService {

	private SeriesRepository seriesRepository;
	
	@Autowired
	public SeriesServiceImpl(SeriesRepository theSeriesRepository) {
		seriesRepository = theSeriesRepository;
	}
	
	@Override
	public List<Series> findAll() {
		return seriesRepository.findAll();
	}

	@Override
	public Series findById(int theId) {
		Optional<Series> result = seriesRepository.findById(theId);

		Series theSeries = null;

		if (result.isPresent()) {
			theSeries = result.get();
		}
		else {
			throw new RuntimeException("Did not find series id - " + theId);
		}

		return theSeries;
	}

	@Override
	public void save(Series theSeries) {
		seriesRepository.save(theSeries);
	}

	@Override
	public void deleteById(int theId) {
		seriesRepository.deleteById(theId);
	}

}






