package org.sonarsource.sonarqube.aggs;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

import java.net.UnknownHostException;

public class EsAggsGlobalQueryTest {

    private Client client;

    private String index = "components";
    private String type = "component";

    @Before
    public void createClient() throws UnknownHostException {
        this.client = MyEsClient.getTransportClient();
    }

    /***
     * GET projectmeasures/projectmeasure/_search
     * {
     *   "aggs" : {
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
    public void globalTest() {
        //
//        SearchRequest searchRequest = new SearchRequest(this.index);
//        searchRequest.types(type);
//        //
//        SearchSourceBuilder builder = new SearchSourceBuilder();
//
//        AggregationBuilders.global("reliability_rating")
//                .subAggregation(AggregationBuilders.cardinality("facet_filter_reliability_rating").subAggregation());
//
//        builder.aggregation(cardinalityAggregationBuilder);
//        //
//        searchRequest.source(builder);
//        //
//        SearchResponse searchResponse = client.search(searchRequest).get();
//        Cardinality cardinality = searchResponse.getAggregations().get("agg");
//        System.out.println(cardinality.getValue());
    }

}
