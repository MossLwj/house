package com.lwj.house.service.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lwj.house.entity.House;
import com.lwj.house.entity.HouseDetail;
import com.lwj.house.entity.HouseTag;
import com.lwj.house.repository.HouseDetailRepository;
import com.lwj.house.repository.HouseRepository;
import com.lwj.house.repository.HouseTagRepository;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author lwj
 */
@Service
public class SearchServiceImpl implements ISearchService{
    private static final Logger logger = LoggerFactory.getLogger(ISearchService.class);

    private static final String INDEX_NAME = "xunwu";

    private static final String INDEX_TYPE = "house";

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TransportClient esClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean index(Integer houseId) {
        House house = houseRepository.findById(houseId).orElse(null);
        if (house == null) {
            logger.error("Index house {} dose not exist!", houseId);
        }

        HouseIndexTemplate indexTemplate = new HouseIndexTemplate();
        modelMapper.map(house, indexTemplate);

        HouseDetail houseDetail = houseDetailRepository.findByHouseId(houseId);
        if (houseDetail == null) {
            //TODO 异常情况
        }
        modelMapper.map(houseDetail, indexTemplate);

        List<HouseTag> tages = houseTagRepository.findAllByHouseId(houseId);
        if (tages != null && !tages.isEmpty()) {
            List<String> tagStrings = new ArrayList<>();
            tages.forEach(houseTag -> {
                tagStrings.add(houseTag.getName());
            });
            indexTemplate.setTags(tagStrings);
        }
        //  校验在ES中是否已存在该数据
        SearchRequestBuilder searchRequestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
                .setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId));
        logger.debug(searchRequestBuilder.toString());
        SearchResponse searchResponse = searchRequestBuilder.get();

        boolean success;
        long totalHit = searchResponse.getHits().getTotalHits();
        if (totalHit == 0) {
            success = create(indexTemplate);
        } else if (totalHit == 1) {
            // 此处因为仅有一个索引，所以可以写0
            String esId = searchResponse.getHits().getAt(0).getId();
            success = update(esId, indexTemplate);
        } else {
            //  当如果命中量大于一个的时候说明有重复数据，则需要先删除数据，再创建
            success = deleteAndCreate(totalHit, indexTemplate);
        }
        if (success) {
            logger.debug("Index success with house " + houseId);
        }
        return success;
    }

    /**
     * 新增操作
     * @param houseIndexTemplate
     * @return
     */
    private boolean create(HouseIndexTemplate houseIndexTemplate) {
        try {
            IndexResponse response = this.esClient.prepareIndex(INDEX_NAME, INDEX_TYPE)
                    .setSource(objectMapper.writeValueAsBytes(houseIndexTemplate), XContentType.JSON).get();
            logger.debug("Create index with house: " + houseIndexTemplate.getHouseId());
            if (response.status() == RestStatus.CREATED) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            logger.error("Error to index house " + houseIndexTemplate.getHouseId(), e);
            return false;
        }
    }

    /**
     * 更新操作
     * @param esId
     * @param houseIndexTemplate
     * @return
     */
    private boolean update(String esId,HouseIndexTemplate houseIndexTemplate) {
        try {
            UpdateResponse response = this.esClient.prepareUpdate(INDEX_NAME, INDEX_TYPE, esId)
                    .setDoc(objectMapper.writeValueAsBytes(houseIndexTemplate), XContentType.JSON).get();
            logger.debug("Update index with house: " + houseIndexTemplate.getHouseId());
            if (response.status() == RestStatus.OK) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            logger.error("Error to index house " + houseIndexTemplate.getHouseId(), e);
            return false;
        }
    }

    /**
     * 删除并创建
     * @param totalHit
     * @param houseIndexTemplate
     * @return
     */
    private boolean deleteAndCreate(long totalHit,HouseIndexTemplate houseIndexTemplate) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseIndexTemplate.getHouseId())).source(INDEX_NAME);

        logger.debug("Delete by query for house: " + builder);

        BulkByScrollResponse response = builder.get();
        if (totalHit != response.getDeleted()) {
            logger.warn("Need delete {}, but {} was deleted!", totalHit, response.getDeleted());
            return false;
        } else {
            return create(houseIndexTemplate);
        }
    }

    @Override
    public void remove(Integer houseId) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId)).source(INDEX_NAME);

        logger.debug("Delete by query for house: " + builder);

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        logger.debug("Delete total " + deleted);
    }
}
