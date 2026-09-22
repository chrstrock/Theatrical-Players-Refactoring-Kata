package theatricalplays;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {

    private Map<String, Play> plays;
    public String print(Invoice invoice, Map<String, Play> plays) {
        this.plays = plays;
        var totalAmount = 0;
        var volumeCredits = 0;
        var result = String.format("Statement for %s%n", invoice.customer);

        NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);

        for (var perf : invoice.performances) {

            // add volume credits
            volumeCredits += Math.max(perf.audience - 30, 0);
            // add extra credit for every ten comedy attendees
            if ("comedy".equals(playFor(perf).type)) volumeCredits += Math.floor(perf.audience / 5);

            // print line for this order
            result += String.format("  %s: %s (%s seats)%n", playFor(perf).name, frmt.format(getThisAmount(perf) / 100), perf.audience);
            totalAmount += getThisAmount(perf);
        }
        result += String.format("Amount owed is %s%n", frmt.format(totalAmount / 100));
        result += String.format("You earned %s credits%n", volumeCredits);
        return result;
    }

    private Play playFor(Performance perf) {
        return this.plays.get(perf.playID);
    }

    private int getThisAmount(Performance aPerformance) {
        int result;
        switch (playFor(aPerformance).type) {
            case "tragedy":
                result = 40000;
                if (aPerformance.audience > 30) {
                    result += 1000 * (aPerformance.audience - 30);
                }
                break;
            case "comedy":
                result = 30000;
                if (aPerformance.audience > 20) {
                    result += 10000 + 500 * (aPerformance.audience - 20);
                }
                result += 300 * aPerformance.audience;
                break;
            default:
                throw new Error("unknown type: %s".formatted(playFor(aPerformance).type));
        }
        return result;
    }

}
