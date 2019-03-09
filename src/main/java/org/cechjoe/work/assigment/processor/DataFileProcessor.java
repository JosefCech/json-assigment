package org.cechjoe.work.assigment.processor;

import org.cechjoe.work.assigment.data.SaveRecordModel;
import org.cechjoe.work.assigment.repository.FileRepository;
import org.cechjoe.work.assigment.repository.IndexRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class DataFileProcessor {

    FileRepository fileRepository;
    SavedDataProcessor savedDataProcessor;
    IndexRepository indexRepository;

   public DataFileProcessor(FileRepository repository, SavedDataProcessor savedDataProcessor, IndexRepository indexRepository)
   {
       this.fileRepository = repository;
       this.savedDataProcessor =  savedDataProcessor;
       this.indexRepository = indexRepository;
   }

    public void putRecord(SaveRecordModel saveRecordModel) {
       int savedLine =  fileRepository.appendLine(savedDataProcessor.createLine(saveRecordModel));
       indexRepository.setIndex(saveRecordModel.getUuid(),savedLine);
    }

    public SaveRecordModel getRecord(String uuid)
    {
        int lineNum = indexRepository.getIndex(uuid);
        String line = fileRepository.getLine(lineNum);
        if (line != "") {
            return savedDataProcessor.readRecord(line);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "key : " + uuid + " not found");
        }
    }



}
