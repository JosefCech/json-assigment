package org.cechjoe.work.assigment.Integration;

import org.cechjoe.work.assigment.repository.FileRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

public class FileRepositoryTest {

    @Test
    public void GivenRecords_WhenSaved_ReturnNumberOfSavedLine() {
        {
            FileRepository fileRepository = new FileRepository("dataTest.out");
            int line1 = fileRepository.appendLine("line1");
            int line2 = fileRepository.appendLine("line2");
            assert (line1 == 0);
            assert (line2 == 1);
            String line1S = fileRepository.getLine(0);
            String line2S = fileRepository.getLine(1);
            assert (line1S.equals("line1"));
            assert (line2S.equals("line2"));
        }
        new File("dataTest.out").delete();
    }

   @AfterClass
   @BeforeClass
    public static void CleanUp()
   {
       new File("dataTest.out").delete();
   }


}
