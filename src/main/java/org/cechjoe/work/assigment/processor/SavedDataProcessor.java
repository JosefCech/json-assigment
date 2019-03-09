package org.cechjoe.work.assigment.processor;


import org.cechjoe.work.assigment.data.RecordModel;
import org.springframework.stereotype.Component;

@Component
public class SavedDataProcessor {

    public String createLine(RecordModel recordModel) {

        return recordModel.serializeJson();
    }


    public RecordModel readRecord(String line) {

        try {
            return new RecordModel(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
