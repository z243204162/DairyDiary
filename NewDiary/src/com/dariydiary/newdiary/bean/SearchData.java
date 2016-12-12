package com.dariydiary.newdiary.bean;

import java.util.List;


public class SearchData {
	private List<SearchResult> items;

	public List<SearchResult> getItems() {
		return items;
	}

	public void setItems(List<SearchResult> items) {
		this.items = items;
	}
	public class SearchResult{
		private String link;

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}
		
	}
}