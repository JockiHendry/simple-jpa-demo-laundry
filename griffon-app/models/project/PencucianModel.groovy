package project

import ca.odell.glazedlists.BasicEventList
import domain.WorkOrder
import groovy.beans.Bindable
import org.joda.time.LocalDate

class PencucianModel {

    @Bindable String nomorSearch

    @Bindable LocalDate tanggal
    @Bindable String keterangan

    BasicEventList<WorkOrder> workOrderList = new BasicEventList<>()

}