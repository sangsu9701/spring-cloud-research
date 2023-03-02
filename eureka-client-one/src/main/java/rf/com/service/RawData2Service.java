package rf.com.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import rf.com.model.RawData;

@Service
public class RawData2Service {

	private static final Logger logger = LoggerFactory.getLogger(RawDataService.class);

	@Autowired
	private RestHighLevelClient restHighLevelClient;
    
	public ResponseEntity<?> searchRawData(String regDateRange, String title, String content, String sortField, String sortType, String index) throws IOException {
		// Build the Elasticsearch query based on the query parameters
		SearchRequest searchRequest = new SearchRequest(index != null ? index : "samsunghealth");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
				.query(buildQuery(regDateRange, title, content))
				.sort(sortField, SortOrder.fromString(sortType.split(",")[1])).from(0).size(10);
		searchRequest.source(searchSourceBuilder);

		// Execute the search and return the results
		SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		List<RawData> results = Arrays.stream(searchResponse.getHits().getHits())
				.map(hit -> new RawData(hit.getId(), hit.getSourceAsMap())).collect(Collectors.toList());
		return ResponseEntity.ok(results);
	}

	private QueryBuilder buildQuery(String regDateRange, String title, String content) {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		if (StringUtils.isNotBlank(regDateRange)) {
			String[] dates = regDateRange.split(" TO ");
			queryBuilder.must(QueryBuilders.rangeQuery("md_date").from(dates[0]).to(dates[1]));
		}
		if (StringUtils.isNotBlank(title)) {
			queryBuilder.must(QueryBuilders.matchQuery("md_title", title));
		}
		if (StringUtils.isNotBlank(content)) {
			queryBuilder.must(QueryBuilders.matchQuery("md_content", content));
		}
		return queryBuilder;
	}
}
