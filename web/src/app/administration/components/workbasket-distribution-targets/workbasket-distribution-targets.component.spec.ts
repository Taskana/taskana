import { SimpleChange } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';

import { WorkbasketSummaryRepresentation } from 'app/shared/models/workbasket-summary-representation';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { ICONTYPES } from 'app/shared/models/icon-types';
import { Links } from 'app/shared/models/links';
import { Filter } from 'app/shared/models/filter';
import { Workbasket } from 'app/shared/models/workbasket';
import { WorkbasketDistributionTargets } from 'app/shared/models/workbasket-distribution-targets';

import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { SavingWorkbasketService } from 'app/administration/services/saving-workbaskets.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';

import { LinksWorkbasketSummary } from 'app/shared/models/links-workbasket-summary';
import { configureTests } from 'app/app.test.configuration';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { WorkbasketDistributionTargetsComponent, Side } from './workbasket-distribution-targets.component';
import { WorkbasketDualListComponent } from '../workbasket-dual-list/workbasket-dual-list.component';
import { NotificationService } from '../../../shared/services/notifications/notification.service';

describe('WorkbasketDistributionTargetsComponent', () => {
  let component: WorkbasketDistributionTargetsComponent;
  let fixture: ComponentFixture<WorkbasketDistributionTargetsComponent>;
  let workbasketService;
  const workbasket = new Workbasket('1', '', '', '', ICONTYPES.TOPIC, '', '', '', '', '', '', '', '', '', '', '', '',
    new Links({ href: 'someurl' }, { href: 'someurl' }, { href: 'someurl' }));

  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        imports: [AngularSvgIconModule, HttpClientModule, InfiniteScrollModule],
        declarations: [WorkbasketDistributionTargetsComponent, WorkbasketDualListComponent],
        providers: [WorkbasketService, NotificationService, SavingWorkbasketService, RequestInProgressService,
        ]
      });
    };
    configureTests(configure).then(testBed => {
      fixture = TestBed.createComponent(WorkbasketDistributionTargetsComponent);
      component = fixture.componentInstance;
      component.workbasket = workbasket;
      workbasketService = TestBed.get(WorkbasketService);
      spyOn(workbasketService, 'getWorkBasketsSummary').and.callFake(() => of(new WorkbasketSummaryRepresentation(
        new Array<WorkbasketSummary>(
          new WorkbasketSummary('id1', '', '', '', '', '', '', '', '', '', '', '', false, new Links({ href: 'someurl' })),
          new WorkbasketSummary('id2', '', '', '', '', '', '', '', '', '', '', '', false, new Links({ href: 'someurl' })),
          new WorkbasketSummary('id3', '', '', '', '', '', '', '', '', '', '', '', false, new Links({ href: 'someurl' }))
        ),
        new LinksWorkbasketSummary({ href: 'someurl' })
      )));
      spyOn(workbasketService, 'getWorkBasketsDistributionTargets').and.callFake(() => of(new WorkbasketDistributionTargets(
        new Array<WorkbasketSummary>(
          new WorkbasketSummary('id2', '', '', '', '', '', '', '', '', '', '', '', false, new Links({ href: 'someurl' }))
        ),
        new LinksWorkbasketSummary({ href: 'someurl' })
      )));
      component.ngOnChanges({
        active: new SimpleChange(undefined, 'distributionTargets', true)
      });
      fixture.detectChanges();
      done();
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should clone distribution target selected on init', () => {
    expect(component.distributionTargetsClone).toBeDefined();
  });

  it('should clone distribution target left and distribution target right lists on init', () => {
    expect(component.distributionTargetsLeft).toBeDefined();
    expect(component.distributionTargetsRight).toBeDefined();
  });

  it('should have two list with differents elements onInit', () => {
    let repeteadElemens = false;
    expect(component.distributionTargetsLeft.length).toBe(2);
    expect(component.distributionTargetsRight.length).toBe(1);
    component.distributionTargetsLeft.forEach(leftElement => {
      component.distributionTargetsRight.forEach(rightElement => {
        if (leftElement.workbasketId === rightElement.workbasketId) { repeteadElemens = true; }
      });
    });
    expect(repeteadElemens).toBeFalsy();
  });

  it('should filter left list and keep selected elements as selected', () => {
    component.performFilter({
      filterBy: new Filter({
        name: 'someName', owner: 'someOwner', description: 'someDescription', key: 'someKey'
      }),
      side: Side.LEFT
    });
    component.distributionTargetsLeft = new Array<WorkbasketSummary>(
      new WorkbasketSummary('id1', '', '', '', '', '', '', '', '', '', '', '', false, new Links({ href: 'someurl' }))
    );
    expect(component.distributionTargetsLeft.length).toBe(1);
    expect(component.distributionTargetsLeft[0].workbasketId).toBe('id1');
    expect(component.distributionTargetsRight.length).toBe(1);
    expect(component.distributionTargetsRight[0].workbasketId).toBe('id2');
  });

  it('should reset distribution target and distribution target selected on reset', () => {
    component.distributionTargetsLeft.push(
      new WorkbasketSummary('id4', '', '', '', '', '', '', '', '', '', '', '', false, new Links({ href: 'someurl' }))
    );
    component.distributionTargetsRight.push(
      new WorkbasketSummary('id5', '', '', '', '', '', '', '', '', '', '', '', false, new Links({ href: 'someurl' }))
    );

    expect(component.distributionTargetsLeft.length).toBe(3);
    expect(component.distributionTargetsRight.length).toBe(2);

    component.onClear();
    fixture.detectChanges();
    expect(component.distributionTargetsLeft.length).toBe(2);
    expect(component.distributionTargetsRight.length).toBe(1);
  });

  it('should save distribution targets selected and update Clone objects.', () => {
    expect(component.distributionTargetsSelected.length).toBe(1);
    expect(component.distributionTargetsSelectedClone.length).toBe(1);
    spyOn(workbasketService, 'updateWorkBasketsDistributionTargets').and.callFake(() => of(new WorkbasketDistributionTargets(
      new Array<WorkbasketSummary>(
        new WorkbasketSummary('id2', '', '', '', '', '', '', '', '', '', '', '', false, new Links({ href: 'someurl' })),
        new WorkbasketSummary('id1', '', '', '', '', '', '', '', '', '', '', '', false, new Links({ href: 'someurl' }))
      ),
      new LinksWorkbasketSummary({ href: 'someurl' })
    )));
    component.onSave();
    fixture.detectChanges();
    expect(component.distributionTargetsSelected.length).toBe(2);
    expect(component.distributionTargetsSelectedClone.length).toBe(2);
    expect(component.distributionTargetsLeft.length).toBe(1);
  });
});
