import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { Component, DebugElement, Input } from '@angular/core';
import { WorkbasketDistributionTargetsComponent } from './workbasket-distribution-targets.component';
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
import { engineConfigurationMock, workbasketReadStateMock } from '../../../shared/store/mock-data/mock-store';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { Side } from '../../models/workbasket-distribution-enums';

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

const domainServiceSpy: Partial<DomainService> = {
  getSelectedDomainValue: jest.fn().mockReturnValue(of(null)),
  getSelectedDomain: jest.fn().mockReturnValue(of('A')),
  getDomains: jest.fn().mockReturnValue(of(null))
};

const workbasketServiceSpy: Partial<WorkbasketService> = {
  getWorkBasketsSummary: jest.fn().mockReturnValue(of(null)),
  getWorkBasketsDistributionTargets: jest.fn().mockReturnValue(of(null))
};

const notificationsServiceSpy: Partial<NotificationService> = {
  showSuccess: jest.fn().mockReturnValue(true)
};
const requestInProgressServiceSpy: Partial<RequestInProgressService> = {
  setRequestInProgress: jest.fn().mockReturnValue(of(null))
};

describe('WorkbasketDistributionTargetsComponent', () => {
  let fixture: ComponentFixture<WorkbasketDistributionTargetsComponent>;
  let debugElement: DebugElement;
  let component: WorkbasketDistributionTargetsComponent;
  let store: Store;
  let actions$: Observable<any>;

  beforeEach(
    waitForAsync(() => {
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
          { provide: WorkbasketService, useValue: workbasketServiceSpy },
          { provide: NotificationService, useValue: notificationsServiceSpy },
          { provide: ActivatedRoute, useValue: activatedRouteMock },
          { provide: RequestInProgressService, useValue: requestInProgressServiceSpy },
          { provide: DomainService, useValue: domainServiceSpy }
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
      fixture.detectChanges();
    })
  );

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
});
