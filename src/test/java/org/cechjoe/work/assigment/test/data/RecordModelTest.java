package org.cechjoe.work.assigment.test.data;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.cechjoe.work.assigment.common.JsonNodeHelper;
import org.cechjoe.work.assigment.data.RecordModel;
import org.cechjoe.work.assigment.data.RecordStatus;
import org.junit.Test;


public class RecordModelTest {

    @Test
    public void GivenJsonNodeWithGuid_WhenRecordModel_ThenGuidIsGenerated()
    {
        JsonNode request = JsonNodeHelper.GetPostRequestJsonNode();
        ((ObjectNode) request).put("requestId", "newId");
        RecordModel recordModel = new RecordModel(request);
        assert(!recordModel.getUuid().equals("newId"));
        assert (recordModel.getJsonNode().path("info").path("recordStatus").asText().equals(RecordStatus.NEW.toString()));
    }

    @Test
    public void GivenJsonNodeWitoutGuid_WhenRecordModel_ThenGuidIsGenerated()
    {
        JsonNode request = JsonNodeHelper.GetPostRequestJsonNode();
        RecordModel recordModel = new RecordModel(request);
        assert(!recordModel.getUuid().isEmpty());
        assert (recordModel.getJsonNode().path("info").path("recordStatus").asText().equals(RecordStatus.NEW.toString()));

    }

    @Test
    public  void GivenJsonString_WhenRecordObject_ThenObjectIsCreated()
    {
        String lineToWrite = "{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"data\":\"test123\",\"status\":\"NEW\",\"createdAt\":1552142013520,\"updateAt\":[]}}";
        RecordModel recordModel = new RecordModel(lineToWrite);
        assert(recordModel.getUuid().equals("bd987eac-d21b-4b63-a3f3-1d33f8081a0b"));
        assert(!recordModel.getJsonNode().isMissingNode());

    }


}
