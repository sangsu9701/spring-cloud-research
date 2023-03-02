package rf.com.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TermCount {
	private String term;
    private long count;
}
	