package theatricalplays;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {

    private Map<String, Play> plays;

    public String print(Invoice invoice, Map<String, Play> plays) {
        return renderPlainText(new StatementData(invoice, plays));
    }

    public String htmlStatement(Invoice invoice, Map<String, Play> plays) {
        return renderHtml(new StatementData(invoice, plays));
    }

    private String renderHtml(StatementData statementData) {
        StringBuilder result = new StringBuilder(String.format("<h1>Statement for %s%n", statementData.customer));
        result.append("<table>\n");
        result.append("<tr><th>play</th><th>seats</th><th>cost</th></tr>");
        for(var perf: statementData.performances){
            result.append(String.format(" <tr><td>%s</td><td>%s</td><td>%s</td></tr>%n",
                    perf.play.name, usd(perf.amount), perf.audience));
        }
        result.append("</table>\n");
        result.append(String.format("<p>Amount owed is <em>%s</em></p>%n", usd(statementData.totalAmount)));
        result.append(String.format("<p>You earned <em>%s</em> credits</p>%n", statementData.totalVolumeCredits));
        return result.toString();
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
