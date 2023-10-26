package org.hibernate.bugs;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class BookEntity {

	@Id
	private String id;

	@Column(name = "Type")
	private String type;

	public BookEntity(String id, String type) {
		this.id = id;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
