package org.cechjoe.work.assigment.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.cechjoe.work.assigment.data.RecordModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;

@Component
public class RecordProcessor {

    private DataFileProcessor dataFileProcessor;

    public RecordProcessor(@NotNull DataFileProcessor dataFileProcessor) {
        this.dataFileProcessor = dataFileProcessor;
    }

      public JsonNode saveNewRecord(JsonNode newData) {
        RecordModel savedData = new RecordModel(newData);
        dataFileProcessor.putRecord(savedData);
        return savedData.getJsonNode();
    }

    public JsonNode getRecord(String key) {
        return dataFileProcessor.getRecord(key).getJsonNode();
    }


    public JsonNode deleteRecord(String key) {

        RecordModel recordModel = dataFileProcessor.getRecord(key);
        recordModel.markForDeletion();
        dataFileProcessor.putRecord(recordModel);
        return recordModel.getJsonNode();
    }

    public JsonNode updateRecord(String key, JsonPatch patch) {
        RecordModel recordModel = dataFileProcessor.getRecord(key);
        try {
            recordModel.patchOperation(patch);
            dataFileProcessor.putRecord(recordModel);
             return recordModel.getJsonNode();
        } catch (JsonPatchException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
