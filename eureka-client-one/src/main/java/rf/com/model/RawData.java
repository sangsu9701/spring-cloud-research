package rf.com.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RawData {
	
	private String id;
	private String title;
	private String content;
	private String regDate;
    private Map<String, Object> data;

	public RawData(String id, String title, String content, String regDate) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.regDate = regDate;
	}
	
	public RawData(String id, Map<String, Object> data) {
		this.id = id;
		this.data = data;
	}
}
