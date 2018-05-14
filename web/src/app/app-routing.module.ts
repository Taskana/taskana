import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { BusinessAdminGuard } from 'app/guards/business-admin-guard';
import { MonitorGuard } from 'app/guards/monitor-guard';

const appRoutes: Routes = [
    {
        canActivate: [BusinessAdminGuard],
        path: 'administration',
        loadChildren: './administration/administration.module#AdministrationModule',
    },
    {
        canActivate: [MonitorGuard],
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
