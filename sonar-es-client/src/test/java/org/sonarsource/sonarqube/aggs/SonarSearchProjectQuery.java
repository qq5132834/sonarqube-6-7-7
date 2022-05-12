package org.sonarsource.sonarqube.aggs;

import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filters.FiltersAggregator;
import org.elasticsearch.search.aggregations.bucket.filters.InternalFilters;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.missing.Missing;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.search.aggregations.AggregationBuilders.filters;
import static org.sonar.server.measure.index.ProjectMeasuresIndexDefinition.FIELD_MEASURES;

/***
 * sonarqube中search_projects接口，可靠性评级reliablility_rating统计
 */
public class SonarSearchProjectQuery {

    private Client client;

    private String index = "projectmeasures";
    private String type = "projectmeasure";

    @Before
    public void createClient() throws UnknownHostException {
        this.client = MyEsClient.getTransportClient();
    }

    /***
     * 可靠性评级
     * GET projectmeasures/projectmeasure/_search
     * {
     *   "size" : 0,
     *   "_source" : false,
     *   "aggregations" : {
     *     "reliability_rating" : {
     *       "global" : { },
     *       "aggregations" : {
     *         "facet_filter_reliability_rating" : {
     *           "filter" : {
     *             "bool" : {
     *               "must" : [
     *                 {
     *                   "match_all" : {
     *                     "boost" : 1.0
     *                   }
     *                 }
     *               ],
     *               "disable_coord" : false,
     *               "adjust_pure_negative" : true,
     *               "boost" : 1.0
     *             }
     *           },
     *           "aggregations" : {
     *             "nested_reliability_rating" : {
     *               "nested" : {
     *                 "path" : "measures"
     *               },
     *               "aggregations" : {
     *                 "filter_reliability_rating" : {
     *                   "filter" : {
     *                     "terms" : {
     *                       "measures.key" : [
     *                         "reliability_rating"
     *                       ],
     *                       "boost" : 1.0
     *                     }
     *                   },
     *                   "aggregations" : {
     *                     "reliability_rating" : {
     *                       "filters" : {
     *                         "filters" : {
     *                           "1" : {
     *                             "term" : {
     *                               "measures.value" : {
     *                                 "value" : 1.0,
     *                                 "boost" : 1.0
     *                               }
     *                             }
     *                           },
     *                           "2" : {
     *                             "term" : {
     *                               "measures.value" : {
     *                                 "value" : 2.0,
     *                                 "boost" : 1.0
     *                               }
     *                             }
     *                           },
     *                           "3" : {
     *                             "term" : {
     *                               "measures.value" : {
     *                                 "value" : 3.0,
     *                                 "boost" : 1.0
     *                               }
     *                             }
     *                           },
     *                           "4" : {
     *                             "term" : {
     *                               "measures.value" : {
     *                                 "value" : 4.0,
     *                                 "boost" : 1.0
     *                               }
     *                             }
     *                           },
     *                           "5" : {
     *                             "term" : {
     *                               "measures.value" : {
     *                                 "value" : 5.0,
     *                                 "boost" : 1.0
     *                               }
     *                             }
     *                           }
     *                         },
     *                         "other_bucket" : false,
     *                         "other_bucket_key" : "_other_"
     *                       }
     *                     }
     *                   }
     *                 }
     *               }
     *             }
     *           }
     *         }
     *       }
     *     }
     *   }
     * }
     */
    @Test
    public void reliablilityRating() throws ExecutionException, InterruptedException {

        SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder(this.client, SearchAction.INSTANCE);
        searchRequestBuilder.setIndices(this.index);
        searchRequestBuilder.setTypes(this.type);
        searchRequestBuilder.setFetchSource(false);
        //searchRequestBuilder.setFrom(0);
        searchRequestBuilder.setSize(0);

        String metricKey = "reliability_rating";
        AggregationBuilder aggregationBuilder = AggregationBuilders.nested("nested_" + metricKey, FIELD_MEASURES)
                .subAggregation(
                        AggregationBuilders.filter("filter_" + metricKey, termsQuery("measures.key", metricKey))
                                .subAggregation(filters(metricKey,
                                        new FiltersAggregator.KeyedFilter("1", termQuery("measures.value", 1d)),
                                        new FiltersAggregator.KeyedFilter("2", termQuery("measures.value", 2d)),
                                        new FiltersAggregator.KeyedFilter("3", termQuery("measures.value", 3d)),
                                        new FiltersAggregator.KeyedFilter("4", termQuery("measures.value", 4d)),
                                        new FiltersAggregator.KeyedFilter("5", termQuery("measures.value", 5d)))));

        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        BoolQueryBuilder facetFilter = boolQuery().must(queryBuilder);

        AbstractAggregationBuilder abstractAggregationBuilder =
                AggregationBuilders.global(metricKey)
                                    .subAggregation(AggregationBuilders.filter("facet_filter_" + metricKey, facetFilter)
                                            .subAggregation(aggregationBuilder));

        searchRequestBuilder.addAggregation(abstractAggregationBuilder);

        SearchResponse searchResponse = searchRequestBuilder.get();

        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations != null) {
            for (Aggregation facet : aggregations) {
                processAggregation(facet);
            }
        }

