package org.cechjoe.work.assigment.test.processor;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.cechjoe.work.assigment.data.RecordModel;
import org.cechjoe.work.assigment.processor.SavedDataProcessor;
import org.junit.Test;


public class SavedDataProcessorTest {

    @Test
    public void GivenFullSavedRecord_WhenLineIsGenerated_ThenSuccesfullyCreated() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode model = mapper.createObjectNode();
        ObjectNode info = mapper.createObjectNode();
        info.put("data", "test123");
        model.putObject("info");
        model.set("info",info);
         RecordModel model1 = new RecordModel(model);
        SavedDataProcessor processor = new SavedDataProcessor();

        String line = processor.createLine(model1);
        assert (line.contains("test123"));

    }

    @Test
    public void GivenLineWithMinimumValues_WhenLineToObject_ThenSuccessfullyMigrated() {
        String line = "{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"data\":\"test123\",\"status\":\"NEW\",\"createdAt\":1552142013520,\"updateAt\":[]}}";
        SavedDataProcessor processor = new SavedDataProcessor();
        RecordModel model = processor.readRecord(line);
        assert (model.getUuid().equals("bd987eac-d21b-4b63-a3f3-1d33f8081a0b"));

    }
}
