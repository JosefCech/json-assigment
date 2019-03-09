package org.cechjoe.work.assigment.processor;


import org.cechjoe.work.assigment.data.SaveRecordModel;
import org.springframework.stereotype.Component;

@Component
public class SavedDataProcessor {

    public String createLine(SaveRecordModel saveRecordModel) {

        return saveRecordModel.serializeJson();
    }


    public SaveRecordModel readRecord(String line) {

        try {
            return new SaveRecordModel(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
