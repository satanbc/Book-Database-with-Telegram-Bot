package com.lab1.labs.service;

import com.lab1.labs.Entities.Series;

import java.util.List;

public interface SeriesService {

	public List<Series> findAll();
	
	public Series findById(int theId);
	
	public void save(Series theSeries);
	
	public void deleteById(int theId);
	
}
