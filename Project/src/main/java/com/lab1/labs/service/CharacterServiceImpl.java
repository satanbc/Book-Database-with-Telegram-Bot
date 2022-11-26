package com.lab1.labs.service;

import com.lab1.labs.Entities.Character;
import com.lab1.labs.dao.CharacterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CharacterServiceImpl implements CharacterService {

	private CharacterRepository characterRepository;

	@Autowired
	public CharacterServiceImpl(CharacterRepository theCharacterRepository) {
		characterRepository = theCharacterRepository;
	}
	
	@Override
	public List<Character> findAll() {
		return characterRepository.findAll();
	}

	@Override
	public Character findById(int theId) {
		Optional<Character> result = characterRepository.findById(theId);

		Character theCharacter = null;

		if (result.isPresent()) {
			theCharacter = result.get();
		}
		else {
			throw new RuntimeException("Did not find character id - " + theId);
		}

		return theCharacter;
	}

	@Override
	public void save(Character theCharacter) {
		characterRepository.save(theCharacter);
	}

	@Override
	public void deleteById(int theId) {
		characterRepository.deleteById(theId);
	}

}






