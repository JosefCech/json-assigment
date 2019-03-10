package org.cechjoe.work.assigment.test.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.cechjoe.work.assigment.data.RecordModel;
import org.cechjoe.work.assigment.processor.DataFileProcessor;
import org.cechjoe.work.assigment.processor.RecordProcessor;
import org.junit.Test;

import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class RecordProcessorTest {
    @Test
    public void GivenData_WhenCreateNew_ThenNewRecordCreated() {
        DataFileProcessor dataFileProcessor = Mockito.mock(DataFileProcessor.class);
        when(dataFileProcessor.keyExists(anyString())).thenReturn(false);
        RecordProcessor recordProcessor = new RecordProcessor(dataFileProcessor);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode model = mapper.createObjectNode();
        ObjectNode info = mapper.createObjectNode();
        info.put("data", "base64");
        model.putObject("info");
        model.set("info", info);
        JsonNode node = recordProcessor.saveNewRecord(model);
        verify(dataFileProcessor).putRecord(any(RecordModel.class));
        assert (!node.path("recordId").asText().isEmpty());
    }

    @Test(expected = ResponseStatusException.class)
    public void GivenAlreadyExistKey_WhenCreateNew_ThenNewRecordCreated() {

        DataFileProcessor dataFileProcessor = Mockito.mock(DataFileProcessor.class);
        when(dataFileProcessor.keyExists(anyString())).thenReturn(true);
        RecordProcessor recordProcessor = new RecordProcessor(dataFileProcessor);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode model = mapper.createObjectNode();
        ObjectNode info = mapper.createObjectNode();
        info.put("recordData", "base64");
        model.putObject("info");
        model.set("info", info);
        JsonNode node = recordProcessor.saveNewRecord(model);
    }

    @Test
    public void GivenKey_WhenLoadData_ThenObjectIsLoaded() {
        DataFileProcessor dataFileProcessor = Mockito.mock(DataFileProcessor.class);
        String lineToRead = "{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"recordData\":\"test123\",\"recordStatus\":\"NEW\",\"created\":1552142013520,\"updated\":[]}}";
        RecordModel model = new RecordModel(lineToRead);
        when(dataFileProcessor.getRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b")).thenReturn(model);
        RecordProcessor recordProcessor = new RecordProcessor(dataFileProcessor);
        JsonNode node = recordProcessor.getRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b");
        verify(dataFileProcessor).getRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b");
        assert (node.path("recordId").asText().contains("bd987eac-d21b-4b63-a3f3-1d33f8081a0b"));
    }

    @Test(expected = ResponseStatusException.class)
    public void GivenDeletedRecord_WhenUpdated_ThenExceptionIsThrown() {
        DataFileProcessor dataFileProcessor = Mockito.mock(DataFileProcessor.class);
        String lineToRead = "{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"recordData\":\"test123\",\"recordStatus\":\"DELETED\",\"created\":1552142013520,\"updated\":[]}}";
        RecordModel model = new RecordModel(lineToRead);
        when(dataFileProcessor.getRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b")).thenReturn(model);
        RecordProcessor recordProcessor = new RecordProcessor(dataFileProcessor);
        recordProcessor.deleteRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b");
    }

}
