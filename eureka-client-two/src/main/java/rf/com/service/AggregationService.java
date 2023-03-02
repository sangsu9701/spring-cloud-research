package rf.com.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rf.com.model.TermCount;
import rf.com.model.TermsAggregationResult;

@Service
public class AggregationService {

	private static final Logger logger = LoggerFactory.getLogger(AggregationService.class);

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	public TermsAggregationResult getAggregationResult(String regDateRange, String title, String content, String sortField, String sortType, String index, String agg_field) throws IOException {
        SearchResponse response = getAggregationResponse(regDateRange, title, content, sortField, sortType, index, agg_field);
        return transformResponse(response, agg_field);
    }

    private SearchResponse getAggregationResponse(String regDateRange, String title, String content, String sortField, String sortType, String index, String agg_field) throws IOException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (StringUtils.isNotBlank(regDateRange)) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("md_date");
            String[] range = regDateRange.split(" TO ");
            if (range.length == 2) {
                String start = range[0];
                String end = range[1];
                if (StringUtils.isNotBlank(start)) {
                    rangeQueryBuilder.gte(start);
                }
                if (StringUtils.isNotBlank(end)) {
                    rangeQueryBuilder.lte(end);
                }
                boolQueryBuilder.must(rangeQueryBuilder);
            }
        }

        if (StringUtils.isNotBlank(title)) {
            MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("md_title", title);
            boolQueryBuilder.must(matchQueryBuilder);
        }

        if (StringUtils.isNotBlank(content)) {
            MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("md_content", content);
            boolQueryBuilder.must(matchQueryBuilder);
        }

        SearchRequest searchRequest = new SearchRequest(index != null ? index : "samsunghealth");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(0);

        if (StringUtils.isNotBlank(agg_field)) {
            TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms(agg_field)
                    .field(agg_field)
                    .size(300)
                    .shardSize(1000);
            searchSourceBuilder.aggregation(termsAggregationBuilder);
        }

        if (StringUtils.isNotBlank(sortField)) {
            searchSourceBuilder.sort(sortField, SortOrder.fromString(sortType.split(",")[1]));
        }

        searchRequest.source(searchSourceBuilder);
        
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        return response;
    }

    private TermsAggregationResult transformResponse(SearchResponse response, String agg_field) {
    	TermsAggregationResult result = new TermsAggregationResult();

        ParsedStringTerms agg = response.getAggregations().get(agg_field);
        List<TermCount> termCounts = new ArrayList<>();
        for (Terms.Bucket bucket : agg.getBuckets()) {
            String key = bucket.getKeyAsString();
            long count = bucket.getDocCount();
            termCounts.add(new TermCount(key, count));
        }
        result.setTermCounts(termCounts);

        return result;
    }
}
