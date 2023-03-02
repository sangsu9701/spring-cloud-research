package rf.com.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AggregationBucket {
	
	private String key;
    private long count;
    
}
