package io.vertx.armysystem.microservice.standard.impl;

import io.vertx.armysystem.business.common.*;
import io.vertx.armysystem.business.common.standard.rule.DailySchedule;
import io.vertx.armysystem.microservice.common.service.MongoRepositoryWrapper;
import io.vertx.armysystem.microservice.standard.StandardModelUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class DailyScheduleServiceImpl extends MongoRepositoryWrapper implements ServiceBase, CRUDService {
    private static final Logger logger = LoggerFactory.getLogger(DailyScheduleServiceImpl.class);
    private final Vertx vertx;

    public DailyScheduleServiceImpl(Vertx vertx, JsonObject config){
        super(vertx, config);

        this.vertx = vertx;
    }

    @Override
    public String getServiceName() {
        return "standard-DailySchedule-eb-service";
    }

    @Override
    public String getServiceAddress() {
        return "service.standard.DailySchedule";
    }

    @Override
    public String getPermission() {
        return "standard";
    }

    @Override
    public String getCollectionName() {
        return "DailySchedule";
    }

    @Override
    public CRUDService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
        this.createCollection(getCollectionName())
                .otherwise(err->null)
                .compose(o -> this.createIndexWithOptions(getCollectionName(),
                        new JsonObject().put("orgCategory",1), new JsonObject()))
                .otherwise(err->null).setHandler(resultHandler);
        return this ;
    }

    @Override
    public CRUDService addOne(JsonObject item, JsonObject principal, Handler<AsyncResult<JsonObject>> resultHandler) {
        validateParams(item, true)
                .compose(o -> this.insertOne(getCollectionName(), new DailySchedule(o).toJson()))
                .setHandler(resultHandler);

        return this;
    }

    @Override
    public CRUDService retrieveOne(String id, JsonObject principal, Handler<AsyncResult<JsonObject>> resultHandler) {
        this.findOne(getCollectionName(),getIdQuery(id), new JsonObject())
                .map(option -> option.orElse(null))
                .setHandler(resultHandler);
        return this;
    }

    @Override
    public CRUDService retrieveAll(JsonObject principal, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        this.retrieveManyByCondition(new JsonObject(), principal, resultHandler);
        return this;
    }

    @Override
    public CRUDService count(JsonObject condition, JsonObject principal, Handler<AsyncResult<Long>> resultHandler) {
        QueryCondition qConditon = QueryCondition.parse(condition);
        StandardModelUtil.countWithStandard(this, getCollectionName(), qConditon)
                .setHandler(resultHandler);
        return  this;
    }

    @Override
    public CRUDService retrieveManyByCondition(JsonObject condition, JsonObject principal, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        QueryCondition qCondition = QueryCondition.parse(condition);

        if (null == qCondition.getOption().getJsonObject("sort")){
            qCondition.getOption().put("sort",new JsonObject().put("orgCategoryObj.order", 1));
        }
        logger.info("query condition:"+ qCondition);
        AggregateBuilder builder = new AggregateBuilder()
                .addLookupStandard()
                .addLookupOrgCategory()
                .addQuery(qCondition.getQuery())
                .addOption(qCondition.getOption());

        this.aggregateQuery(getCollectionName(), builder.getPipeline(), new JsonObject())
                .map(list -> builder.fixLookupResults(list))
                .map(list -> list.stream()
                .map(item -> {
                    item.remove("orgCategoryObj");
                    return item;}).collect(Collectors.toList()))
                .setHandler(resultHandler);
        return this;
    }

    @Override
    public CRUDService updateOne(String id, JsonObject item, JsonObject principal, Handler<AsyncResult<JsonObject>> resultHandler) {
        item.remove("id");
        this.update(getCollectionName(), getIdQuery(id), new DailySchedule(item).toJson())
                .setHandler(ar -> {
                    if (ar.succeeded()){
                        this.retrieveOne(id, principal,resultHandler);
                    } else {
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                    }
                });
        return this;
    }

    @Override
    public CRUDService deleteOne(String id, JsonObject principal, Handler<AsyncResult<Void>> resultHandler) {
        this.remove(getCollectionName(),getIdQuery(id))
                .setHandler(resultHandler);
        return this;
    }

    private JsonObject getIdQuery(String id){
        return new JsonObject().put("_id", id);
    }

    private Future<JsonObject> validateParams(JsonObject item, Boolean forAdd){
        Future<JsonObject> future = Future.future();
        DailySchedule requirement = new DailySchedule(item);
        Boolean failed = false;

        if(forAdd){
            failed = BaseUtil.isEmpty(requirement.getOrgCategory());
        }

        if(failed){
            future.fail("Invalid Parameter");
        } else {
            future.complete(requirement.toJson());
        }
        return future;
    }
}
