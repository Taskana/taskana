import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router, Routes } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { of } from 'rxjs';

import { Workbasket } from 'app/shared/models/workbasket';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { WorkbasketSummaryRepresentation } from 'app/shared/models/workbasket-summary-representation';
import { WorkbasketAccessItemsRepresentation } from 'app/shared/models/workbasket-access-items-representation';
import { ICONTYPES } from 'app/shared/models/icon-types';
import { Links } from 'app/shared/models/links';
import { WorkbasketAccessItems } from 'app/shared/models/workbasket-access-items';
import { LinksWorkbasketSummary } from 'app/shared/models/links-workbasket-summary';

import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { MasterAndDetailService } from 'app/shared/services/master-and-detail/master-and-detail.service';
import { SavingWorkbasketService } from 'app/administration/services/saving-workbaskets.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { configureTests } from 'app/app.test.configuration';

import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { ImportExportService } from 'app/administration/services/import-export.service';
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
export class DummyDetailComponent {
}

describe('WorkbasketDetailsComponent', () => {
  let component: WorkbasketDetailsComponent;
  let fixture: ComponentFixture<WorkbasketDetailsComponent>;
  let debugElement;
  let masterAndDetailService;
  let workbasketService;
  let router;
  const workbasket = new Workbasket('1', '', '', '', ICONTYPES.TOPIC, '', '', '', '', '', '', '', '', '', '', '', '',
    new Links({ href: 'someurl' }, { href: 'someurl' }, { href: 'someurl' }));

  const routes: Routes = [
    { path: '*', component: DummyDetailComponent }
  ];

  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        imports: [RouterTestingModule.withRoutes(routes), FormsModule, AngularSvgIconModule, HttpClientModule, ReactiveFormsModule,
          InfiniteScrollModule],
        declarations: [WorkbasketDetailsComponent, WorkbasketInformationComponent,
          WorkbasketAccessItemsComponent,
          WorkbasketDistributionTargetsComponent, WorkbasketDualListComponent, DummyDetailComponent],
        providers: [WorkbasketService, MasterAndDetailService, RequestInProgressService,
          NotificationService, SavingWorkbasketService, ImportExportService]
      });
    };
    configureTests(configure).then(testBed => {
      fixture = TestBed.createComponent(WorkbasketDetailsComponent);
      component = fixture.componentInstance;
      debugElement = fixture.debugElement.nativeElement;
      router = TestBed.get(Router);
      fixture.detectChanges();
      masterAndDetailService = TestBed.get(MasterAndDetailService);
      workbasketService = TestBed.get(WorkbasketService);
      spyOn(masterAndDetailService, 'getShowDetail').and.callFake(() => of(true));
      spyOn(workbasketService, 'getSelectedWorkBasket').and.callFake(() => of('id1'));
      spyOn(workbasketService, 'getWorkBasketsSummary').and.callFake(() => of(new WorkbasketSummaryRepresentation(
        new Array<WorkbasketSummary>(
          new WorkbasketSummary('id1', '', '', '', '', '', '', '', '', '', '', '',
            false, new Links({ href: 'someurl' }))
        ),
        new LinksWorkbasketSummary({ href: 'someurl' })
      )));

      spyOn(workbasketService, 'getWorkBasket').and.callFake(() => of(workbasket));
      spyOn(workbasketService, 'getWorkBasketAccessItems').and.callFake(() => of(new WorkbasketAccessItemsRepresentation(
        new Array<WorkbasketAccessItems>(), new Links({ href: 'url' })
      )));
      spyOn(workbasketService, 'getWorkBasketsDistributionTargets').and.callFake(() => of(new WorkbasketSummaryRepresentation(
        new Array<WorkbasketSummary>(), new LinksWorkbasketSummary({ href: 'url' })
      )));
      done();
    });
  });

  afterEach(() => {
    document.body.removeChild(debugElement);
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
