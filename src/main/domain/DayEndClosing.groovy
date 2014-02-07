package domain

import groovy.transform.*
import simplejpa.DomainClass
import javax.persistence.*
import org.hibernate.annotations.Type
import javax.validation.constraints.*
import org.hibernate.validator.constraints.*
import org.joda.time.*

@DomainClass @Entity @Canonical
class DayEndClosing {

    @NotNull
    LocalDate tanggal

    @NotNull
    String user

    @NotNull
    BigDecimal tunai

    @NotNull
    BigDecimal aktualTunai

    @NotNull
    BigDecimal kartuKredit

    @NotNull
    BigDecimal kartuDebit

    @NotNull
    BigDecimal signedBill

    @NotNull
    BigDecimal compliment

    BigDecimal getTotal() {
        tunai + signedBill + kartuKredit + kartuDebit + compliment
    }

    BigDecimal getAktual() {
        aktualTunai + signedBill + kartuKredit + kartuDebit + compliment
    }

}

