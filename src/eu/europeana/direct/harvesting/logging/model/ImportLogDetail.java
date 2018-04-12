package eu.europeana.direct.harvesting.logging.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "importlogdetail")
@SequenceGenerator(name="importlogdetail_id_seq",sequenceName="importlogdetail_id_seq", allocationSize=1)
@NamedQueries({
@NamedQuery(name = "ImportLogDetail.loadAll", query = "SELECT i FROM ImportLogDetail i")	
})
public class ImportLogDetail implements Serializable{

	private int Id;
	private int LogId;
	private String status;
	private String message;
	private String payload;
	
	public ImportLogDetail(){}
	
	public ImportLogDetail(int id, int logId, String status, String message, String payload) {
		super();
		Id = id;
		LogId = logId;
		this.status = status;
		this.message = message;
		this.payload = payload;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="importlogdetail_id_seq")
	@Column(name="id")
	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getLogId() {
		return LogId;
	}

	public void setLogId(int logId) {
		LogId = logId;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	
}

