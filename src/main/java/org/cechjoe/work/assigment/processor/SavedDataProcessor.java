package org.cechjoe.work.assigment.processor;


import org.cechjoe.work.assigment.data.RecordModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;

@Component
public class SavedDataProcessor {

    public String createLine(@NotNull RecordModel recordModel) {

        return recordModel.serializeJson();
    }

    public RecordModel readRecord(@NotNull String line) {
        try {
            return new RecordModel(line);
        } catch (Exception e) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
        }

    }

}
