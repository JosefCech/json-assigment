package org.cechjoe.work.assigment.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.cechjoe.work.assigment.data.RecordModel;
import org.cechjoe.work.assigment.data.RecordStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;
import java.util.Date;

import static org.cechjoe.work.assigment.data.RecordConstant.*;
import static org.cechjoe.work.assigment.data.RecordConstant.DELETED_AT_FIELD;
import static org.cechjoe.work.assigment.data.RecordUtils.dateToString;
import static org.cechjoe.work.assigment.data.RecordUtils.verifyNode;

@Component
public class RecordFacade {


    public RecordModel patchOperation(@NotNull JsonPatch patch , @NotNull RecordModel recordModel) throws JsonPatchException {
        ObjectNode node  = (ObjectNode) recordModel.getJsonNode();
        ObjectNode info = (ObjectNode) node.path(INFO_FIELD);
        JsonNode nodeAppllied = (ObjectNode) patch.apply(node);
        if (verifyNode(nodeAppllied)) {
            node = (ObjectNode) nodeAppllied;
            info = (ObjectNode) node.path(INFO_FIELD);
            info.remove(STATUS_FIELD);
            info.put(STATUS_FIELD, RecordStatus.UPDATED.toString());
            if (info.path(UPDATE_AT_FIELD).isMissingNode()) {
                info.putArray(UPDATE_AT_FIELD);
            }
            ArrayNode updates = (ArrayNode) info.path(UPDATE_AT_FIELD);
            updates.add(dateToString(new Date()));
            node.set(INFO_FIELD,info);
            recordModel.setJsonNode(node);
            return  recordModel;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "patch: " + patch.toString() + " contains invalid operations");
        }
    }

    public RecordModel markForDeletion(RecordModel recordModel) {
        ObjectNode node = (ObjectNode) recordModel.getJsonNode();
        ObjectNode info = (ObjectNode) node.path(INFO_FIELD);
        info.remove(STATUS_FIELD);
        info.remove(DELETED_AT_FIELD);
        info.put(STATUS_FIELD, RecordStatus.DELETED.toString());
        info.put(DELETED_AT_FIELD, dateToString(new Date()));
        node.set(INFO_FIELD,info);
        recordModel.setJsonNode(node);
        return recordModel;
    }

}
