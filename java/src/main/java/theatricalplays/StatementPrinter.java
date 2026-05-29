package theatricalplays;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {

    public String print(Invoice invoice, Map<String, Play> plays) {
        var totalAmount = 0;
        StringBuilder result = new StringBuilder(String.format("Statement for %s%n", invoice.customer));
        for(var perf : invoice.performances){
            // print line for this order
            result.append(String.format("  %s: %s (%s seats)%n", playFor(plays, perf).name, usd(getThisAmount(perf, plays)), perf.audience));
            totalAmount += getThisAmount(perf, plays);
        }
        var volumeCredits = totalVolumeCredits(invoice, plays);

        result.append(String.format("Amount owed is %s%n", usd(totalAmount)));
        result.append(String.format("You earned %s credits%n", volumeCredits));
        return result.toString();
    }

    private static int totalVolumeCredits(Invoice invoice, Map<String, Play> plays) {
        var volumeCredits = 0;
        for (var perf : invoice.performances) {
            volumeCredits += volumeCreditsFor(plays, perf);
        }
        return volumeCredits;
    }

    private static String usd(int number) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(number / 100);
    }

    private static int volumeCreditsFor(Map<String, Play> plays, Performance perf) {
        int result = 0;
        result  = Math.max(perf.audience - 30, 0);
        // add extra credit for every ten comedy attendees
        if ("comedy".equals(playFor(plays, perf).type)) result += (int) Math.floor((double) perf.audience / 5);
        return result;
    }

    private static Play playFor(Map<String, Play> plays, Performance perf) {
        return plays.get(perf.playID);
    }

    private static int getThisAmount(Performance perf, Map<String, Play>plays) {
        var result = 0;

        switch (playFor(plays, perf).type) {
            case "tragedy" -> {
                result = 40000;
                if (perf.audience > 30) {
                    result += 1000 * (perf.audience - 30);
                }
            }
            case "comedy" -> {
                result = 30000;
                if (perf.audience > 20) {
                    result += 10000 + 500 * (perf.audience - 20);
                }
                result += 300 * perf.audience;
            }
            default -> throw new Error("unknown type: %s".formatted(playFor(plays, perf).type));
        }
        return result;
    }

}
