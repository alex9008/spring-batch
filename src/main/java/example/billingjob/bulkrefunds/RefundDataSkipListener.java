package example.billingjob.bulkrefunds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class RefundDataSkipListener implements SkipListener<RefundData, FinalResultDto> {


    Path skippedItemsFile;

    public RefundDataSkipListener(String skippedItemsFile) {
        this.skippedItemsFile = Paths.get(skippedItemsFile);
    }

    @Override
    public void onSkipInProcess(@NonNull RefundData refundData, Throwable t) {
        if (t instanceof Exception) {
            String skippedLine = refundData.id() + "|" + refundData.amount() + "|" + t.getMessage() + System.lineSeparator();
            try {
                Files.writeString(this.skippedItemsFile, skippedLine, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            } catch (IOException e) {
                throw new RuntimeException("Unable to write skipped item " + skippedLine);
            }
        }
    }
}
