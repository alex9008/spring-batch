package example.billingjob.bulkrefunds;

import example.billingjob.billingjob.BillingData;

public record ReportingData(BillingData billingData, double billingTotal) {
}
