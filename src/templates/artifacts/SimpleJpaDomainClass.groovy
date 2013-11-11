package ${packageName}

import groovy.transform.*
import simplejpa.DomainClass
import javax.persistence.*
import org.hibernate.annotations.Type
import javax.validation.constraints.*
import org.hibernate.validator.constraints.*
import org.joda.time.*

@DomainClass @Entity @Canonical
class ${className} {

    // Example of attribute declaration:
    //
    // @Size(min=2, max=50)
    // String name
    //
    // @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    // DateTime birthDate
    //
    // String address
    //
    // Integer score
    //
    // @NotNull @ManyToOne
    // ClassRoom classRoom
    //
    // @NotEmpty @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
    // List<ItemTransaksi> listItemTransaksi = []

}

