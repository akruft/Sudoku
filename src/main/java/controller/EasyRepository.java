package controller;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface EasyRepository extends CrudRepository<EasyWinner, Integer>{
	public List<EasyWinner> findAllByOrderByTimeAsc();
}

