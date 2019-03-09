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


}
