import { async, ComponentFixture, inject, TestBed } from '@angular/core/testing';
import { WorkbasketOverviewComponent } from './workbasket-overview.component';
import { CUSTOM_ELEMENTS_SCHEMA, DebugElement } from '@angular/core';
import { Actions, NgxsModule, Store } from '@ngxs/store';
import { Observable, of } from 'rxjs';
import { WorkbasketState } from '../../../shared/store/workbasket-store/workbasket.state';
import { WorkbasketService } from '../../../shared/services/workbasket/workbasket.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { RouterTestingModule } from '@angular/router/testing';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { SelectedRouteService } from '../../../shared/services/selected-route/selected-route';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { ActivatedRoute } from '@angular/router';

const showDialogFn = jest.fn().mockReturnValue(true);
const NotificationServiceSpy = jest.fn().mockImplementation(
  (): Partial<NotificationService> => ({
    triggerError: showDialogFn,
    showToast: showDialogFn
  })
);
const mockActivatedRoute = {
  firstChild: {
    params: {
      id: '123'
    }
  }
};

describe('WorkbasketOverviewComponent', () => {
  let fixture: ComponentFixture<WorkbasketOverviewComponent>;
  let debugElement: DebugElement;
  let component: WorkbasketOverviewComponent;
  let store: Store;
  let action$: Observable<any>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), NgxsModule.forRoot([WorkbasketState])],
      declarations: [WorkbasketOverviewComponent],
      providers: [
        WorkbasketService,
        { provide: NotificationService, useClass: NotificationServiceSpy },
        { provide: ActivatedRoute, useValue: of(mockActivatedRoute) },
        DomainService,
        RequestInProgressService,
        SelectedRouteService
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
    fixture = TestBed.createComponent(WorkbasketOverviewComponent);
    debugElement = fixture.debugElement;
    component = fixture.debugElement.componentInstance;
    store = TestBed.inject(Store);
    action$ = TestBed.inject(Actions);
  }));

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should always displays workbasket-list', () => {
    expect(debugElement.nativeElement.querySelector('taskana-administration-workbasket-list')).toBeTruthy();
  });

  it('should display workbasket-details correctly', () => {
    component.showDetail = false;
    fixture.detectChanges();
    expect(debugElement.nativeElement.querySelector('taskana-administration-workbasket-details')).toBeNull();

    component.showDetail = true;
    fixture.detectChanges();
    expect(debugElement.nativeElement.querySelector('taskana-administration-workbasket-details')).toBeTruthy();
  });
});
