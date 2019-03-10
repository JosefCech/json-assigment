package org.cechjoe.work.assigment.data;

import com.fasterxml.jackson.databind.JsonNode;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.cechjoe.work.assigment.data.RecordConstant.*;
import static org.cechjoe.work.assigment.data.RecordConstant.CREATE_AT_FIELD;

public class RecordUtils {
    private static SimpleDateFormat rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    public static  String dateToString(Date date) {
        return rfc3339.format(date);
    }

    public static boolean verifyNewData(JsonNode newData) {
        JsonNode info = newData.path(INFO_FIELD);
        if (!info.isMissingNode()) {
            return !info.path(DATA_INFO).isMissingNode();
        }
        return false;
    }

    public static boolean verifyNode(JsonNode nodeAppllied) {
        JsonNode id = nodeAppllied.path(UUID_FIELD);
        JsonNode info = nodeAppllied.path(INFO_FIELD);
        JsonNode status = info.path(STATUS_FIELD);
        JsonNode createAt = info.path(CREATE_AT_FIELD);
        JsonNode data = info.path(DATA_INFO);
        return !(id.isMissingNode() || info.isMissingNode() || status.isMissingNode() || createAt.isMissingNode() || data.isMissingNode());

    }
}
