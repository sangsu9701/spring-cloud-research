package rf.com.model;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TermsAggregationResult {
	private String name;
    private List<TermCount> termCounts;

    public static TermsAggregationResult fromParsedStringTerms(String name, ParsedStringTerms terms) {
        List<TermCount> termCounts = new ArrayList<>();
        for (Terms.Bucket bucket : terms.getBuckets()) {
            termCounts.add(new TermCount(bucket.getKeyAsString(), bucket.getDocCount()));
        }
        return new TermsAggregationResult(name, termCounts);
    }
}
