import simplejpa.transaction.TransactionHolder
import util.BusyLayerUI

onUncaughtExceptionThrown = { Exception e ->
    if (e instanceof org.codehaus.groovy.runtime.InvokerInvocationException) e = e.cause.cause
    javax.swing.JOptionPane.showMessageDialog(null, e.message, "Error", javax.swing.JOptionPane.ERROR_MESSAGE)
}

onSimpleJpaNewTransaction = { TransactionHolder th ->
    BusyLayerUI.getInstance().show()
}

onSimpleJpaCommitTransaction = { TransactionHolder th ->
    BusyLayerUI.getInstance().hide()
}

onSimpleJpaRollbackTransaction = { TransactionHolder th ->
    BusyLayerUI.getInstance().hide()
}