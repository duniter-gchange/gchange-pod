package org.duniter.elasticsearch.gchange.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.duniter.core.service.CryptoService;
import org.duniter.elasticsearch.client.Duniter4jClient;
import org.duniter.elasticsearch.exception.InvalidFormatException;
import org.duniter.elasticsearch.gchange.PluginSettings;
import org.duniter.elasticsearch.gchange.dao.shape.ShapeDao;
import org.duniter.elasticsearch.user.service.DeleteHistoryService;
import org.elasticsearch.common.inject.Inject;

public class ShapeService extends AbstractService {

    private ShapeDao indexDao;
    private DeleteHistoryService deleteService;

    @Inject
    public ShapeService(Duniter4jClient client, PluginSettings settings,
                         CryptoService cryptoService,
                         DeleteHistoryService deleteService,
                         ShapeDao indexDao
    ) {
        super("gchange.shape", client, settings, cryptoService);
        this.indexDao = indexDao;

        this.deleteService = deleteService;
    }


    /**
     * Create index need for blockchain registry, if need
     */
    public ShapeService createIndexIfNotExists() {
        indexDao.createIndexIfNotExists();

        return this;
    }

    public ShapeService deleteIndex() {
        indexDao.deleteIndex();
        return this;
    }

    public ShapeService startDataMigration() {

        // Check if categories must be filled
        indexDao.startDataMigration();

        return this;
    }

    public String indexCountryFromJson(String json) {
        JsonNode actualObj = readAndVerifyIssuerSignature(json);
        String issuer = getIssuer(actualObj);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Indexing a %s from issuer [%s]", ShapeDao.TYPE, issuer.substring(0, 8)));
        }

        // Check id is unique
        JsonNode propertiesNode = getMandatoryField(actualObj, "properties");
        String id = getMandatoryField(propertiesNode, "id").asText();
        checkNotExists(ShapeDao.INDEX, ShapeDao.TYPE, id);

        return indexDao.create(id, json);
    }

    public void updateCountryFromJson(String id, String json) {
        JsonNode actualObj = readAndVerifyIssuerSignature(json);
        String issuer = getIssuer(actualObj);

        // Check issuer has access right
        checkIssuerIsAdminOrModerator(issuer);

        // CHeck same ID
        JsonNode propertiesNode = getMandatoryField(actualObj, "properties");
        String propertiesId = getMandatoryField(propertiesNode, "id").asText();
        if (!id.equals(propertiesId)) {
            throw new InvalidFormatException("Invalid format. Document id must be equals to 'properties.id'");
        }

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Updating %s [%s] from issuer [%s]", ShapeDao.TYPE, id, issuer.substring(0, 8)));
        }

        indexDao.update(id, json);
    }
}
