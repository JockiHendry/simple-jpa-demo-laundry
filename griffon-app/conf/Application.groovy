/*
 * Copyright 2013 Jocki Hendry.
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

application {
    title = 'Demo Sistem Laundry'
    startupGroups = ['mainGroup']
    autoShutdown = true
    locale = 'id_ID'
}
mvcGroups {
    // MVC Group for "antrianCuci"
    'antrianCuci' {
        model      = 'project.AntrianCuciModel'
        view       = 'project.AntrianCuciView'
        controller = 'project.AntrianCuciController'
    }

    // MVC Group for "pembayaran"
    'pembayaranSignedBill' {
        model      = 'project.PembayaranSignedBillModel'
        view       = 'project.PembayaranSignedBillView'
        controller = 'project.PembayaranSignedBillController'
    }

    // MVC Group for "workOrderAsChild"
    'workOrderAsChild' {
        model      = 'project.WorkOrderAsChildModel'
        view       = 'project.WorkOrderAsChildView'
        controller = 'project.WorkOrderAsChildController'
    }

    // MVC Group for "eventPekerjaanAsChild"
    'eventPekerjaanAsChild' {
        model      = 'project.EventPekerjaanAsChildModel'
        view       = 'project.EventPekerjaanAsChildView'
        controller = 'project.EventPekerjaanAsChildController'
    }

    // MVC Group for "itemWorkOrderAsChild"
    'itemWorkOrderAsChild' {
        model      = 'project.ItemWorkOrderAsChildModel'
        view       = 'project.ItemWorkOrderAsChildView'
        controller = 'project.ItemWorkOrderAsChildController'
    }

    // MVC Group for "workOrder"
    'workOrder' {
        model      = 'project.WorkOrderModel'
        view       = 'project.WorkOrderView'
        controller = 'project.WorkOrderController'
    }

    // MVC Group for "pelanggan"
    'pelanggan' {
        model      = 'project.PelangganModel'
        view       = 'project.PelangganView'
        controller = 'project.PelangganController'
    }

    // MVC Group for "work"
    'work' {
        model      = 'project.WorkModel'
        view       = 'project.WorkView'
        controller = 'project.WorkController'
    }

    // MVC Group for "jenisWork"
    'jenisWork' {
        model      = 'project.JenisWorkModel'
        view       = 'project.JenisWorkView'
        controller = 'project.JenisWorkController'
    }


    // MVC Group for "itemPakaian"
    'itemPakaian' {
        model      = 'project.ItemPakaianModel'
        view       = 'project.ItemPakaianView'
        controller = 'project.ItemPakaianController'
    }

    // MVC Group for "kategori"
    'kategori' {
        model      = 'project.KategoriModel'
        view       = 'project.KategoriView'
        controller = 'project.KategoriController'
    }

    // MVC Group for "mainGroup"
    'mainGroup' {
        model      = 'project.MainGroupModel'
        view       = 'project.MainGroupView'
        controller = 'project.MainGroupController'
    }

}
