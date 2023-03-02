package rf.com.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rf.com.model.RawData;

@Service
public class RawDataService implements serviceInterface{

	private static final Logger logger = LoggerFactory.getLogger(RawDataService.class);

	@Autowired
	private RestHighLevelClient restHighLevelClient;
    
	@Override
    public List<RawData> getRawData(String regDate, String title, String content, String sort, String sortType, String index) throws ParseException, IOException {
        SearchResponse response = getRawDataResponse(regDate, title, content, sort, sortType, index);
        return transformResponse(response);
    }
    
    private SearchResponse getRawDataResponse(String regDate, String title, String content, String sortField, String sortType, String index) throws ParseException, IOException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        
        if (StringUtils.isNotBlank(regDate)) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("md_date");
            String[] range = regDate.split(" TO ");
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
        if (StringUtils.isNotBlank(sortField)) {
            searchSourceBuilder.sort(sortField, SortOrder.fromString(sortType.split(",")[1]));
        }

        searchRequest.source(searchSourceBuilder);
        
     // Execute the search and return the results
 		SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        return searchResponse;
    }

    private List<RawData> transformResponse(SearchResponse response) throws ParseException, IOException {
        return Arrays.stream(response.getHits().getHits())
        		.map(hit -> {
					Map<String, Object> source = hit.getSourceAsMap();

		            String id = hit.getId();
		            String title = StringUtils.defaultIfBlank((String) source.get("md_title"), "");
		            String content = StringUtils.defaultIfBlank((String) source.get("md_content"), "");
		            String regDate = StringUtils.defaultIfBlank((String) source.get("md_date"), "");

		            RawData rawData = new RawData(id, title, content, regDate);
		            return rawData;
				})
				.collect(Collectors.toList());
    }
}