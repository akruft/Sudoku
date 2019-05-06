package controller;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface DifficultRepository extends CrudRepository<DifficultWinner, Integer>{
	public List<DifficultWinner> findAllByOrderByTimeAsc();
}

