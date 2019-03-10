package org.cechjoe.work.assigment.test.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.cechjoe.work.assigment.data.RecordModel;
import org.cechjoe.work.assigment.data.RecordStatus;
import org.cechjoe.work.assigment.facade.RecordFacade;
import org.junit.Test;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.cechjoe.work.assigment.data.RecordConstant.*;

public class RecordFacadeTest {

    @Test
    public void GivenNewRecordModel_WhenMarkDeletion_ThenStatusAndDateIsUpdated() {
        String lineToRead = "{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"recordData\":\"test123\",\"recordStatus\":\"UPDATED\",\"created\":1552142013520}}";
        RecordModel model = new RecordModel(lineToRead);
        RecordFacade facade = new RecordFacade();
        RecordModel updatedModel = facade.markForDeletion(model);

        //isdeleted
        assert (updatedModel.getJsonNode().path(INFO_FIELD).path(STATUS_FIELD).asText().equals(RecordStatus.DELETED.toString()));
        assert (!updatedModel.getJsonNode().path(INFO_FIELD).path(DELETED_AT_FIELD).isMissingNode());

    }

    @Test
    public void GivenOperationReplaceData_WhenApply_ThenOperationIsApplied() throws JsonPatchException, IOException {
        String lineToRead = "{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"recordData\":\"test123\",\"recordStatus\":\"NEW\",\"created\":1552142013520}}";
        RecordModel model = new RecordModel(lineToRead);
        RecordFacade facade = new RecordFacade();
        ObjectMapper mapper = new ObjectMapper();
        JsonPatch patch = mapper.readValue("[{ \"op\": \"replace\", \"path\": \"/info/recordData\", \"value\" : \"new_data\" }]", JsonPatch.class);
        RecordModel updatedModel = facade.patchOperation(patch, model);
        assert (updatedModel.getJsonNode().path(INFO_FIELD).path(DATA_INFO).asText().equals("new_data"));
        assert (updatedModel.getJsonNode().path(INFO_FIELD).path(STATUS_FIELD).asText().equals(RecordStatus.UPDATED.toString()));
        assert (!updatedModel.getJsonNode().path(INFO_FIELD).path(UPDATE_AT_FIELD).isMissingNode());
    }

    @Test(expected = ResponseStatusException.class)
    public void GivenOperationRemoveData_WhenApply_ThenThrowException() throws JsonPatchException, IOException {
        String lineToRead = "{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"recordData\":\"test123\",\"recordStatus\":\"UPDATED\",\"created\":1552142013520}}";
        RecordModel model = new RecordModel(lineToRead);
        RecordFacade facade = new RecordFacade();
        ObjectMapper mapper = new ObjectMapper();
        JsonPatch patch = mapper.readValue("[{ \"op\": \"remove\", \"path\": \"/info/recordData\"}]", JsonPatch.class);
        RecordModel updatedModel = facade.patchOperation(patch, model);
    }
}
