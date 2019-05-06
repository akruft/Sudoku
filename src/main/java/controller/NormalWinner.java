package controller;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
public class NormalWinner {

    private Integer time;

    @Column(columnDefinition="varchar(150)")
    private String name;

    @Column(columnDefinition="varchar(1)")
    private String difficulty;
    
    public Integer getWinnerId() {
		return winnerId;
	}

	public void setWinnerId(Integer winnerId) {
		this.winnerId = winnerId;
	}

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer winnerId;

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	public String toString() {
		String s= "<tr><td>" + name + "<//td><td>" + time + " seconds" + "<//td><//tr>";
		return s;
	}


}