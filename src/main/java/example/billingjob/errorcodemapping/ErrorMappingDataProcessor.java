package example.billingjob.errorcodemapping;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;


@Service
public class ErrorMappingDataProcessor implements ItemProcessor<ErrorMappingDataDto, ErrorMappingSql> {


    @Override
    public ErrorMappingSql process(ErrorMappingDataDto item) {
        String sql = "insert into hermione.processor_rejected_code_map (processor_id, rejected_code_id, provider_rejected_code_key, created_at) values (" + item.processor() + ", " + item.gmErrorCode() + ", '" + item.providerErrorCode() + "', now());";
        return new ErrorMappingSql(sql);
    }
}

