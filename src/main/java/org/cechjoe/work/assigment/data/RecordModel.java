package org.cechjoe.work.assigment.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static org.cechjoe.work.assigment.data.RecordConstant.*;
import static org.cechjoe.work.assigment.data.RecordUtils.dateToString;
import static org.cechjoe.work.assigment.data.RecordUtils.verifyNode;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RecordModel {
    private String uuid;
    private ObjectNode node;


    public static boolean verifyNewData(JsonNode newData) {
        JsonNode info = newData.path(INFO_FIELD);
        if (!info.isMissingNode()) {
            return !info.path(DATA_INFO).isMissingNode();
        }
        return false;
    }

    public RecordModel(@NotNull JsonNode recordModel) {
        this.node = (ObjectNode) recordModel;
        if (recordModel.path(UUID_FIELD).isMissingNode()) {
            this.uuid = UUID.randomUUID().toString();
            node.put(UUID_FIELD, this.uuid);
        } else {
            this.uuid = recordModel.path(UUID_FIELD).asText();
        }
        JsonNode info = node.path(INFO_FIELD);
        if (info.isMissingNode()) {
            node.putObject(INFO_FIELD);
            ObjectMapper mapper = new ObjectMapper();
            info = mapper.createObjectNode();
            node.set(INFO_FIELD, info);
        }
        setData((ObjectNode) info, STATUS_FIELD, RecordStatus.NEW.toString());
        setData((ObjectNode) info, CREATE_AT_FIELD, dateToString(new Date()));
    }

    private void setData(ObjectNode node, String path, String value) {
        JsonNode subNode = node.path(path);
        if (!subNode.isMissingNode()) {
            node.remove(path);
        }
        node.put(path, value);
    }

    public RecordModel(@NotNull String line) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            node = (ObjectNode) mapper.readTree(line);
            uuid = node.path(UUID_FIELD).asText();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUuid() {
        return uuid;
    }

    public String serializeJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(node);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }



    public JsonNode getJsonNode() {
        return node;
    }


    public boolean isDeleted() {
        return node.path(INFO_FIELD).path(STATUS_FIELD).asText().contains(RecordStatus.DELETED.toString());
    }

    public void setJsonNode(ObjectNode node) {
        this.node = node;
    }
}
