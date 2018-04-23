import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { WorkbasketListComponent } from './administration/workbasket/master/list/workbasket-list.component';
import { WorkbasketDetailsComponent } from './administration/workbasket/details/workbasket-details.component';
import { MasterAndDetailComponent } from './shared/master-and-detail/master-and-detail.component';
import { NoAccessComponent } from './administration/workbasket/details/noAccess/no-access.component';
import { ClassificationListComponent } from './administration/classification/master/list/classification-list.component';
import { ClassificationDetailsComponent } from 'app/administration/classification/details/classification-details.component';
import { DomainGuard } from 'app/guards/domain-guard';

const appRoutes: Routes = [
    {
        path: 'administration/workbaskets',
        component: MasterAndDetailComponent,
        canActivate: [DomainGuard],
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
                path: 'new-classification/:id',
                component: WorkbasketDetailsComponent,
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
        path: 'administration/classifications',
        component: MasterAndDetailComponent,
        canActivate: [DomainGuard],
        children: [
            {
                path: '',
                component: ClassificationListComponent,
                outlet: 'master'
            },
            {
                path: ':id',
                component: ClassificationDetailsComponent,
                outlet: 'detail'
            }
        ]
    },
    {
        path: '',
        redirectTo: 'administration/workbaskets',
        pathMatch: 'full'
    },
    {
        path: 'administration',
        redirectTo: 'administration/workbaskets',
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
