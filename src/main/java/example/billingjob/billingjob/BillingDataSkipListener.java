package example.billingjob.billingjob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;

import java.nio.file.Path;
import java.nio.file.Paths;


public class BillingDataSkipListener implements SkipListener<BillingData, BillingData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingDataSkipListener.class);

    Path skippedItemsFile;

    public BillingDataSkipListener(String skippedItemsFile) {
        this.skippedItemsFile = Paths.get(skippedItemsFile);
    }

    @Override
    public void onSkipInRead(Throwable throwable) {
        LOGGER.warn("Skipping item due to {}", throwable.getMessage());
        System.out.println("throwable = " + throwable);
//        if (throwable instanceof FlatFileParseException exception) {
//            String rawLine = exception.getInput();
//            int lineNumber = exception.getLineNumber();
//            String skippedLine = lineNumber + "|" + rawLine + System.lineSeparator();
//            try {
//                Files.writeString(this.skippedItemsFile, skippedLine, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
//            } catch (IOException e) {
//                throw new RuntimeException("Unable to write skipped item " + skippedLine);
//            }
//        }
    }
}
