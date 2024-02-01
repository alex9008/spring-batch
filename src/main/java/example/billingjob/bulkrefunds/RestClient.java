package example.billingjob.bulkrefunds;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class RestClient {

    private static final String URL = "https://www.atfawry.com/ECommerceWeb/Fawry/payments/refund";

    public ProviderRefundResponseDto sendPostRequest(ProviderRefundRequestDto requestBody) {

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(URL, requestBody, ProviderRefundResponseDto.class);

    }
}
