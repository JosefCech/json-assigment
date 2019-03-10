package org.cechjoe.work.assigment.test.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import org.cechjoe.work.assigment.data.RecordModel;
import org.cechjoe.work.assigment.data.RecordStatus;
import org.cechjoe.work.assigment.processor.DataFileProcessor;
import org.cechjoe.work.assigment.processor.RecordProcessor;
import org.json.JSONArray;
import org.junit.Test;

import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

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
        info.put("recordData", "base64");
        model.putObject("info");
        model.set("info", info);
        JsonNode node = recordProcessor.saveNewRecord(model);
        verify(dataFileProcessor).putRecord(any(RecordModel.class));
        assert (!node.path("recordId").asText().isEmpty());
    }

    @Test(expected = ResponseStatusException.class)
    public void GivenAlreadyExistKey_WhenCreateNew_ThenExceptionIsThrown() {

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

    @Test(expected = ResponseStatusException.class)
    public void GivenRecordWithoutDataField_WhenCreateNew_ThenExceptionIsThrown() {

        DataFileProcessor dataFileProcessor = Mockito.mock(DataFileProcessor.class);
        when(dataFileProcessor.keyExists(anyString())).thenReturn(false);
        RecordProcessor recordProcessor = new RecordProcessor(dataFileProcessor);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode model = mapper.createObjectNode();
        ObjectNode info = mapper.createObjectNode();
        model.putObject("info");
        model.set("info", info);
        JsonNode node = recordProcessor.saveNewRecord(model);
    }

    @Test
    public void GivenKey_WhenLoadData_ThenObjectIsLoaded() {
        DataFileProcessor dataFileProcessor = Mockito.mock(DataFileProcessor.class);
        when(dataFileProcessor.keyExists("bd987eac-d21b-4b63-a3f3-1d33f8081a0b")).thenReturn(true);
        String lineToRead = "{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"recordData\":\"test123\",\"recordStatus\":\"NEW\",\"created\":1552142013520,\"updated\":[]}}";
        RecordModel model = new RecordModel(lineToRead);
        when(dataFileProcessor.getRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b")).thenReturn(model);
        RecordProcessor recordProcessor = new RecordProcessor(dataFileProcessor);
        JsonNode node = recordProcessor.getRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b");
        verify(dataFileProcessor).getRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b");
        assert (node.path("recordId").asText().contains("bd987eac-d21b-4b63-a3f3-1d33f8081a0b"));
    }

    @Test
    public void GivenNeWRecord_WhenUpdated_ThenOperationIsSuccesfull() throws IOException {
        DataFileProcessor dataFileProcessor = Mockito.mock(DataFileProcessor.class);
        String lineToRead = "{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"recordData\":\"test123\",\"recordStatus\":\"NEW\",\"created\":1552142013520}}";
        RecordModel model = new RecordModel(lineToRead);
        when(dataFileProcessor.getRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b")).thenReturn(model);
        RecordProcessor recordProcessor = new RecordProcessor(dataFileProcessor);
        ObjectMapper mapper = new ObjectMapper();
        JsonPatch patch = mapper.readValue("[{ \"op\": \"add\", \"path\": \"/password\" }]", JsonPatch.class);
        JsonNode returnValue = recordProcessor.updateRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b",patch);
        assert (!returnValue.path("password").isMissingNode());
        assert (returnValue.path("info").path("recordStatus").asText().equals(RecordStatus.UPDATED.toString()));
        assert (!returnValue.path("info").path("updated").isMissingNode());
    }

    @Test
    public void GivenUpdatedRecord_WhenUpdated_ThenOperationIsSuccesfull() throws IOException {
        DataFileProcessor dataFileProcessor = Mockito.mock(DataFileProcessor.class);
        String lineToRead = "{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"recordData\":\"test123\",\"recordStatus\":\"UPDATED\",\"created\":1552142013520}}";
        RecordModel model = new RecordModel(lineToRead);
        when(dataFileProcessor.getRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b")).thenReturn(model);
        RecordProcessor recordProcessor = new RecordProcessor(dataFileProcessor);
        ObjectMapper mapper = new ObjectMapper();
        JsonPatch patch = mapper.readValue("[{ \"op\": \"add\", \"path\": \"/password\" }]", JsonPatch.class);
        JsonNode returnValue = recordProcessor.updateRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b",patch);
        assert (!returnValue.path("password").isMissingNode());
        assert (returnValue.path("info").path("recordStatus").asText().equals(RecordStatus.UPDATED.toString()));
    }


    @Test
    public void GivenNewRecord_WhenDelete_ThenOperationIsSuccesfull() {
        DataFileProcessor dataFileProcessor = Mockito.mock(DataFileProcessor.class);
        String lineToRead = "{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"recordData\":\"test123\",\"recordStatus\":\"NEW\",\"created\":1552142013520}}";
        RecordModel model = new RecordModel(lineToRead);
        when(dataFileProcessor.getRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b")).thenReturn(model);
        RecordProcessor recordProcessor = new RecordProcessor(dataFileProcessor);
        JsonNode returnValue = recordProcessor.deleteRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b");
        assert (returnValue.path("info").path("recordStatus").asText().equals(RecordStatus.DELETED.toString()));
        assert (!returnValue.path("info").path("deleted").asText().isEmpty());
    }

    @Test
    public void GivenUpdatedRecord_WhenDelete_ThenOperationIsSuccesfull() {
        DataFileProcessor dataFileProcessor = Mockito.mock(DataFileProcessor.class);
        String lineToRead = "{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"recordData\":\"test123\",\"recordStatus\":\"UPDATED\",\"created\":1552142013520}}";
        RecordModel model = new RecordModel(lineToRead);
        when(dataFileProcessor.getRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b")).thenReturn(model);
        RecordProcessor recordProcessor = new RecordProcessor(dataFileProcessor);
        JsonNode returnValue = recordProcessor.deleteRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b");
        assert (returnValue.path("info").path("recordStatus").asText().equals(RecordStatus.DELETED.toString()));
        assert (!returnValue.path("info").path("deleted").asText().isEmpty());
    }


    @Test(expected = ResponseStatusException.class)
    public void GivenDeletedRecord_WhenUpdated_ThenExceptionIsThrown() throws IOException {
        DataFileProcessor dataFileProcessor = Mockito.mock(DataFileProcessor.class);
        String lineToRead = "{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"recordData\":\"test123\",\"recordStatus\":\"DELETED\",\"created\":1552142013520,\"updated\":[]}}";
        RecordModel model = new RecordModel(lineToRead);
        when(dataFileProcessor.getRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b")).thenReturn(model);
        RecordProcessor recordProcessor = new RecordProcessor(dataFileProcessor);
        ObjectMapper mapper = new ObjectMapper();
        JsonPatch patch = mapper.readValue("[{ \"op\": \"add\", \"path\": \"/password\" }]", JsonPatch.class);
        recordProcessor.updateRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b",patch);
    }

    @Test(expected = ResponseStatusException.class)
    public void GivenDeletedRecord_WhenDelete_ThenExceptionIsThrown() {
        DataFileProcessor dataFileProcessor = Mockito.mock(DataFileProcessor.class);
        String lineToRead = "{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"recordData\":\"test123\",\"recordStatus\":\"DELETED\",\"created\":1552142013520,\"updated\":[]}}";
        RecordModel model = new RecordModel(lineToRead);
        when(dataFileProcessor.getRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b")).thenReturn(model);
        RecordProcessor recordProcessor = new RecordProcessor(dataFileProcessor);
        recordProcessor.deleteRecord("bd987eac-d21b-4b63-a3f3-1d33f8081a0b");
    }


}
