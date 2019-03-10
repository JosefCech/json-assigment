package org.cechjoe.work.assigment.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonNodeHelper {

    public static  JsonNode GetPostRequestJsonNode() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode model = mapper.createObjectNode();
        ObjectNode info = mapper.createObjectNode();
        info.put("recordData", "test123");
        model.putObject("info");
        model.set("info",info);
        return  model;
    }

}
