package theatricalplays;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {

    private Map<String, Play> plays;

    public String print(Invoice invoice, Map<String, Play> plays) {
        return renderPlainText(new StatementData(invoice, plays));
    }

    private String renderPlainText(StatementData data) {
        StringBuilder result = new StringBuilder(String.format("Statement for %s%n", data.customer));

        // print line for this order
        for(var perf : data.performances){
            result.append(String.format("  %s: %s (%s seats)%n", perf.play.name, usd(perf.amount), perf.audience));

        }
        result.append(String.format("Amount owed is %s%n", usd(data.totalAmount)));
        result.append(String.format("You earned %s credits%n", data.totalVolumeCredits));
        return result.toString();
    }



    private String usd(long number) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(number / 100);
    }

}
