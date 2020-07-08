import { SimpleChange } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';

import { WorkbasketSummaryRepresentation } from 'app/shared/models/workbasket-summary-representation';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { ICONTYPES } from 'app/shared/models/icon-types';
import { Links } from 'app/shared/models/links';
import { Workbasket } from 'app/shared/models/workbasket';
import { WorkbasketDistributionTargets } from 'app/shared/models/workbasket-distribution-targets';

import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { SavingWorkbasketService } from 'app/administration/services/saving-workbaskets.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';

import { LinksWorkbasketSummary } from 'app/shared/models/links-workbasket-summary';
import { configureTests } from 'app/app.test.configuration';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { NgxsModule, Store } from '@ngxs/store';
import { WorkbasketDistributionTargetsComponent, Side } from './workbasket-distribution-targets.component';
import { WorkbasketDualListComponent } from '../workbasket-dual-list/workbasket-dual-list.component';
import { NotificationService } from '../../../shared/services/notifications/notification.service'; import { ClassificationSelectors } from '../../../shared/store/classification-store/classification.selectors';
import { WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';

describe('WorkbasketDistributionTargetsComponent', () => {
  let component: WorkbasketDistributionTargetsComponent;
  let fixture: ComponentFixture<WorkbasketDistributionTargetsComponent>;
  let workbasketService;
  const workbasket = createWorkbasket('1', '', '', '', ICONTYPES.TOPIC, '', '', '', '', '', '', '', '', '', '', '', '',
    {});

  function createWorkbasket(workbasketId?, created?, key?, domain?, type?, modified?, name?, description?,
    owner?, custom1?, custom2?, custom3?, custom4?, orgLevel1?, orgLevel2?, orgLevel3?, orgLevel4?,
    _links?: Links, markedForDeletion?: boolean): Workbasket {
    return {
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
  }

  function createWorkbasketSummary(workbasketId, key, name, domain, type, description, owner, custom1, custom2, custom3, custom4) {
    const workbasketSummary: WorkbasketSummary = {
      workbasketId,
      key,
      name,
      domain,
      type,
      description,
      owner,
      custom1,
      custom2,
      custom3,
      custom4
    };
    return workbasketSummary;
  }

  const workbasketSummaryResource: WorkbasketSummaryRepresentation = {
    workbaskets: [
      createWorkbasketSummary('1', 'key1', 'NAME1', '', 'PERSONAL',
        'description 1', 'owner1', '', '', '', ''),
      createWorkbasketSummary('2', 'key2', 'NAME2', '', 'PERSONAL',
        'description 2', 'owner2', '', '', '', ''),
    ],
    _links: new LinksWorkbasketSummary({ href: 'url' }),
    page: {}
  };

  const workbasketDistributionTargets: WorkbasketDistributionTargets = {
    distributionTargets: [createWorkbasketSummary('id2', '', '', '', '', '', '', '', '', '', '')],
    _links: {}
  };

  const storeSpy: jasmine.SpyObj<Store> = jasmine.createSpyObj('Store', ['select', 'dispatch']);

  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        imports: [AngularSvgIconModule, HttpClientModule, InfiniteScrollModule, NgxsModule.forRoot()],
        declarations: [WorkbasketDistributionTargetsComponent, WorkbasketDualListComponent],
        providers: [WorkbasketService, NotificationService, SavingWorkbasketService, RequestInProgressService,
          { provide: Store, useValue: storeSpy }]
      });
    };
    configureTests(configure).then(testBed => {
      storeSpy.select.and.callFake(selector => {
        switch (selector) {
          case WorkbasketSelectors.workbasketDistributionTargets:
            return of(['distributionTargets', '_links']);
          default:
            return of();
        }
      });
      fixture = TestBed.createComponent(WorkbasketDistributionTargetsComponent);
      component = fixture.componentInstance;
      component.workbasket = workbasket;
      workbasketService = TestBed.get(WorkbasketService);
      spyOn(workbasketService, 'getWorkBasketsSummary').and.callFake(() => of(workbasketSummaryResource));
      spyOn(workbasketService, 'getWorkBasketsDistributionTargets').and.callFake(() => of(workbasketDistributionTargets));
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

  it('should have two list with same elements onInit', () => {
    let repeteadElemens = false;
    expect(component.distributionTargetsLeft.length).toBe(2);
    expect(component.distributionTargetsRight.length).toBe(2);
    component.distributionTargetsLeft.forEach(leftElement => {
      component.distributionTargetsRight.forEach(rightElement => {
        if (leftElement.workbasketId === rightElement.workbasketId) { repeteadElemens = true; }
      });
    });
    expect(repeteadElemens).toBeTruthy();
  });

  it('should reset distribution target and distribution target selected on reset', () => {
    component.distributionTargetsLeft.push(
      createWorkbasketSummary('id4', '', '', '', '', '', '', '', '', '', '')
    );
    component.distributionTargetsRight.push(
      createWorkbasketSummary('id5', '', '', '', '', '', '', '', '', '', '')
    );

    expect(component.distributionTargetsLeft.length).toBe(3);
    expect(component.distributionTargetsRight.length).toBe(3);

    component.onClear();
    fixture.detectChanges();
    expect(component.distributionTargetsLeft.length).toBe(2);
    expect(component.distributionTargetsRight.length).toBe(0);
  });
});
