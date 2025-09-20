package com.mwarmstrong.wordgames.utilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Slf4j
public final class AppUtils {


    // Regex pattern for a 5-digit US zip code, with optional ZIP+4 part.
    // It is designed to match a zip code at the end of the string.
    static final String ZIP_CODE_REGEX = " *\\d{5}(?:-\\d{4})?$";

    static final ObjectMapper mapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static ObjectMapper getObjectMapper() {
        return mapper;
    }

    public static String createCacheKey(String source, double latitude, double longitude) {
        return String.format("%s for %.4f,%.4f", source, latitude, longitude);
    }

    public static String readFileContent(String fileName) throws IOException {
        Resource resource = new ClassPathResource(fileName); // fileName is relative to src/main/resources
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] bdata = org.springframework.util.StreamUtils.copyToByteArray(inputStream);
            return new String(bdata, StandardCharsets.UTF_8);
        }
    }
    public static <T> List<List<T>> chopList(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<List<T>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<T>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }
    public static String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static String toJsonFormatted(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static String printStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Removes a US zip code from the end of an address string.
     * This method handles both 5-digit and ZIP+4 formats.
     *
     * @param address The address string that may contain a zip code.
     * @return The address string with the zip code removed.
     */
    public static String removeZipCode(String address) {
        if (address == null || address.trim().isEmpty()) {
            return address;
        }
        // Use the Pattern and Matcher classes for more control, though String.replaceAll also works.
        // This is a common and efficient way to handle regex operations in Java.
        Pattern pattern = Pattern.compile(ZIP_CODE_REGEX);
        return pattern.matcher(address).replaceAll("").trim();
    }

    public static ZonedDateTime parseDateTime(String value, String timeZone) {
        ZonedDateTime zdt = ZonedDateTime.parse(value);
        return StringUtils.isAllEmpty(timeZone) ? zdt : zdt.withZoneSameInstant(ZoneId.of(timeZone));
    }
}
