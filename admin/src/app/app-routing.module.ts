import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { WorkbasketadministrationComponent } from './workbasketadministration/workbasketadministration.component';
import { CategoriesadministrationComponent } from './categoriesadministration/categoriesadministration.component';

const appRoutes: Routes = [
    {
        path: 'workbaskets',
        component: WorkbasketadministrationComponent
    },
    {
        path: 'workbaskets/:id',
        component: WorkbasketadministrationComponent
    },
    {
        path: 'categories',
        component: CategoriesadministrationComponent
    },
    {
        path: '',
        redirectTo: 'workbaskets',
        pathMatch: 'full'
    },
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
