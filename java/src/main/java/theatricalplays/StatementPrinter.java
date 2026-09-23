package theatricalplays;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {

    private Invoice invoice;
    private Map<String, Play> plays;
    public String print(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;


        StringBuilder result = new StringBuilder(String.format("Statement for %s%n", invoice.customer));

        // print line for this order
        for(var perf : invoice.performances){
            result.append(String.format("  %s: %s (%s seats)%n", playFor(perf).name, usd(getThisAmount(perf)), perf.audience));

        }
        result.append(String.format("Amount owed is %s%n", usd(getTotalAmount(invoice))));
        result.append(String.format("You earned %s credits%n", totalVolumeCredits()));
        return result.toString();
    }

    private int getTotalAmount(Invoice invoice) {
        var totalAmount = 0;
        for(var perf: invoice.performances) {
            totalAmount += getThisAmount(perf);
        }
        return totalAmount;
    }

    private int totalVolumeCredits() {
        var volumeCredits = 0;
        for (var perf : invoice.performances) {

            volumeCredits += volumeCreditsFor(perf);
        }
        return volumeCredits;
    }

    private String usd(long number) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(number / 100);
    }

    private int volumeCreditsFor(Performance aPerformance) {
        // add volume credits
        int result = 0;
        result += Math.max(aPerformance.audience - 30, 0);
        // add extra credit for every ten comedy attendees
        if ("comedy".equals(playFor(aPerformance).type)) result += Math.floor(aPerformance.audience / 5);
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
