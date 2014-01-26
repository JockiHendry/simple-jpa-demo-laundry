package laundry

import groovy.beans.Bindable
import org.joda.time.LocalDate

class KriteriaLaporanPendapatanHarianModel {

    @Bindable LocalDate tanggalMulaiCari

    @Bindable LocalDate tanggalSelesaiCari

    @Bindable List result

    boolean batal = false

}