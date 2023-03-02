package rf.com.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rf.com.model.TermsAggregationResult;
import rf.com.service.AggregationService;

@RestController
public class AggregationController {
	
	@Autowired
	AggregationService aggregationService;

	@GetMapping("/aggregation")
	public ResponseEntity<TermsAggregationResult> getTermsAggregation(
			@RequestParam(value = "md_date", required = false) String regDateRange,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "sort", required = false, defaultValue = "md_seq") String sortField,
			@RequestParam(value = "sort_type", required = false, defaultValue = "key,asc") String sortType,
			@RequestParam(value = "index", required = false) String index, 
            @RequestParam(name = "agg_field", required = true) String agg_field) throws IOException {


        TermsAggregationResult aggregationResult = aggregationService.getAggregationResult(regDateRange, title, content, sortField, sortType, index, agg_field);
        return ResponseEntity.ok(aggregationResult);
    }
}