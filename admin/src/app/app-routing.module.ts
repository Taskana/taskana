import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { WorkbasketListComponent } from './workbasket/list/workbasket-list.component';
import { WorkbasketadministrationComponent } from './workbasketadministration/workbasketadministration.component';
import { CategoriesadministrationComponent } from './categoriesadministration/categoriesadministration.component';
import { MasterAndDetailComponent } from './shared/masterAndDetail/master-and-detail.component';

const appRoutes: Routes = [
    {   path: 'workbaskets',
        component: MasterAndDetailComponent,
        children: [
            {
                path: '',
                component: WorkbasketListComponent,
                outlet: 'master'
            },
            {
                path: ':id',
                component: WorkbasketadministrationComponent,
                outlet: 'detail'
            }
        ]
    },
    {   path: 'clasifications',
        component: MasterAndDetailComponent,
        children: [
            {
                path: '',
                component: CategoriesadministrationComponent,
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
