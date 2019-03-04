package org.fenixedu.academic.domain.accounting.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.accounting.calculator.CreditEntry.View.Detailed;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */

@JsonPropertyOrder({ "type", "created", "date", "amount" })
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
    @Type(value = Payment.class, name = "payment"),
    @Type(value = DebtExemption.class, name = "debtExemption"),
    @Type(value = InterestExemption.class, name = "interestExemption"),
    @Type(value = FineExemption.class, name = "fineExemption")
})
public abstract class CreditEntry extends AccountingEntry implements Cloneable {

    interface View {
        interface Simple extends AccountingEntry.View.Simple {

        }

        interface Detailed extends Simple {
        }
    }

    @JsonView(Detailed.class)
    private final Set<PartialPayment> partialPayments = new HashSet<>();


    public CreditEntry(String id, DateTime created, LocalDate date, String description, BigDecimal amount) {
        super(id, created, date, description, amount);
    }

    public abstract boolean isForDebt();

    public abstract boolean isForInterest();

    public abstract boolean isForFine();

    public abstract boolean isToApplyInterest();

    public abstract boolean isToApplyFine();

    public void addPartialPayment(PartialPayment partialPayment) {
            this.partialPayments.add(partialPayment);
    }

    @JsonView(View.Detailed.class)
    public BigDecimal getUnusedAmount() {
        return getAmount().subtract(getUsedAmount());
    }

    @JsonView(View.Detailed.class)
    public BigDecimal getUsedAmount() {
        return getUsedAmount(null, null);
    }

    public BigDecimal getUsedAmount(Class<? extends DebtEntry> debtEntryClass) {
        return getUsedAmount(debtEntryClass, null);
    }

    public BigDecimal getUsedAmount(Class<? extends DebtEntry> debtEntryClass, DebtEntry debtEntry) {
        Stream<PartialPayment> partialPaymentStream = partialPayments.stream();

        if (debtEntryClass != null) {
            partialPaymentStream = partialPaymentStream.filter(partialPayment -> debtEntryClass.isInstance(partialPayment.getDebtEntry()));
        }

        if (debtEntry != null) {
            partialPaymentStream = partialPaymentStream.filter(partialPayment -> partialPayment.getDebtEntry() == debtEntry);
        }
        
        return partialPaymentStream.map(PartialPayment::getAmount).map(amount -> amount.setScale(2, RoundingMode.HALF_EVEN)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @JsonView(View.Detailed.class)
    public BigDecimal getUsedAmountInDebts() {
        return getUsedAmount(Debt.class);
    }

    @JsonView(View.Detailed.class)
    public BigDecimal getUsedAmountInInterests() {
        return getUsedAmount(Interest.class);
    }

    @JsonView(View.Detailed.class)
    public BigDecimal getUsedAmountInFines() {
        return getUsedAmount(Fine.class);
    }

    @JsonView(View.Detailed.class)
    public BigDecimal getUsedAmountInExcessRefunds() {
        return getUsedAmount(ExcessRefund.class);
    }

    @JsonView(View.Detailed.class)
    public BigDecimal getAmountInAdvance() {
        return getUnusedAmount().add(getUsedAmountInExcessRefunds());
    }

    public Set<PartialPayment> getPartialPayments() {
        return Collections.unmodifiableSet(partialPayments);
    }

    public Stream<PartialPayment> getSortedPartialPayments() {
        return partialPayments.stream().sorted((a,b) -> Comparator.comparing(DebtEntry::getCreated).thenComparing(DebtEntry::getDate)
                .thenComparing(DebtEntry::getId).compare(a.getDebtEntry(), b.getDebtEntry()));
    }

    @Override
    public String toString() {
        return String.format("%s{date=%s, amount=%s, partialPayments=%s, unusedAmount=%s, usedAmount=%s, usedAmountInDebts=%s, usedAmountInInterests=%s, usedAmountInFines=%s}",
                        getClass().getSimpleName(), getDate(), getAmount(), partialPayments, getUnusedAmount(), getUsedAmount(),
                        getUsedAmountInDebts(), getUsedAmountInInterests(), getUsedAmountInFines());
    }

}
