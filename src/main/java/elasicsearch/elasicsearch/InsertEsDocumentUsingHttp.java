package elasicsearch.elasicsearch;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

public class InsertEsDocumentUsingHttp {

	public static void main(String[] args) throws Exception {

		ElasticsearchClient client = getElasticsearchClient();

		Technology technology = new Technology();
		technology.setId(200);
		technology.setName("Java");
		technology.setSelfRating("9/10");

		IndexRequest.Builder<Technology> indexReqBuilder = new IndexRequest.Builder<>();

		indexReqBuilder.index("techognite");
		indexReqBuilder.id(technology.getId().toString());
		indexReqBuilder.document(technology);
		IndexRequest<Technology> indexRequest = indexReqBuilder.build();

		IndexResponse response = client.index(indexRequest);

		System.out.println("Indexed with version " + response.version());
	}

	private static ElasticsearchClient getElasticsearchClient() {
		RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200));

		HttpClientConfigCallback httpClientConfigCallback = new HttpClientConfigCallback() {
			@Override
			public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {

				final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

				AuthScope authScope = new AuthScope("localhost", 9200);
				UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials("elastic",
						"aJYdX=hoT7bPxH3AQKD_");

				credentialsProvider.setCredentials(authScope, usernamePasswordCredentials);

				return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
			}
		};

		builder.setHttpClientConfigCallback(httpClientConfigCallback);

		RestClient restClient = builder.build();

		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

		return new ElasticsearchClient(transport);
	}

}
