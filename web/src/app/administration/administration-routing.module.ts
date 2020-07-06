import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DomainGuard } from 'app/shared/guards/domain.guard';
import { AccessItemsManagementComponent } from './components/access-items-management/access-items-management.component';
import { ClassificationOverviewComponent } from './components/classification-overview/classification-overview.component';
import { WorkbasketOverviewComponent } from './components/workbasket-overview/workbasket-overview.component';

const routes: Routes = [
  {
    path: 'workbaskets',
    component: WorkbasketOverviewComponent,
    canActivate: [DomainGuard],
    children: [
      {
        path: '',
        component: WorkbasketOverviewComponent,
        outlet: 'master'
      },
      {
        path: ':id',
        component: WorkbasketOverviewComponent,
        outlet: 'detail'
      },
      {
        path: '**',
        redirectTo: ''
      }
    ]
  },
  {
    path: 'classifications',
    component: ClassificationOverviewComponent,
    canActivate: [DomainGuard],
    children: [
      {
        path: '',
        component: ClassificationOverviewComponent,
        outlet: 'master'
      },
      {
        path: ':id',
        component: ClassificationOverviewComponent,
        outlet: 'detail'
      },
      {
        path: '**',
        redirectTo: ''
      }
    ]
  },
  {
    path: 'access-items-management',
    component: AccessItemsManagementComponent,
    canActivate: [DomainGuard],
    children: [
      {
        path: '**',
        redirectTo: ''
      }
    ]
  },
  {
    path: '',
    redirectTo: 'workbaskets',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: 'workbaskets'
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdministrationRoutingModule {
}
