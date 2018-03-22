import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { WorkbasketListComponent } from './workbasket/list/workbasket-list.component';
import { WorkbasketDetailsComponent } from './workbasket/details/workbasket-details.component';
import { MasterAndDetailComponent } from './shared/masterAndDetail/master-and-detail.component';
import { NoAccessComponent } from './workbasket/noAccess/no-access.component';

const appRoutes: Routes = [
    {
        path: 'workbaskets',
        component: MasterAndDetailComponent,
        children: [
            {
                path: '',
                component: WorkbasketListComponent,
                outlet: 'master'
            },
            {
                path: 'noaccess',
                component: NoAccessComponent,
                outlet: 'detail'
            },
            {
                path: ':id',
                component: WorkbasketDetailsComponent,
                outlet: 'detail'
            }
        ]
    },
    {
        path: 'clasifications',
        component: MasterAndDetailComponent,
        children: [
            {
                path: '',
                component: WorkbasketListComponent,
                outlet: 'detail'
            }
        ]
    },
    {
        path: '',
        redirectTo: 'workbaskets',
        pathMatch: 'full'
    }
];
@NgModule({
    imports: [
        RouterModule.forRoot(
            appRoutes
        )
    ],
    exports: [
        RouterModule
    ]
})
export class AppRoutingModule { }
