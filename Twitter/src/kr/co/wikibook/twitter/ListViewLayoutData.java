package kr.co.wikibook.twitter;

public class ListViewLayoutData {
	
	private long id = 0;
	private String friend_name = "";
	private String screen_name = "";
	private String twit_body = "";
	private String twit_image_url = "";
	
	public ListViewLayoutData() {
		
	}

	public ListViewLayoutData(long id, String friend_name, String screen_name, String twit_body, String twit_image_url) {
		this.id = id;
		this.friend_name = friend_name;
		this.screen_name = screen_name;
		this.twit_body = twit_body;
		this.twit_image_url = twit_image_url;
	}
	
	public long getId() {
		return id;
	}
	
	public String getFriendName() {
		return friend_name;
	}
	
	public String getScreenName() {
		return screen_name;
	}
	
	public String getTwitBody() {
		return twit_body;
	}
	
	public String getTwitImageUrl() {
		return twit_image_url;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setFriendName(String friend_name) {
		this.friend_name = friend_name;
	}
	
	public void setScreenName(String screen_name) {
		this.screen_name = screen_name;
	}
	
	public void setTwitBody(String twit_body) {
		this.twit_body = twit_body;
	}
	
	public void setTwitImageUrl(String twit_image_url) {
		this.twit_image_url = twit_image_url;
	}
}
