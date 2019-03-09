package org.cechjoe.work.assigment.repository;

import org.cechjoe.work.assigment.data.RecordModel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Component
public class IndexRepository {
    private Map<String,Integer> indexes;

    public IndexRepository(FileRepository fileRepository)
    {
        this.indexes = new HashMap<String,Integer>();
        try (Stream<String> stream =  fileRepository.getAllLines()) {
            AtomicInteger index = new AtomicInteger();
            stream.forEach(line -> {
                RecordModel recordModel = new RecordModel(line);
                setIndex(recordModel.getUuid(), index.get());
                index.addAndGet(1);
            });
        }
    }

    public void setIndex(String key, int indexLine )
    {
        if (indexes.containsKey(key))
        {
            indexes.replace(key,indexLine);
        }
        else {
            indexes.put(key,indexLine);
        }

    }

    public int getIndex(String key)
    {
        if (indexes.containsKey(key))
        {
            return indexes.get(key).intValue();
        }
        return  -1;
    }
}
