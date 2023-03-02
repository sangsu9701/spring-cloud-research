package rf.com.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rf.com.model.RawData;
import rf.com.service.RawData2Service;
import rf.com.service.RawDataService;

@RestController
public class RawDataController {

	@Autowired
	private RawDataService service;

	@Autowired
	private RawData2Service service2;

	@GetMapping("/rawdata")
	public ResponseEntity<?> searchRawData(	@RequestParam(value = "md_date", required = false) String regDateRange,
											@RequestParam(value = "title", required = false) String title,
											@RequestParam(value = "content", required = false) String content,
											@RequestParam(value = "sort", required = false, defaultValue = "md_seq") String sortField,
											@RequestParam(value = "sort_type", required = false, defaultValue = "key,asc") String sortType,
											@RequestParam(value = "index", required = false) String index) throws IOException {

		// Build the Elasticsearch query based on the query parameters
		return service2.searchRawData(regDateRange, title, content, sortField, sortType, index);
	}
	
	@GetMapping("/rawdata2")
	public List<RawData> searchRawData2(	@RequestParam(value = "md_date", required = false) String regDateRange,
											@RequestParam(value = "title", required = false) String title,
											@RequestParam(value = "content", required = false) String content,
											@RequestParam(value = "sort", required = false, defaultValue = "md_seq") String sortField,
											@RequestParam(value = "sort_type", required = false, defaultValue = "_key,asc") String sortType,
											@RequestParam(value = "index", required = false) String index) throws IOException, ParseException {

		// Build the Elasticsearch query based on the query parameters
		return service.getRawData(regDateRange, title, content, sortField, sortType, index);
	}
}