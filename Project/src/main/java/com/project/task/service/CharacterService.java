package com.project.task.service;

import com.project.task.Entities.Character;

import java.util.List;

public interface CharacterService {

	public List<Character> findAll();
	
	public Character findById(int theId);
	
	public void save(Character theCharacter);
	
	public void deleteById(int theId);
	
}
