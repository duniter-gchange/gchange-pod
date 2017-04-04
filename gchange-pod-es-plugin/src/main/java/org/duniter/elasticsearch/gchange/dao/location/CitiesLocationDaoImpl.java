package org.duniter.elasticsearch.gchange.dao.location;

/*
 * #%L
 * UCoin Java Client :: Core API
 * %%
 * Copyright (C) 2014 - 2015 EIS
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.fasterxml.jackson.core.JsonProcessingException;
import org.duniter.core.exception.TechnicalException;
import org.duniter.elasticsearch.PluginSettings;
import org.duniter.elasticsearch.dao.AbstractDao;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

/**
 * Created by Benoit on 30/03/2015.
 */
public class CitiesLocationDaoImpl extends AbstractDao {

    private static final ESLogger log = ESLoggerFactory.getLogger(CitiesLocationDaoImpl.class.getName());

    private static final String CITIES_BULK_FILENAME = "registry-cities-bulk-insert.json";

    private static final String CITIES_SOURCE_CLASSPATH_FILE = "cities/countriesToCities.json";

    private static final String CITIES_SOURCE_FILE2 = "/home/blavenie/git/ucoin-io/duniter4j/duniter4j-elasticsearch/src/main/misc/geoflar-communes-2015.geojson";

    public static final String INDEX = "location";
    public static final String CITY_TYPE = "city";

    public CitiesLocationDaoImpl() {
        super("gchange.location.cities");
    }

    /**
     * Delete blockchain index, and all data
     * @throws JsonProcessingException
     */
    public void deleteIndex() throws JsonProcessingException {
        client.deleteIndexIfExists(INDEX);
    }


    public boolean existsIndex() {
        return client.existsIndex(INDEX);
    }

    /**
     * Create index need for blockchain registry, if need
     */
    public void createIndexIfNotExists() {
        try {
            if (!client.existsIndex(INDEX)) {
                createIndex();
            }
        }
        catch(JsonProcessingException e) {
            throw new TechnicalException(String.format("Error while creating index [%s]", INDEX));
        }
    }

    /**
     * Create index need for category registry
     * @throws JsonProcessingException
     */
    public void createIndex() throws JsonProcessingException {
        log.info(String.format("Creating index [%s/%s]", INDEX, CITY_TYPE));

        CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(INDEX);
        org.elasticsearch.common.settings.Settings indexSettings = org.elasticsearch.common.settings.Settings.settingsBuilder()
                .put("number_of_shards", 1)
                .put("number_of_replicas", 1)
                //.put("analyzer", createDefaultAnalyzer())
                .build();
        createIndexRequestBuilder.setSettings(indexSettings);
        createIndexRequestBuilder.addMapping(CITY_TYPE, createIndexMapping());
        createIndexRequestBuilder.execute().actionGet();
    }

