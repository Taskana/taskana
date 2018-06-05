import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';

import { BusinessAdminGuard } from 'app/guards/business-admin-guard';
import { MonitorGuard } from 'app/guards/monitor-guard';
import { UserGuard } from 'app/guards/user-guard';
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
