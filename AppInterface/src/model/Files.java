package model;


import java.time.LocalDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class for Files
 * @author Thomas Quarshie & Richard Quayson
 */
public class Files {
	private final StringProperty filename;
	private final StringProperty type;
	private final StringProperty filepath;
	private final ObjectProperty<LocalDateTime> created;
	private final ObjectProperty<LocalDateTime> modified;
	private final SimpleFloatProperty size;
	

	/**
	 * Default constructor
	 */
	public Files() {
		this(null, null);
	}
	
	/**
	 * Constructor with some initial data.
	 * 
	 * @param firstName
	 * @param lastName
	 */
	public Files(String filename, String type) {
		this.filename = new SimpleStringProperty(filename);
		this.type = new SimpleStringProperty(type);
		this.filepath = new SimpleStringProperty("../documents/tim.txt");
        this.created = new SimpleObjectProperty<LocalDateTime>(LocalDateTime.now()); 
        this.modified = new SimpleObjectProperty<LocalDateTime>(LocalDateTime.now());
		this.size = new SimpleFloatProperty(25.4f);
		
	}
	
	public String getFilename() {
		return filename.get();
	}

	public void setFilename(String filename) {
		this.filename.set(filename);
	}
	
	public StringProperty filenameProperty() {
		return filename;
	}
	
	public String gettype() {
		return type.get();
	}

	public void settype(String type) {
		this.type.set(type);
	}
	
	public StringProperty typeProperty() {
		return type;
	}
	
	
	public String getfilepath() {
		return filepath.get();
	}

	public void setfilepath(String filepath) {
		this.filepath.set(filepath);
	}
	
	public StringProperty filepathProperty() {
		return filepath;
	}
	
	
	
	public LocalDateTime getCreated() {
		return created.get();
	}

	public void setCreated(LocalDateTime created) {
		this.created.set(created);
	}
	
	public ObjectProperty<LocalDateTime> createdProperty() {
		return created;
	}
	
	
	public LocalDateTime getModified() {
		return modified.get();
	}

	public void setModified(LocalDateTime modified) {
		this.modified.set(modified);
	}
	
	public ObjectProperty<LocalDateTime> ModifiedProperty() {
		return modified;
	}
	
	
	
	public float getsize() {
		return size.get();
	}

	public void setsize(float size) {
		this.size.set(size);
	}
	
	public SimpleFloatProperty sizeProperty() {
		return size;
	}
	
	
	
	



}