    public void initCities() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing allOfToList registry cities");
        }

        //File bulkFile = createCitiesBulkFile2();

        // Insert cities
        //bulkFromFile(bulkFile, INDEX, CITY_TYPE);
    }


    /* -- Internal methods -- */


    public XContentBuilder createIndexMapping() {

        try {
            XContentBuilder mapping = XContentFactory.jsonBuilder().startObject().startObject(CITY_TYPE)
                    .startObject("properties")

                    // city
                    .startObject("name")
                    .field("type", "string")
                    .endObject()

                    // country
                    .startObject("country")
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()

                    .endObject()
                    .endObject().endObject();

            return mapping;
        }
        catch(IOException ioe) {
            throw new TechnicalException(String.format("Error while getting mapping for index [%s/%s]: %s", INDEX, CITY_TYPE, ioe.getMessage()), ioe);
        }
    }

    /*
    public File createCitiesBulkFile() {

        File result = new File(pluginSettings.getTempDirectory(), CITIES_BULK_FILENAME);

        InputStream ris = null;
        BufferedReader bf = null;
        FileWriter fw = null;
        try {
            if (result.exists()) {
                FileUtils.forceDelete(result);
            }
            else if (!result.getParentFile().exists()) {
                FileUtils.forceMkdir(result.getParentFile());
            }

            ris = getClass().getClassLoader().getResourceAsStream(CITIES_SOURCE_CLASSPATH_FILE);
            if (ris == null) {
                throw new TechnicalException(String.format("Could not retrieve file [%s] from test classpath. Make sure git submodules has been initialized before building.", CITIES_SOURCE_CLASSPATH_FILE));
            }

            boolean firstLine = true;
            java.lang.reflect.Type typeOfHashMap = new TypeToken<Map<String, String[]>>() { }.getType();

            Gson gson = GsonUtils.newBuilder().create();

            StringBuilder builder = new StringBuilder();
            bf = new BufferedReader(
                    new InputStreamReader(
                            ris, "UTF-16LE"), 2048);

            fw = new FileWriter(result);
            char[] buf = new char[2048];
            int len;

            while((len = bf.read(buf)) != -1) {
                String bufStr = new String(buf, 0, len);

                if (firstLine) {
                    // Remove UTF-16 BOM char
                    int objectStartIndex = bufStr.indexOf('\uFEFF');
                    if (objectStartIndex != -1) {
                        bufStr = bufStr.substring(objectStartIndex);
                    }
                    firstLine=false;
                }

                int arrayEndIndex = bufStr.indexOf("],\"");
                if (arrayEndIndex == -1) {
                    arrayEndIndex = bufStr.indexOf("]}");
                }

                if (arrayEndIndex == -1) {
                    builder.append(bufStr);
                }
                else {
                    builder.append(bufStr.substring(0, arrayEndIndex+1));
                    builder.append("}");
                    if (log.isTraceEnabled()) {
                        log.trace(builder.toString());
                    }
                    Map<String, String[]> citiesByCountry = gson.fromJson(builder.toString(), typeOfHashMap);

                    builder.setLength(0);
                    for (String country: citiesByCountry.keySet()) {
                        if (StringUtils.isNotBlank(country)) {
                            for (String city : citiesByCountry.get(country)) {
                                if (StringUtils.isNotBlank(city)) {
                                    fw.write(String.format("{\"index\":{\"_id\" : \"%s-%s\"}}\n", country, city));
                                    fw.write(String.format("{\"country\":\"%s\", \"name\":\"%s\"}\n", country, city));
                                }
                            }
                        }
                    }

                    fw.flush();

                    // reset and prepare buffer for next country
                    builder.setLength(0);
                    builder.append("{");
                    if (arrayEndIndex+2 < bufStr.length()) {
                        builder.append(bufStr.substring(arrayEndIndex+2));
                    }
                }
            }

            fw.close();
            bf.close();

        } catch(Exception e) {
            throw new TechnicalException(String.format("Error while creating cities file [%s]", result.getName()), e);
        }
        finally {
            IOUtils.closeQuietly(bf);
            IOUtils.closeQuietly(ris);
            IOUtils.closeQuietly(fw);
        }

        return result;
    }

    public File createCitiesBulkFile2() {

        File result = new File(pluginSettings.getTempDirectory(), CITIES_BULK_FILENAME);
        File inputFile = new File(CITIES_SOURCE_FILE2);

        InputStream ris = null;
        BufferedReader bf = null;
        FileWriter fw = null;
        try {
            if (result.exists()) {
                FileUtils.forceDelete(result);
            }
            else if (!result.getParentFile().exists()) {
                FileUtils.forceMkdir(result.getParentFile());
            }

            ris = new BufferedInputStream(new FileInputStream(inputFile));
            if (ris == null) {
                throw new TechnicalException(String.format("Could not retrieve file [%s] from test classpath. Make sure git submodules has been initialized before building.", CITIES_SOURCE_FILE2));
            }

            boolean firstLine = true;
            java.lang.reflect.Type typeOfHashMap = new TypeToken<Map<String, String[]>>() { }.getType();

            Gson gson = GsonUtils.newBuilder().create();

            StringBuilder builder = new StringBuilder();
            bf = new BufferedReader(
                    new InputStreamReader(
                            ris, "UTF-16LE"), 2048);

            fw = new FileWriter(result);
            char[] buf = new char[2048];
            int len;

            while((len = bf.read(buf)) != -1) {
                String bufStr = new String(buf, 0, len);

                if (firstLine) {
                    // Remove UTF-16 BOM char
                    int objectStartIndex = bufStr.indexOf('\uFEFF');
                    if (objectStartIndex != -1) {
                        bufStr = bufStr.substring(objectStartIndex);
                    }
                    firstLine=false;
                }

                int arrayEndIndex = bufStr.indexOf("],\"");
                if (arrayEndIndex == -1) {
                    arrayEndIndex = bufStr.indexOf("]}");
                }

                if (arrayEndIndex == -1) {
                    builder.append(bufStr);
                }
                else {
                    builder.append(bufStr.substring(0, arrayEndIndex+1));
                    builder.append("}");
                    if (log.isTraceEnabled()) {
                        log.trace(builder.toString());
                    }
                    Map<String, String[]> citiesByCountry = gson.fromJson(builder.toString(), typeOfHashMap);

                    builder.setLength(0);
                    for (String country: citiesByCountry.keySet()) {
                        if (StringUtils.isNotBlank(country)) {
                            for (String city : citiesByCountry.get(country)) {
                                if (StringUtils.isNotBlank(city)) {
                                    fw.write(String.format("{\"index\":{\"_id\" : \"%s-%s\"}}\n", country, city));
                                    fw.write(String.format("{\"country\":\"%s\", \"name\":\"%s\"}\n", country, city));
                                }
                            }
                        }
                    }

                    fw.flush();

                    // reset and prepare buffer for next country
                    builder.setLength(0);
                    builder.append("{");
                    if (arrayEndIndex+2 < bufStr.length()) {
                        builder.append(bufStr.substring(arrayEndIndex+2));
                    }
                }
            }

            fw.close();
            bf.close();

        } catch(Exception e) {
            throw new TechnicalException(String.format("Error while creating cities file [%s]", result.getName()), e);
        }
        finally {
            IOUtils.closeQuietly(bf);
            IOUtils.closeQuietly(ris);
            IOUtils.closeQuietly(fw);
        }

        return result;
    }
    */
}
