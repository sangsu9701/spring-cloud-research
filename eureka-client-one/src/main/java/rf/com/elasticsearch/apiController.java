package rf.com.elasticsearch;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class apiController {

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    ResponseEntity<Map<String, String>> sample() {
        ResponseEntity<Map<String, String>> response = null;

        Map<String, String> resMap = new HashMap<>();
        resMap.put("type", "First eureka client!");
        resMap.put("message", "Spring Cloud is awesome!");

        response = new ResponseEntity<>(resMap, HttpStatus.OK);

        return response;
    }

}
