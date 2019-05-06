package controller;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface NormalRepository extends CrudRepository<NormalWinner, Integer>{
	public List<NormalWinner> findAllByOrderByTimeAsc();
}

