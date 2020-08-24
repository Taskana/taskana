import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketDetailsComponent } from './workbasket-details.component';
import { Component, DebugElement, Input } from '@angular/core';
import { Actions, NgxsModule, Store } from '@ngxs/store';
import { Observable } from 'rxjs';
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
import { selectedWorkbasketMock } from '../../../shared/store/mock-data/mock-store';
import { ClassificationCategoriesService } from '../../../shared/services/classification-categories/classification-categories.service';
import { StartupService } from '../../../shared/services/startup/startup.service';
import { TaskanaEngineService } from '../../../shared/services/taskana-engine/taskana-engine.service';
import { WindowRefService } from '../../../shared/services/window/window.service';

@Component({ selector: 'taskana-shared-spinner', template: '' })
class SpinnerStub {
  @Input() isRunning: boolean;
}

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
}

@Component({ selector: 'taskana-administration-workbasket-distribution-targets', template: '' })
class WorkbasketDistributionTargetsStub {
  @Input() workbasket: Workbasket;
  @Input() action: ACTION;
  @Input() active: string;
}

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
        MatDialogModule
      ],
      declarations: [
        WorkbasketDetailsComponent,
        SpinnerStub,
        WorkbasketAccessItemsStub,
        WorkbasketDistributionTargetsStub,
        WorkbasketInformationStub
      ],
      providers: [
        DomainService,
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
    fixture.detectChanges();
  }));

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should display loading spinner while content loads', () => {
    component.requestInProgress = true;
    fixture.detectChanges();
    const spinner = debugElement.nativeElement.querySelector('taskana-shared-spinner');
    expect(spinner).toBeTruthy();
    expect(spinner.style.display).toContain('');
  });

  it('should render workbasket-details when workbasket exists and request is not in progress', () => {
    component.workbasket = { workbasketId: '1' };
    component.requestInProgress = false;
    fixture.detectChanges();
    const workbasketDetails = debugElement.nativeElement.querySelector('#workbasket-details');
    expect(workbasketDetails).toBeTruthy();
  });

  it('should render information, access items and distribution targets components', () => {
    component.workbasket = { workbasketId: '1' };
    component.requestInProgress = false;
    fixture.detectChanges();
    const information = debugElement.nativeElement.querySelector('taskana-administration-workbasket-information');
    const accessItems = debugElement.nativeElement.querySelector('taskana-administration-workbasket-access-items');
    const distributionTargets = debugElement.nativeElement.querySelector(
      'taskana-administration-workbasket-distribution-targets'
    );
    expect(information).toBeTruthy();
    expect(accessItems).toBeTruthy();
    expect(distributionTargets).toBeTruthy();
  });

  it('should render new workbasket when action is CREATE', () => {
    store.reset({
      ...store.snapshot(),
      workbasket: workbasketCreateState
    });
    fixture.detectChanges();
    expect(component.tabSelected).toMatch('information');
    expect(component.selectedId).toBeUndefined();
  });

  it('should render copied workbasket when action is COPY', () => {
    store.reset({
      ...store.snapshot(),
      workbasket: workbasketCopyState
    });
    component.ngOnInit();
    fixture.detectChanges();
    expect(component.workbasketCopy).toEqual(component.workbasket);
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

  it('should select information tab when action is CREATE', () => {
    component.action = ACTION.CREATE;
    component.selectTab('workbasket');
    expect(component.tabSelected).toEqual('information');
  });
});
