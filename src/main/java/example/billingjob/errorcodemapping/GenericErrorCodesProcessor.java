package example.billingjob.errorcodemapping;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;



@Service
public class GenericErrorCodesProcessor implements ItemProcessor<GenericErrorCodesDto, ErrorMappingSql> {


    @Override
    public ErrorMappingSql process(GenericErrorCodesDto item) {
        // how to get all the rejected_code_id from hermione.rejected_code table where code in (item.gmErrorCode())
        // how to build a sql statement with a list of values
        String sql = "select * from hermione.rejected_code where code in ('" + String.join(",", item.gmErrorCode()) + "');";
        return new ErrorMappingSql(sql);
    }
}