        System.out.println();

    }

    private void processAggregation(Aggregation aggregation) {
        System.out.println(aggregation.getClass().getName());
        if (Missing.class.isAssignableFrom(aggregation.getClass())) {
            processMissingAggregation((Missing) aggregation);
        } else if (Terms.class.isAssignableFrom(aggregation.getClass())) {
            processTermsAggregation((Terms) aggregation);
        } else if (Filter.class.isAssignableFrom(aggregation.getClass())) {
            processSubAggregations((Filter) aggregation);
        } else if (HasAggregations.class.isAssignableFrom(aggregation.getClass())) {
            processSubAggregations((HasAggregations) aggregation);
        } else if (Histogram.class.isAssignableFrom(aggregation.getClass())) {
            processDateHistogram((Histogram) aggregation);
        } else if (Sum.class.isAssignableFrom(aggregation.getClass())) {
            processSum((Sum) aggregation);
        } else if (MultiBucketsAggregation.class.isAssignableFrom(aggregation.getClass())) {
            processMultiBucketAggregation((MultiBucketsAggregation) aggregation);
        } else {
            throw new IllegalArgumentException("Aggregation type not supported yet: " + aggregation.getClass());
        }
    }

    private void processSum(Sum aggregation) {
    }

    private void processMissingAggregation(Missing aggregation ) {
        long docCount = aggregation.getDocCount();
        System.out.println(docCount);
        if (docCount > 0L) {
//            LinkedHashMap<String, Long> facet = getOrCreateFacet(aggregation.getName().replace("_missing", ""));
//            if (aggregation.getAggregations().getAsMap().containsKey(FACET_MODE_EFFORT)) {
//                facet.put("", Math.round(((Sum) aggregation.getAggregations().get(FACET_MODE_EFFORT)).getValue()));
//            } else {
//                facet.put("", docCount);
//            }
        }
    }

    private void processTermsAggregation(Terms aggregation) {
        String facetName = aggregation.getName();
        System.out.println(facetName);
        for (Terms.Bucket value : aggregation.getBuckets()) {
            System.out.println(value.getKeyAsString());
            System.out.println(value.getDocCount());
        }
    }

    private void processSubAggregations(HasAggregations aggregation) {
        if (Filter.class.isAssignableFrom(aggregation.getClass())) {
            Filter filter = (Filter) aggregation;
            System.out.println(filter.getName());
            System.out.println(filter.getDocCount());
        }

        for (Aggregation sub : aggregation.getAggregations()) {
            processAggregation(sub);
        }
    }

    private void processDateHistogram(Histogram aggregation) {
        for (Histogram.Bucket value : aggregation.getBuckets()) {
            System.out.println(value.getKeyAsString() + "," + value.getDocCount());
        }
    }

    private void processMultiBucketAggregation(MultiBucketsAggregation aggregation) {
        List list = aggregation.getBuckets();
        for(int i= 0; i < list.size(); i++){
            Object object = list.get(i);
            System.out.println(object.getClass().getName());
            if(object instanceof InternalFilters.InternalBucket){
                InternalFilters.InternalBucket internalBucket = (InternalFilters.InternalBucket) object;
                System.out.println(internalBucket.getKey() + "," + internalBucket.getDocCount());
            }
        }
    }

}
