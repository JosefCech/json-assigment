package org.cechjoe.work.assigment.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.cechjoe.work.assigment.data.SaveRecordModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class RecordProcessor {

    private DataFileProcessor dataFileProcessor;

    public RecordProcessor(DataFileProcessor dataFileProcessor) {
        this.dataFileProcessor = dataFileProcessor;
    }

      public JsonNode saveNewRecord(JsonNode newData) {
        SaveRecordModel savedData = new SaveRecordModel(newData);
        dataFileProcessor.putRecord(savedData);
        return savedData.getJsonNode();
    }

    public JsonNode getRecord(String key) {
        return dataFileProcessor.getRecord(key).getJsonNode();
    }


    public JsonNode deleteRecord(String key) {

        SaveRecordModel saveRecordModel = dataFileProcessor.getRecord(key);
        saveRecordModel.markForDeletion();
        dataFileProcessor.putRecord(saveRecordModel);
        return saveRecordModel.getJsonNode();
    }

    public JsonNode updateRecord(String key, JsonPatch patch) {
        SaveRecordModel saveRecordModel = dataFileProcessor.getRecord(key);
        try {
            saveRecordModel.patchOperation(patch);
            dataFileProcessor.putRecord(saveRecordModel);
             return saveRecordModel.getJsonNode();
        } catch (JsonPatchException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
