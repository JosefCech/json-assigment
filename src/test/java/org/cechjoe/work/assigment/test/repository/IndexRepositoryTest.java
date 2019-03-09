package org.cechjoe.work.assigment.test.repository;

import org.cechjoe.work.assigment.repository.FileRepository;
import org.cechjoe.work.assigment.repository.IndexRepository;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

public class IndexRepositoryTest {

    @Test
    public void GivenNewindexKeyPair_WhenIndexISaved_ThanValueIsThere()
    {
        FileRepository fileRepository =  Mockito.mock(FileRepository.class);
        when(fileRepository.getAllLines()).thenReturn( Stream.<String>builder().build());
        IndexRepository indexRepository = new IndexRepository(fileRepository);
        indexRepository.setIndex("new_id", 1);
        assert(indexRepository.getIndex("new_id") == 1);
    }

    @Test
    public void GivenTwiceNewIndexKeyPair_WhenIndexISaved_ThanValueIsThere()
    {
        FileRepository fileRepository =  Mockito.mock(FileRepository.class);
        when(fileRepository.getAllLines()).thenReturn( Stream.<String>builder().build());
        IndexRepository indexRepository = new IndexRepository(fileRepository);
        indexRepository.setIndex("new_id", 1);
        assert(indexRepository.getIndex("new_id") == 1);
        indexRepository.setIndex("new_id",2);
        assert(indexRepository.getIndex("new_id") == 2);

    }
}
