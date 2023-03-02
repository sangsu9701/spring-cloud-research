package rf.com.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import rf.com.model.RawData;

public interface serviceInterface {
	public List<RawData> getRawData(String regDate, String title, String content, String sort, String sortType, String index) throws ParseException, IOException;
}
