import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { BusinessAdminGuard } from './guards/business-admin.guard';
import { MonitorGuard } from './guards/monitor.guard';
import { UserGuard } from './guards/user.guard';
import { HistoryGuard } from './guards/history.guard';
import { NoAccessComponent } from './components/no-access/no-access.component';

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
        canActivate: [UserGuard],
        path: 'workplace',
        loadChildren: './workplace/workplace.module#WorkplaceModule'
    },
    {
        canActivate: [HistoryGuard],
        path: 'history',
        loadChildren: './history/history.module#HistoryModule'
    },
    {
        path: 'no-role',
        component: NoAccessComponent
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
