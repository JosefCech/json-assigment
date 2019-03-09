package org.cechjoe.work.assigment.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatch;
import org.cechjoe.work.assigment.processor.RecordProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;

@RestController
public class JsonController {

    private final RecordProcessor recordProcessor;

    public  JsonController(RecordProcessor recordProcessor)
    {
        this.recordProcessor = recordProcessor;
    }

    @RequestMapping(value = "/record" , method = RequestMethod.POST)
    @ResponseBody
    public JsonNode SaveNewData(@RequestBody JsonNode newData) {
        if (!verifyNewData(newData))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing data");
        }
        return (JsonNode) recordProcessor.saveNewRecord(newData);

    }

    private boolean verifyNewData(JsonNode newData) {
       JsonNode info = newData.path("info");
       if (!info.isMissingNode())
       {
          return !info.path("data").isMissingNode();
       }
        return false;
    }

    @RequestMapping(value = "/record/{key}" , method = RequestMethod.GET)
    @ResponseBody
    public JsonNode GetSavedData(@PathVariable ("key") String key) {
        return recordProcessor.getRecord(key);

    }

    @RequestMapping(value = "/record/{key}" , method = RequestMethod.DELETE)
    @ResponseBody
    public JsonNode deleteSavedData(@PathVariable ("key") String key) {
        return recordProcessor.deleteRecord(key);

    }

    @PatchMapping(value = "/record/{key}")
    @ResponseBody
    public JsonNode patchRecord(@RequestBody JsonPatch patch,@PathVariable("key") String key )
    {
        return recordProcessor.updateRecord(key,patch);
    }



}
