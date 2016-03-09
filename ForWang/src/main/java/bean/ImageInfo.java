package bean;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="image_info")
public class ImageInfo {
	/**
	 * 主键增长策略：自动增长
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	@Transient
	private String sid;
	private String url;
	private String title;
	@Transient String stake_time;
	private Date take_time;
	private String location;
	private String content;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getTake_time() {
		return take_time;
	}
	public void setTake_time(Date take_time) {
		this.take_time = take_time;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
		setId(Integer.valueOf(sid));
	}
	public String getStake_time() {
		return stake_time;
	}
	public void setStake_time(String stake_time) {
		this.stake_time = stake_time;
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd");
		try {
			setTake_time(new Date(dateFormat.parse(stake_time).getTime()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
