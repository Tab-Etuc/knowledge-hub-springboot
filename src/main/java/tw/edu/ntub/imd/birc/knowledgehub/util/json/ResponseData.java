package tw.edu.ntub.imd.birc.knowledgehub.util.json;

import com.fasterxml.jackson.databind.JsonNode;

@FunctionalInterface
public interface ResponseData {
    JsonNode getData();
}
