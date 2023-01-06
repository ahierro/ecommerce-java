package com.iron.tec.labs.ecommercejava.mappers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ReadingConverter
@AllArgsConstructor
public class JsonToListOfStringConverter implements Converter<Json, List<String>> {

    private final ObjectMapper objectMapper;

    @Override
    public List<String> convert(Json json) {
        try {
            return objectMapper.readValue(json.asString(), TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
        } catch (IOException e) {
            log.error("Problem while parsing JSON: {}", json, e);
        }
        return new ArrayList<>();
    }

}
