import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketDetailsComponent } from './workbasket-details.component';
import { Component, DebugElement, Input } from '@angular/core';
import { Actions, NgxsModule, Store } from '@ngxs/store';
import { Observable, of } from 'rxjs';
import { Workbasket } from '../../../shared/models/workbasket';
import { ACTION } from '../../../shared/models/action';
import { WorkbasketState } from '../../../shared/store/workbasket-store/workbasket.state';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { ImportExportService } from '../../services/import-export.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { WorkbasketService } from '../../../shared/services/workbasket/workbasket.service';
import { RouterTestingModule } from '@angular/router/testing';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { SelectedRouteService } from '../../../shared/services/selected-route/selected-route';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { selectedWorkbasketMock, workbasketReadStateMock } from '../../../shared/store/mock-data/mock-store';
import { StartupService } from '../../../shared/services/startup/startup.service';
import { TaskanaEngineService } from '../../../shared/services/taskana-engine/taskana-engine.service';
import { WindowRefService } from '../../../shared/services/window/window.service';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbarModule } from '@angular/material/toolbar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CopyWorkbasket, CreateWorkbasket } from '../../../shared/store/workbasket-store/workbasket.actions';
import { take } from 'rxjs/operators';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({ selector: 'taskana-administration-workbasket-information', template: '<div>i</div>' })
class WorkbasketInformationStub {
  @Input() workbasket: Workbasket;
  @Input() action: ACTION;
}

@Component({ selector: 'taskana-administration-workbasket-access-items', template: '' })
class WorkbasketAccessItemsStub {
  @Input() workbasket: Workbasket;
  @Input() action: ACTION;
  @Input() active: string;
  @Input() expanded: boolean;
}

@Component({ selector: 'taskana-administration-workbasket-distribution-targets', template: '' })
class WorkbasketDistributionTargetsStub {
  @Input() workbasket: Workbasket;
  @Input() action: ACTION;
  @Input() active: string;
}

const domainServiceSpy: Partial<DomainService> = {
  getSelectedDomain: jest.fn().mockReturnValue(of('A')),
  getSelectedDomainValue: jest.fn().mockReturnValue(of()),
  getDomains: jest.fn().mockReturnValue(of())
};

export const workbasketCopyState = {
  selectedWorkbasket: selectedWorkbasketMock,
  action: ACTION.COPY
};

export const workbasketCreateState = {
  selectedWorkbasket: selectedWorkbasketMock,
  action: ACTION.CREATE
};

export const workbasketReadState = {
  selectedWorkbasket: selectedWorkbasketMock,
  action: ACTION.READ
};
describe('WorkbasketDetailsComponent', () => {
  let fixture: ComponentFixture<WorkbasketDetailsComponent>;
  let debugElement: DebugElement;
  let component: WorkbasketDetailsComponent;
  let store: Store;
  let actions$: Observable<any>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgxsModule.forRoot([WorkbasketState]),
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([]),
        MatSnackBarModule,
        MatDialogModule,
        MatIconModule,
        MatProgressBarModule,
        MatTabsModule,
        MatMenuModule,
        MatToolbarModule,
        MatTooltipModule,
        BrowserAnimationsModule
      ],
      declarations: [
        WorkbasketDetailsComponent,
        WorkbasketAccessItemsStub,
        WorkbasketDistributionTargetsStub,
        WorkbasketInformationStub
      ],
      providers: [
        { provide: DomainService, useValue: domainServiceSpy },
        ImportExportService,
        WorkbasketService,
        RequestInProgressService,
        SelectedRouteService,
        StartupService,
        TaskanaEngineService,
        WindowRefService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(WorkbasketDetailsComponent);
    debugElement = fixture.debugElement;
    component = fixture.debugElement.componentInstance;
    store = TestBed.inject(Store);
    actions$ = TestBed.inject(Actions);
    store.reset({
      ...store.snapshot(),
      workbasket: workbasketReadStateMock
    });
    fixture.detectChanges();
  }));

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should render information component when workbasket details is opened', () => {
    component.workbasket = { workbasketId: '1' };
    fixture.detectChanges();
    const information = debugElement.nativeElement.querySelector('taskana-administration-workbasket-information');
    expect(information).toBeTruthy();
  });

  it('should render new workbasket when action is CREATE', (done) => {
    store
      .dispatch(new CreateWorkbasket())
      .pipe(take(1))
      .subscribe(() => {
        component.selectedWorkbasketAndComponentAndAction$.pipe(take(1)).subscribe((state) => {
          expect(state.selectedWorkbasket.workbasketId).toBeUndefined();
          done();
        });
      });
  });

  it('should render copied workbasket when action is COPY', (done) => {
    const workbasket = component.workbasket;
    store
      .dispatch(new CopyWorkbasket(component.workbasket))
      .pipe(take(1))
      .subscribe(() => {
        component.selectedWorkbasketAndComponentAndAction$.pipe(take(1)).subscribe((state) => {
          const workbasketCopy = state.selectedWorkbasket;
          expect(workbasketCopy.workbasketId).toBeUndefined();
          expect(workbasketCopy.key).toEqual(workbasket.key);
          expect(workbasketCopy.owner).toEqual(workbasket.owner);
          done();
        });
      });
  });

  it('should render workbasket when action is READ', () => {
    store.reset({
      ...store.snapshot(),
      workbasket: workbasketReadState
    });
    fixture.detectChanges();
    expect(component.workbasket).not.toBeUndefined();
    expect(component.workbasket).not.toBeNull();
    expect(component.workbasket).toEqual(selectedWorkbasketMock);
  });

  it('should select information tab when action is CREATE', (done) => {
    component.selectComponent(1);
    store
      .dispatch(new CreateWorkbasket())
      .pipe(take(1))
      .subscribe(() => {
        component.selectedTab$.pipe(take(1)).subscribe((tab) => {
          expect(tab).toEqual(0);
          done();
        });
      });
  });
});
