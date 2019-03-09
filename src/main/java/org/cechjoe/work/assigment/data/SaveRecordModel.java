package org.cechjoe.work.assigment.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SaveRecordModel {
    private String uuid;
    private SimpleDateFormat rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private ObjectNode node;

    private String UUID_FIELD = "recordId";
    private String INFO_FIELD = "info";
    private String DATA_INFO = "data";
    private String STATUS_FIELD = "recordStatus";
    private String CREATE_AT_FIELD = "createdAt";
    private String DELETED_AT_FIELD = "deletedAt";
    private String UPDATE_AT_FIELD = "updatedAt";


    private void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public SaveRecordModel(JsonNode recordModel) {
        this.node = (ObjectNode) recordModel;
        if (recordModel.path(UUID_FIELD).isMissingNode())
        {
            this.uuid = UUID.randomUUID().toString();
            node.put(UUID_FIELD,this.uuid);
        }
        JsonNode info = node.path(INFO_FIELD);
        if (!info.isMissingNode()) {
          JsonNode data = info.path(DATA_INFO);
          setData((ObjectNode) info,STATUS_FIELD,RecordStatus.NEW.toString());
          setData((ObjectNode) info,CREATE_AT_FIELD, dateToString(new Date()));
        }
    }

    private void setData(ObjectNode node, String path, String value) {
        JsonNode subNode = node.path(path);
        if (subNode.isMissingNode()){
          node.put(path,value);
        }
    }

    public SaveRecordModel(String line) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            node = (ObjectNode) mapper.readTree(line);
            uuid = node.path(UUID_FIELD).asText();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SaveRecordModel(String uuid, RecordStatus status, String data, Date createAt, Date deleteAt, List<Date> updatedAt) {
        node = JsonNodeFactory.instance.objectNode();
        setUuid(uuid);
        node.put(UUID_FIELD, uuid);
        ObjectNode infoNode = node.putObject(INFO_FIELD);
        infoNode.put(DATA_INFO, data);
        infoNode.put(STATUS_FIELD, status.toString());
        infoNode.put(CREATE_AT_FIELD, dateToString(createAt));
        if (deleteAt != null) {
            infoNode.put(DELETED_AT_FIELD, dateToString(deleteAt));

        };
        ArrayNode arrayNode = infoNode.putArray(UPDATE_AT_FIELD);
        if (updatedAt != null) {
            for (Date date : updatedAt) {
                arrayNode.add(date.getTime());
            }
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

    public void patchOperation(JsonPatch patch) throws JsonPatchException {
        ObjectNode info = (ObjectNode) node.path(INFO_FIELD);
        if (!info.path(STATUS_FIELD).asText().equals(RecordStatus.DELETED.toString())) {


            JsonNode nodeAppllied = (ObjectNode) patch.apply(node);
            if (verifyNode(nodeAppllied)) {
                node = (ObjectNode) nodeAppllied;
                info = (ObjectNode) node.path(INFO_FIELD);
                info.remove(STATUS_FIELD);
                info.put(STATUS_FIELD, RecordStatus.UPDATED.toString());
                ArrayNode updates = (ArrayNode) info.path(UPDATE_AT_FIELD);
                updates.add(dateToString(new Date()));
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "patch: " + patch.toString() + " contains invalid operations");
            }

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "key : " + uuid + " already deleted");
        }
    }

    private boolean verifyNode(JsonNode nodeAppllied) {
        JsonNode id = nodeAppllied.path(UUID_FIELD);
        JsonNode info = nodeAppllied.path(INFO_FIELD);
        JsonNode status = info.path(STATUS_FIELD);
        JsonNode createAt = info.path(CREATE_AT_FIELD);
        return !(id.isMissingNode() || info.isMissingNode() || status.isMissingNode() || createAt.isMissingNode());

    }

    public void markForDeletion() {
        if (!node.path(INFO_FIELD).path(STATUS_FIELD).asText().contains(RecordStatus.DELETED.toString())) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode info = (ObjectNode) node.path(INFO_FIELD);
            info.remove(STATUS_FIELD);
            info.remove(DELETED_AT_FIELD);
            info.put(STATUS_FIELD, RecordStatus.DELETED.toString());
            info.put(DELETED_AT_FIELD, dateToString(new Date()));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "key : " + uuid + " already deleted");

        }
    }

    public JsonNode getJsonNode() {
        return node;
    }

    private String dateToString(Date date) {
        return rfc3339.format(date);
    }
}
