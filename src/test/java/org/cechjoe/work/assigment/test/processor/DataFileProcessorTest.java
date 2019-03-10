package org.cechjoe.work.assigment.test.processor;


import org.cechjoe.work.assigment.data.RecordModel;
import org.cechjoe.work.assigment.processor.DataFileProcessor;
import org.cechjoe.work.assigment.processor.SavedDataProcessor;
import org.cechjoe.work.assigment.repository.FileRepository;
import org.cechjoe.work.assigment.repository.IndexRepository;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class DataFileProcessorTest {

    @Test
    public void GivenId_WhenLineLoad_ThenWholeRecordLoaded() {
        FileRepository fileRepository = Mockito.mock(FileRepository.class);
        String lineToRead = "{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"data\":\"test123\",\"status\":\"NEW\",\"createdAt\":1552142013520,\"updateAt\":[]}}";

        when(fileRepository.getLine(1)).thenReturn(lineToRead);
        SavedDataProcessor savedDataProcessor = Mockito.mock(SavedDataProcessor.class);
        RecordModel model = new RecordModel("{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"data\":\"test123\",\"status\":\"NEW\",\"createdAt\":1552142013520,\"updateAt\":[]}}");
        when(savedDataProcessor.readRecord(lineToRead)).thenReturn(model);
        IndexRepository indexRepository = Mockito.mock(IndexRepository.class);
        when(indexRepository.getIndex("newId")).thenReturn(1);

        DataFileProcessor dataFileProcessor = new DataFileProcessor(fileRepository, savedDataProcessor, indexRepository);

        RecordModel returnedModel = dataFileProcessor.getRecord("newId");

        assert (returnedModel.getUuid() == model.getUuid());
        verify(fileRepository).getLine(1);
        verify(indexRepository).getIndex("newId");
        verify(savedDataProcessor).readRecord(lineToRead);
    }

    @Test
    public void GivenData_WhenWriteToFile_ThenIndexUpdated() {
        FileRepository fileRepository = Mockito.mock(FileRepository.class);
        String lineToWrite = "{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"data\":\"test123\",\"status\":\"NEW\",\"created\":1552142013520,\"updated\":[]}}";

        when(fileRepository.appendLine(lineToWrite)).thenReturn(1);
        SavedDataProcessor savedDataProcessor = Mockito.mock(SavedDataProcessor.class);
        RecordModel model = new RecordModel("{\"recordId\":\"bd987eac-d21b-4b63-a3f3-1d33f8081a0b\",\"info\":{\"recordData\":\"test123\",\"status\":\"NEW\",\"created\":1552142013520,\"updated\":[]}}");
        when(savedDataProcessor.createLine(model)).thenReturn(lineToWrite);
        IndexRepository indexRepository = Mockito.mock(IndexRepository.class);

        DataFileProcessor dataFileProcessor = new DataFileProcessor(fileRepository, savedDataProcessor, indexRepository);

        dataFileProcessor.putRecord(model);

        verify(fileRepository).appendLine(lineToWrite);
        verify(indexRepository).setIndex("bd987eac-d21b-4b63-a3f3-1d33f8081a0b", 1);
        verify(savedDataProcessor).createLine(model);
    }


    @Test
    public void GivenIdForDokument_WhenKeyExistsScan_ThenReturnTrue() {
        FileRepository fileRepository = Mockito.mock(FileRepository.class);
        SavedDataProcessor savedDataProcessor = Mockito.mock(SavedDataProcessor.class);
        IndexRepository indexRepository = Mockito.mock(IndexRepository.class);
        when(indexRepository.getIndex("bd987eac-d21b-4b63-a3f3-1d33f8081a0b")).thenReturn(1);
        DataFileProcessor dataFileProcessor = new DataFileProcessor(fileRepository, savedDataProcessor, indexRepository);

        boolean keyExists = dataFileProcessor.keyExists("bd987eac-d21b-4b63-a3f3-1d33f8081a0b");
        assert (keyExists);
        verify(indexRepository).getIndex("bd987eac-d21b-4b63-a3f3-1d33f8081a0b");
    }

    @Test
    public void GivenNonExistingDocumentID_WhenKeyExistsScan_ThenReturnTrue() {
        FileRepository fileRepository = Mockito.mock(FileRepository.class);
        SavedDataProcessor savedDataProcessor = Mockito.mock(SavedDataProcessor.class);
        IndexRepository indexRepository = Mockito.mock(IndexRepository.class);
        when(indexRepository.getIndex("bd987eac-d21b-4b63-a3f3-1d33f8081a0b")).thenReturn(-1);
        DataFileProcessor dataFileProcessor = new DataFileProcessor(fileRepository, savedDataProcessor, indexRepository);

        boolean keyExists = dataFileProcessor.keyExists("bd987eac-d21b-4b63-a3f3-1d33f8081a0b");
        assert (!keyExists);
        verify(indexRepository).getIndex("bd987eac-d21b-4b63-a3f3-1d33f8081a0b");
    }

}
