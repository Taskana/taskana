import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, DebugElement, Input } from '@angular/core';
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
  @Input() side: Side;
  @Input() header: string;
  @Input() component: 'availableDistributionTargets';
  @Input() allSelected;
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
    // mock-data has 8 entries, array should be filtered by selected distribution targets
    expect(component.availableDistributionTargets).toHaveLength(5);
    expect(component.availableDistributionTargetsUndoClone).toHaveLength(5);
    expect(component.availableDistributionTargetsFilterClone).toHaveLength(5);

    // mock-data has 3 entries
    expect(component.selectedDistributionTargets).toHaveLength(3);
    expect(component.selectedDistributionTargetsUndoClone).toHaveLength(3);
    expect(component.selectedDistributionTargetsFilterClone).toHaveLength(3);
  });

  it('should move distribution targets to selected list', () => {
    component.availableDistributionTargets[0]['selected'] = true; // select first item in available array
    const removeSelectedItems = jest.spyOn(component, 'removeSelectedItems');
    component.moveDistributionTargets(Side.AVAILABLE);

    expect(component.selectedDistributionTargets).toHaveLength(4); // mock-data only has 3
    expect(component.selectedDistributionTargetsFilterClone).toHaveLength(4);
    expect(removeSelectedItems).toHaveBeenCalled();
  });

  it('should move distribution targets to available list', () => {
    component.selectedDistributionTargets[0]['selected'] = true; // select first item in available array
    const removeSelectedItems = jest.spyOn(component, 'removeSelectedItems');
    component.moveDistributionTargets(Side.SELECTED);

    expect(component.availableDistributionTargets).toHaveLength(6); // mock-data has 5
    expect(component.availableDistributionTargetsFilterClone).toHaveLength(6);
    expect(removeSelectedItems).toHaveBeenCalled();
  });

  it('should set selectAll checkboxes to false when moving a workbasket', () => {
    component.selectAllRight = true;
    component.moveDistributionTargets(Side.SELECTED);
    expect(component.selectAllRight).toBeFalsy();

    component.selectAllLeft = true;
    component.moveDistributionTargets(Side.AVAILABLE);
    expect(component.selectAllLeft).toBeFalsy();
  });

  it('should call unselectItems() when moving a workbasket', () => {
    const unselectItems = jest.spyOn(component, 'unselectItems');

    [Side.SELECTED, Side.AVAILABLE].forEach((side) => {
      component.moveDistributionTargets(side);
      expect(unselectItems).toHaveBeenCalled();
    });
  });

  it('should reset distribution targets to last state when undo is called', () => {
    component.availableDistributionTargets[0]['selected'] = true; // select first item in available array

    component.moveDistributionTargets(Side.AVAILABLE);
    expect(component.selectedDistributionTargets).toHaveLength(4); // mock-data only has 3

    component.onClear();
    expect(component.selectedDistributionTargets).toHaveLength(3);
    expect(component.selectedDistributionTargetsFilterClone).toHaveLength(3);
  });

  it('should call performFilter when filter value from store is obtained', () => {
    const performFilter = jest.spyOn(component, 'performFilter');
    component.ngOnInit();
    expect(performFilter).toHaveBeenCalledTimes(2);
  });
});
