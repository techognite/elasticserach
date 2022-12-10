package elasicsearch.elasicsearch;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

public class GetEsDocumentWithNewUser {
	public static void main(String[] args) throws ElasticsearchException, IOException {

		ElasticsearchClient client = getElasticsearchClient();

		GetRequest.Builder getRequestBuilder = new GetRequest.Builder();
		getRequestBuilder.index("techognite");
		getRequestBuilder.id("200");

		GetRequest getRequest = getRequestBuilder.build();

		GetResponse<Technology> getResponse = client.get(getRequest, Technology.class);

		Technology technology = getResponse.source();

		System.out.println(technology.getId());
		System.out.println(technology.getName());
		System.out.println(technology.getSelfRating());

	}

	private static ElasticsearchClient getElasticsearchClient() {
		RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200));

		HttpClientConfigCallback httpClientConfigCallback = (httpClientBuilder) -> {
			final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

			credentialsProvider.setCredentials(AuthScope.ANY,
					new UsernamePasswordCredentials("elasticadmin", "elasticadmin"));

			return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
		};
		builder.setHttpClientConfigCallback(httpClientConfigCallback);

		RestClient restClient = builder.build();
		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

		return new ElasticsearchClient(transport);
	}

}
