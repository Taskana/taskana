import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, DebugElement, EventEmitter, Input, Output } from '@angular/core';
import { Side, WorkbasketDistributionTargetsComponent } from './workbasket-distribution-targets.component';
import { WorkbasketSummary } from '../../../shared/models/workbasket-summary';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { Observable, of } from 'rxjs';
import { WorkbasketService } from '../../../shared/services/workbasket/workbasket.service';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { Actions, NgxsModule, Store } from '@ngxs/store';
import { WorkbasketState } from '../../../shared/store/workbasket-store/workbasket.state';
import { ActivatedRoute } from '@angular/router';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { MatDialogModule } from '@angular/material/dialog';
import {
  engineConfigurationMock,
  selectedWorkbasketMock,
  workbasketReadStateMock
} from '../../../shared/store/mock-data/mock-store';
import { WorkbasketQueryFilterParameter } from '../../../shared/models/workbasket-query-filter-parameter';
import { Pair } from '../../../shared/models/pair';
import { DomainService } from '../../../shared/services/domain/domain.service';

const routeParamsMock = { id: 'workbasket' };
const activatedRouteMock = {
  firstChild: {
    params: of(routeParamsMock)
  }
};
@Component({ selector: 'taskana-administration-workbasket-distribution-targets-list', template: '' })
class WorkbasketDistributionTargetsListStub {
  @Input() distributionTargets: WorkbasketSummary[];
  @Input() distributionTargetsSelected: WorkbasketSummary[];
  @Output() performDualListFilter = new EventEmitter<Pair<Side, WorkbasketQueryFilterParameter>>();
  @Input() requestInProgress = false;
  @Input() loadingItems? = false;
  @Input() side: Side;
  @Input() header: string;
  @Output() scrolling = new EventEmitter<Side>();
  @Input() allSelected;
  @Output() allSelectedChange = new EventEmitter<boolean>();
}

const domainServiceSpy = jest.fn().mockImplementation(
  (): Partial<DomainService> => ({
    getSelectedDomainValue: jest.fn().mockReturnValue(of()),
    getSelectedDomain: jest.fn().mockReturnValue(of('A')),
    getDomains: jest.fn().mockReturnValue(of())
  })
);

const workbasketServiceSpy = jest.fn().mockImplementation(
  (): Partial<WorkbasketService> => ({
    getWorkBasketsSummary: jest.fn().mockReturnValue(of()),
    getWorkBasketsDistributionTargets: jest.fn().mockReturnValue(of())
  })
);

const notificationsServiceSpy = jest.fn().mockImplementation(
  (): Partial<NotificationService> => ({
    showToast: jest.fn().mockReturnValue(true)
  })
);
const requestInProgressServiceSpy = jest.fn().mockImplementation(
  (): Partial<RequestInProgressService> => ({
    setRequestInProgress: jest.fn().mockReturnValue(of())
  })
);

describe('WorkbasketDistributionTargetsComponent', () => {
  let fixture: ComponentFixture<WorkbasketDistributionTargetsComponent>;
  let debugElement: DebugElement;
  let component: WorkbasketDistributionTargetsComponent;
  let store: Store;
  let actions$: Observable<any>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        MatIconModule,
        MatDialogModule,
        MatToolbarModule,
        MatButtonModule,
        NgxsModule.forRoot([WorkbasketState])
      ],
      declarations: [WorkbasketDistributionTargetsComponent, WorkbasketDistributionTargetsListStub],
      providers: [
        { provide: WorkbasketService, useClass: workbasketServiceSpy },
        { provide: NotificationService, useClass: notificationsServiceSpy },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: RequestInProgressService, useClass: requestInProgressServiceSpy },
        { provide: DomainService, useClass: domainServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(WorkbasketDistributionTargetsComponent);
    debugElement = fixture.debugElement;
    component = fixture.componentInstance;
    store = TestBed.inject(Store);
    actions$ = TestBed.inject(Actions);
    store.reset({
      ...store.snapshot(),
      engineConfiguration: engineConfigurationMock,
      workbasket: workbasketReadStateMock
    });
    component.workbasket = selectedWorkbasketMock;
    fixture.detectChanges();
  }));

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should display side-by-side view by default', () => {
    expect(component.sideBySide).toBe(true);
    expect(debugElement.nativeElement.querySelector('.distribution-targets-list__lists--side')).toBeTruthy();
  });

  it('should display single view when toggle view button is clicked', () => {
    const toggleViewButton = debugElement.nativeElement.querySelector('.distribution-targets-list__toggle-view-button');
    expect(toggleViewButton).toBeTruthy();
    toggleViewButton.click();
    fixture.detectChanges();
    expect(component.sideBySide).toBe(false);
    expect(debugElement.nativeElement.querySelector('.distribution-targets-list__lists--side')).toBeFalsy();
  });

  it('should get available and selected distribution targets', () => {
    component.getWorkbaskets();
    expect(component.availableDistributionTargets).toHaveLength(8); //mock-data has 8 entries
    expect(component.distributionTargetsSelected).toHaveLength(3); //mock-data has 3 entries
  });

  // TODO: was ist das für ein test?
  it('should emit filter model and side when performing filter', () => {
    const performDualListFilterSpy = jest.spyOn(component, 'performFilter');
    const filterModelMock: WorkbasketQueryFilterParameter = { domain: ['DOMAIN_A'] };

    component.performFilter({ left: Side.AVAILABLE, right: filterModelMock });

    expect(performDualListFilterSpy).toHaveBeenCalledWith({ right: filterModelMock, left: Side.AVAILABLE });
  });

  it('should move distribution targets to selected list', () => {
    component.availableDistributionTargets[0]['selected'] = true; // select first item in available array
    component.distributionTargetsLeft = component.distributionTargetsSelected;
    component.moveDistributionTargets(Side.AVAILABLE);
    expect(component.distributionTargetsSelected).toHaveLength(4); // mock-data only has 3
  });

  it('should reset distribution targets to last state when undo is called', () => {
    component.distributionTargetsClone = component.availableDistributionTargets;
    component.distributionTargetsSelectedClone = component.distributionTargetsSelected;
    component.availableDistributionTargets[0]['selected'] = true; // select first item in available array
    component.distributionTargetsLeft = component.distributionTargetsSelected;
    component.moveDistributionTargets(Side.AVAILABLE);
    expect(component.distributionTargetsSelected).toHaveLength(4); // mock-data only has 3

    component.onClear();
    expect(component.distributionTargetsSelected).toHaveLength(3);
  });
});
