package theatricalplays;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {

    private Map<String, Play> plays;
    private StatementData data;

    public String print(Invoice invoice, Map<String, Play> plays) {
        this.plays = plays;
        this.data = new StatementData();
        this.data.customer = invoice.customer;
        this.data.performances = invoice.performances.stream().map(this::enrichPerformance).toList();
        return renderPlainText();
    }

    private EnrichedPerformance enrichPerformance(Performance performance) {
        var result = new EnrichedPerformance();
        result.playID = performance.playID;
        result.audience = performance.audience;
        result.play = playFor(performance);
        return result;
    }

    private String renderPlainText() {
        StringBuilder result = new StringBuilder(String.format("Statement for %s%n", data.customer));

        // print line for this order
        for(var perf : data.performances){
            result.append(String.format("  %s: %s (%s seats)%n", perf.play.name, usd(getThisAmount(perf)), perf.audience));

        }
        result.append(String.format("Amount owed is %s%n", usd(getTotalAmount())));
        result.append(String.format("You earned %s credits%n", totalVolumeCredits()));
        return result.toString();
    }

    private int getTotalAmount() {
        var totalAmount = 0;
        for(var perf: data.performances) {
            totalAmount += getThisAmount(perf);
        }
        return totalAmount;
    }

    private int totalVolumeCredits() {
        var volumeCredits = 0;
        for (var perf : data.performances) {

            volumeCredits += volumeCreditsFor(perf);
        }
        return volumeCredits;
    }

    private String usd(long number) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(number / 100);
    }

    private int volumeCreditsFor(EnrichedPerformance aPerformance) {
        // add volume credits
        int result = 0;
        result += Math.max(aPerformance.audience - 30, 0);
        // add extra credit for every ten comedy attendees
        if ("comedy".equals(aPerformance.play.type)) result += Math.floor(aPerformance.audience / 5);
        return result;
    }

    private Play playFor(Performance perf) {
        return this.plays.get(perf.playID);
    }

    private int getThisAmount(EnrichedPerformance aPerformance) {
        int result;
        switch (aPerformance.play.type) {
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
                throw new Error("unknown type: %s".formatted(aPerformance.play.type));
        }
        return result;
    }

}
