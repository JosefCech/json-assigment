package org.cechjoe.work.assigment.test.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.cechjoe.work.assigment.data.RecordModel;
import org.cechjoe.work.assigment.processor.DataFileProcessor;
import org.cechjoe.work.assigment.processor.RecordProcessor;
import org.junit.Test;

import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class RecordProcessorTest {
    @Test
    public void GivenData_WhenCreateNew_ThenNewRecordCreated() {

        DataFileProcessor dataFileProcessor = Mockito.mock(DataFileProcessor.class);
        RecordProcessor recordProcessor = new RecordProcessor(dataFileProcessor);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode model = mapper.createObjectNode();
        ObjectNode info = mapper.createObjectNode();
        info.put("data","base64");
        model.putObject("info");
        model.set("info",info);
        JsonNode node = recordProcessor.saveNewRecord(model);
        verify(dataFileProcessor).putRecord(any(RecordModel.class));
        assert(node.path("recordId").asText() != "");
    }

    @Test
    public void GivenKey_WhenLoadData_ThenObjectIsLoaded()
    {
        DataFileProcessor dataFileProcessor = Mockito.mock(DataFileProcessor.class);
        String lineToRead = "{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"data\":\"test123\",\"status\":\"NEW\",\"createdAt\":1552142013520,\"updateAt\":[]}}";
        RecordModel model = new RecordModel(lineToRead);
        when(dataFileProcessor.getRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b")).thenReturn(model);
        RecordProcessor recordProcessor = new RecordProcessor(dataFileProcessor);
        JsonNode node = recordProcessor.getRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b");
        verify(dataFileProcessor).getRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b");
        assert(node.path("recordId").asText().contains("bd987eac-d21b-4b63-a3f3-1d33f8081a0b"));
    }
}
