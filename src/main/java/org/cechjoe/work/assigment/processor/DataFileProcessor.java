package org.cechjoe.work.assigment.processor;

import org.cechjoe.work.assigment.data.RecordModel;
import org.cechjoe.work.assigment.repository.FileRepository;
import org.cechjoe.work.assigment.repository.IndexRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;


@Component
public class DataFileProcessor {


    FileRepository fileRepository;
    SavedDataProcessor savedDataProcessor;
    IndexRepository indexRepository;

    public DataFileProcessor(@NotNull FileRepository repository, @NotNull SavedDataProcessor savedDataProcessor, @NotNull IndexRepository indexRepository) {


        this.fileRepository = repository;
        this.savedDataProcessor = savedDataProcessor;
        this.indexRepository = indexRepository;
    }

    public void putRecord(@NotNull RecordModel recordModel) {
        int savedLine = fileRepository.appendLine(savedDataProcessor.createLine(recordModel));
        indexRepository.setIndex(recordModel.getUuid(), savedLine);
    }

    public RecordModel getRecord(@NotNull String uuid) {
        int lineNum = indexRepository.getIndex(uuid);
        String line = fileRepository.getLine(lineNum);
            if (!line.isEmpty()) {
                return savedDataProcessor.readRecord(line);
            } else {
                throw new ResponseStatusException(HttpStatus.NO_CONTENT, "line " + lineNum + "found but empty");
            }
    }

    public boolean keyExists(@NotNull String uuid)
    {
        int lineNum = indexRepository.getIndex(uuid);
        return  lineNum >= 0;
    }


}
