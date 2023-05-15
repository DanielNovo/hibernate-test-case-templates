package org.hibernate.bugs;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class MyEntity {

	@Id
	private String id;
	
	@Lob
	@Column(name = "Photo")
	private byte[] photo;
	
	public MyEntity(String id, byte[] photo) {
		this.id = id;
		this.photo = photo;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public byte[] getPhoto() {
		return photo;
	}
	
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	
	
}
