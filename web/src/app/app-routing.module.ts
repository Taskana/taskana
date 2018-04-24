import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';

const appRoutes: Routes = [
    {
        path: 'administration',
        loadChildren: './administration/administration.module#AdministrationModule',
    },
    {
        path: 'monitor',
        loadChildren: './monitor/monitor.module#MonitorModule',
    },
    {
        path: 'workplace',
        loadChildren: './workplace/workplace.module#WorkplaceModule'
    },
    {
        path: '',
        redirectTo: 'workplace',
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
