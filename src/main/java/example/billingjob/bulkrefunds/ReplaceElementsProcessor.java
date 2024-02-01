package example.billingjob.bulkrefunds;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReplaceElementsProcessor implements ItemProcessor<RefundData, RefundList> {

    // create list of refund data
    List<String> refundDataList = List.of("9282214208",
            "9282226878",
            "9278124973",
            "9278157731",
            "9278104234",
            "9278149623",
            "9278163720",
            "9278178738",
            "9278121674",
            "9278183549",
            "9278173773",
            "9278186169");

    @Override
    public RefundList process(RefundData item){
        // compare the refund data with the list of refund data
        if (!refundDataList.contains(item.id())) {
            // if the refund data is in the list, return the refund data
            return new RefundList(item);
        }
        return null;
    }
}
