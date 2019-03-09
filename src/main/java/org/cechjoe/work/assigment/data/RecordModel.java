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

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RecordModel {
    private String uuid;
    private SimpleDateFormat rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private ObjectNode node;

    private static String UUID_FIELD = "recordId";
    private static String INFO_FIELD = "info";
    private static String DATA_INFO = "data";
    private static String STATUS_FIELD = "recordStatus";
    private static String CREATE_AT_FIELD = "createdAt";
    private static String DELETED_AT_FIELD = "deletedAt";
    private static String UPDATE_AT_FIELD = "updatedAt";

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
        if (subNode.isMissingNode()) {
            node.put(path, value);
        }
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

    public void patchOperation(@NotNull JsonPatch patch) throws JsonPatchException {
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
        JsonNode data = info.path(DATA_INFO);
        return !(id.isMissingNode() || info.isMissingNode() || status.isMissingNode() || createAt.isMissingNode() || data.isMissingNode());

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
