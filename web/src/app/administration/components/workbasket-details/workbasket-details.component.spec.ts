import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router, Routes } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { of } from 'rxjs';

import { Workbasket } from 'app/shared/models/workbasket';
import { WorkbasketSummaryRepresentation } from 'app/shared/models/workbasket-summary-representation';
import { WorkbasketAccessItemsRepresentation } from 'app/shared/models/workbasket-access-items-representation';
import { ICONTYPES } from 'app/shared/models/icon-types';
import { Links } from 'app/shared/models/links';

import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { MasterAndDetailService } from 'app/shared/services/master-and-detail/master-and-detail.service';
import { SavingWorkbasketService } from 'app/administration/services/saving-workbaskets.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { configureTests } from 'app/app.test.configuration';

import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { NgxsModule } from '@ngxs/store';
import { WorkbasketDetailsComponent } from './workbasket-details.component';
import { WorkbasketInformationComponent } from '../workbasket-information/workbasket-information.component';
import { WorkbasketAccessItemsComponent } from '../workbasket-access-items/workbasket-access-items.component';
import { WorkbasketDistributionTargetsComponent } from '../workbasket-distribution-targets/workbasket-distribution-targets.component';
import { WorkbasketDualListComponent } from '../workbasket-dual-list/workbasket-dual-list.component';
import { NotificationService } from '../../../shared/services/notifications/notification.service';

@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
export class DummyDetailComponent {}

function createWorkbasket(
  workbasketId?,
  created?,
  key?,
  domain?,
  type?,
  modified?,
  name?,
  description?,
  owner?,
  custom1?,
  custom2?,
  custom3?,
  custom4?,
  orgLevel1?,
  orgLevel2?,
  orgLevel3?,
  orgLevel4?,
  _links?: Links,
  markedForDeletion?: boolean
) {
  const workbasket: Workbasket = {
    workbasketId,
    created,
    key,
    domain,
    type,
    modified,
    name,
    description,
    owner,
    custom1,
    custom2,
    custom3,
    custom4,
    orgLevel1,
    orgLevel2,
    orgLevel3,
    orgLevel4,
    markedForDeletion,
    _links
  };
  return workbasket;
}

describe('WorkbasketDetailsComponent', () => {
  let component: WorkbasketDetailsComponent;
  let fixture: ComponentFixture<WorkbasketDetailsComponent>;
  let debugElement;
  let masterAndDetailService;
  let workbasketService;
  let router;
  const workbasket = createWorkbasket(
    '1',
    '',
    '',
    '',
    ICONTYPES.TOPIC,
    '',
    '',
    '',
    '',
    '',
    '',
    '',
    '',
    '',
    '',
    '',
    '',
    {}
  );

  const workbasketSummaryRepresentation: WorkbasketSummaryRepresentation = { workbaskets: [], _links: {}, page: {} };

  const workbasketAccessItemsRepresentation: WorkbasketAccessItemsRepresentation = { accessItems: [], _links: {} };
  const routes: Routes = [{ path: '*', component: DummyDetailComponent }];

  beforeEach((done) => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        imports: [
          RouterTestingModule.withRoutes(routes),
          FormsModule,
          AngularSvgIconModule,
          HttpClientModule,
          ReactiveFormsModule,
          InfiniteScrollModule,
          NgxsModule.forRoot()
        ],
        declarations: [
          WorkbasketDetailsComponent,
          WorkbasketInformationComponent,
          WorkbasketAccessItemsComponent,
          WorkbasketDistributionTargetsComponent,
          WorkbasketDualListComponent,
          DummyDetailComponent
        ],
        providers: [
          WorkbasketService,
          MasterAndDetailService,
          RequestInProgressService,
          NotificationService,
          SavingWorkbasketService,
          ImportExportService
        ]
      });
    };
    configureTests(configure).then((testBed) => {
      fixture = TestBed.createComponent(WorkbasketDetailsComponent);
      component = fixture.componentInstance;
      debugElement = fixture.debugElement.nativeElement;
      router = TestBed.get(Router);
      fixture.detectChanges();
      masterAndDetailService = TestBed.get(MasterAndDetailService);
      workbasketService = TestBed.get(WorkbasketService);
      spyOn(masterAndDetailService, 'getShowDetail').and.callFake(() => of(true));
      spyOn(workbasketService, 'getSelectedWorkBasket').and.callFake(() => of('id1'));
      spyOn(workbasketService, 'getWorkBasketsSummary').and.callFake(() => of(workbasketSummaryRepresentation));

      spyOn(workbasketService, 'getWorkBasket').and.callFake(() => of(workbasket));
      spyOn(workbasketService, 'getWorkBasketAccessItems').and.callFake(() => of(workbasketAccessItemsRepresentation));
      spyOn(workbasketService, 'getWorkBasketsDistributionTargets').and.callFake(() =>
        of(workbasketSummaryRepresentation)
      );
      done();
    });
  });

  afterEach(() => {
    fixture.destroy();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
