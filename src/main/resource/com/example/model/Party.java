package com.example.model;

public class Party {
	
    private Integer id ;
	private String name;
	private Integer voteCount;
   
	
	
	public Integer getid() {
		// TODO Auto-generated method stub
		return id;
	}

	public String getname() {
		// TODO Auto-generated method stub
		return name;
	}

	public Integer getvoteCount() {
		// TODO Auto-generated method stub
		return voteCount;
	}

	
	public void setname(String name) {
		this.name = name;
	}
	

	public void setvoteCount(Integer voteCount) {
		this.voteCount = voteCount;
	}

	public void setid(Integer id) {
		this.id = id;
	}

	
	

}
