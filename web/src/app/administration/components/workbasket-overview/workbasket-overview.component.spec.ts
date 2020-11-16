import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketOverviewComponent } from './workbasket-overview.component';
import { Component, DebugElement } from '@angular/core';
import { Actions, NgxsModule, ofActionDispatched, Store } from '@ngxs/store';
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
import { CreateWorkbasket, SelectWorkbasket } from '../../../shared/store/workbasket-store/workbasket.actions';
import { StartupService } from '../../../shared/services/startup/startup.service';
import { TaskanaEngineService } from '../../../shared/services/taskana-engine/taskana-engine.service';
import { WindowRefService } from '../../../shared/services/window/window.service';
import { workbasketReadStateMock } from '../../../shared/store/mock-data/mock-store';

const showDialogFn = jest.fn().mockReturnValue(true);
const NotificationServiceSpy = jest.fn().mockImplementation(
  (): Partial<NotificationService> => ({
    triggerError: showDialogFn,
    showToast: showDialogFn
  })
);

const mockActivatedRoute = {
  firstChild: {
    params: of({
      id: 'new-workbasket'
    })
  }
};

const mockActivatedRouteAlternative = {
  firstChild: {
    params: of({
      id: '101'
    })
  }
};

const mockActivatedRouteNoParams = {
  url: of([{ path: 'workbaskets' }])
};

@Component({ selector: 'taskana-administration-workbasket-list', template: '' })
class WorkbasketListStub {}

@Component({ selector: 'taskana-administration-workbasket-details', template: '' })
class WorkbasketDetailsStub {}

@Component({ selector: 'svg-icon', template: '' })
class SvgIconStub {}

describe('WorkbasketOverviewComponent', () => {
  let fixture: ComponentFixture<WorkbasketOverviewComponent>;
  let debugElement: DebugElement;
  let component: WorkbasketOverviewComponent;
  let store: Store;
  let actions$: Observable<any>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), NgxsModule.forRoot([WorkbasketState])],
      declarations: [WorkbasketOverviewComponent, WorkbasketListStub, WorkbasketDetailsStub, SvgIconStub],
      providers: [
        WorkbasketService,
        { provide: NotificationService, useClass: NotificationServiceSpy },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        DomainService,
        RequestInProgressService,
        SelectedRouteService,
        StartupService,
        TaskanaEngineService,
        WindowRefService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(WorkbasketOverviewComponent);
    debugElement = fixture.debugElement;
    component = fixture.debugElement.componentInstance;
    store = TestBed.inject(Store);
    actions$ = TestBed.inject(Actions);
    fixture.detectChanges();
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

  it('should display details when params id exists', async(() => {
    let actionDispatched = false;
    actions$.pipe(ofActionDispatched(CreateWorkbasket)).subscribe(() => (actionDispatched = true));
    component.ngOnInit();
    expect(actionDispatched).toBe(true);
    expect(component.routerParams.id).toMatch('new-workbasket');
    expect(component.showDetail).toBeTruthy();
    expect(debugElement.nativeElement.querySelector('taskana-administration-workbasket-details')).toBeTruthy();
  }));
});

describe('WorkbasketOverviewComponent Alternative Params ID', () => {
  let fixture: ComponentFixture<WorkbasketOverviewComponent>;
  let component: WorkbasketOverviewComponent;
  let store: Store;
  let actions$: Observable<any>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), NgxsModule.forRoot([WorkbasketState])],
      declarations: [WorkbasketOverviewComponent, WorkbasketListStub, WorkbasketDetailsStub, SvgIconStub],
      providers: [
        WorkbasketService,
        { provide: NotificationService, useClass: NotificationServiceSpy },
        { provide: ActivatedRoute, useValue: mockActivatedRouteAlternative },
        DomainService,
        RequestInProgressService,
        SelectedRouteService,
        StartupService,
        TaskanaEngineService,
        WindowRefService
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(WorkbasketOverviewComponent);
    component = fixture.debugElement.componentInstance;
    store = TestBed.inject(Store);
    actions$ = TestBed.inject(Actions);
    fixture.detectChanges();
  }));

  it('should display details when params id exists', async(() => {
    expect(component.routerParams.id).toBeTruthy();
    let actionDispatched = false;
    actions$.pipe(ofActionDispatched(SelectWorkbasket)).subscribe(() => (actionDispatched = true));
    component.ngOnInit();
    expect(actionDispatched).toBe(true);
  }));
});

describe('WorkbasketOverviewComponent No Params', () => {
  let fixture: ComponentFixture<WorkbasketOverviewComponent>;
  let component: WorkbasketOverviewComponent;
  let store: Store;
  let actions$: Observable<any>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), NgxsModule.forRoot([WorkbasketState])],
      declarations: [WorkbasketOverviewComponent, WorkbasketListStub, WorkbasketDetailsStub, SvgIconStub],
      providers: [
        WorkbasketService,
        { provide: NotificationService, useClass: NotificationServiceSpy },
        { provide: ActivatedRoute, useValue: mockActivatedRouteNoParams },
        DomainService,
        RequestInProgressService,
        SelectedRouteService,
        StartupService,
        TaskanaEngineService,
        WindowRefService
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(WorkbasketOverviewComponent);
    component = fixture.debugElement.componentInstance;
    fixture.detectChanges();
    store = TestBed.inject(Store);
    actions$ = TestBed.inject(Actions);

    store.reset({
      ...store.snapshot(),
      workbasket: workbasketReadStateMock
    });
  }));

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should dispatch SelectWorkbasket action when route contains workbasket', async () => {
    let actionDispatched = false;
    actions$.pipe(ofActionDispatched(SelectWorkbasket)).subscribe(() => (actionDispatched = true));
    component.ngOnInit();
    expect(actionDispatched).toBe(true);
  });
});
