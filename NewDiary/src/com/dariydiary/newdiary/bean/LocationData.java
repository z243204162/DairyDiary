package com.dariydiary.newdiary.bean;

import java.util.List;



public class LocationData {
	private List<LocationResults> results;

	public List<LocationResults> getResults() {
		return results;
	}

	public void setResults(List<LocationResults> results) {
		this.results = results;
	}
	public class LocationResults {
		private String name;
		private List<LocationPhotos> photos;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<LocationPhotos> getPhotos() {
			return photos;
		}
		public void setPhotos(List<LocationPhotos> photos) {
			this.photos = photos;
		}	
	}
	public class LocationPhotos {
		private String photo_reference;

		public String getPhoto_reference() {
			return photo_reference;
		}

		public void setPhoto_reference(String photo_reference) {
			this.photo_reference = photo_reference;
		}
	}
}
