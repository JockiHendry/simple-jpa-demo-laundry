/*
 * Copyright 2014 Jocki Hendry.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import simplejpa.transaction.TransactionHolder
import util.BusyLayerUI

onUncaughtExceptionThrown = { Exception e ->
    BusyLayerUI.getInstance().hide()
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