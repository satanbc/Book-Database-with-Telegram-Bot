package com.lab1.labs.service;

import com.lab1.labs.Entities.Character;

import java.util.List;

public interface CharacterService {

	public List<Character> findAll();
	
	public Character findById(int theId);
	
	public void save(Character theCharacter);
	
	public void deleteById(int theId);
	
}
