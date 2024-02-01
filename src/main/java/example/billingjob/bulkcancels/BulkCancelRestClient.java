package example.billingjob.bulkcancels;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class BulkCancelRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BulkCancelRestClient.class);

    private static final String URL = "http://gateway-manager.payments-live.eu1/gateway-manager/card/payment/%s/cancel";
    private static final String X_MERCHANT_ID = "X-IdMerchant";


    public DirectCardResponseDto sendPostRequest(String merchantId, String transactionId) {

        // Set up the headers for the request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(X_MERCHANT_ID, merchantId);
        headers.set("X-Forwarded-Proto", "http");
        headers.set("X-Forwarded-Port", "80");
        headers.set("X-Client", "tx-manager");
        headers.set("Content-Type", "application/json");
        headers.set("Host", "gateway-manager.payments-live.eu1");

        // Create an HttpEntity with the headers (and an empty body)
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        String url = String.format(URL, transactionId);
        ResponseEntity<DirectCardResponseDto> responseEntity = restTemplate.postForEntity(url, httpEntity, DirectCardResponseDto.class);

        HttpStatusCode statusCode = responseEntity.getStatusCode();
        LOGGER.info("Status code: {}", statusCode);

        return responseEntity.getBody();
    }
}
