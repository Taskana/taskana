import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { WorkbasketListComponent } from 'app/administration/workbasket/master/list/workbasket-list.component';
import { WorkbasketDetailsComponent } from 'app/administration/workbasket/details/workbasket-details.component';
import { MasterAndDetailComponent } from 'app/shared/master-and-detail/master-and-detail.component';
import { ClassificationListComponent } from 'app/administration/classification/master/list/classification-list.component';
import { ClassificationDetailsComponent } from 'app/administration/classification/details/classification-details.component';
import { DomainGuard } from 'app/guards/domain.guard';
import { AccessItemsManagementComponent } from './access-items-management/access-items-management.component';

const routes: Routes = [
  {
    path: 'workbaskets',
    component: MasterAndDetailComponent,
    canActivate: [DomainGuard],
    children: [
      {
        path: '',
        component: WorkbasketListComponent,
        outlet: 'master'
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
      },
      {
        path: '**',
        redirectTo: ''
      }
    ]
  },
  {
    path: 'classifications',
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
export class AdministrationRoutingModule {}
