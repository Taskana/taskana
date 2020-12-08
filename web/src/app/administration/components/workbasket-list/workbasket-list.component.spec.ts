import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketListComponent } from './workbasket-list.component';
import { Component, DebugElement, EventEmitter, Input, Output } from '@angular/core';
import { Actions, NgxsModule, ofActionDispatched, Store } from '@ngxs/store';
import { Observable, of } from 'rxjs';
import { WorkbasketState } from '../../../shared/store/workbasket-store/workbasket.state';
import { WorkbasketService } from '../../../shared/services/workbasket/workbasket.service';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { OrientationService } from '../../../shared/services/orientation/orientation.service';
import { ImportExportService } from '../../services/import-export.service';
import { DeselectWorkbasket, SelectWorkbasket } from '../../../shared/store/workbasket-store/workbasket.actions';
import { TaskanaQueryParameters } from '../../../shared/util/query-parameters';
import { WorkbasketSummary } from '../../../shared/models/workbasket-summary';
import { Sorting } from '../../../shared/models/sorting';
import { Filter } from '../../../shared/models/filter';
import { ICONTYPES } from '../../../shared/models/icon-types';
import { Page } from '../../../shared/models/page';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { MatListModule } from '@angular/material/list';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { RouterTestingModule } from '@angular/router/testing';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { selectedWorkbasketMock } from '../../../shared/store/mock-data/mock-store';

const workbasketSavedTriggeredFn = jest.fn().mockReturnValue(of(1));
const workbasketSummaryFn = jest.fn().mockReturnValue(of({}));
const getWorkbasketFn = jest.fn().mockReturnValue(of(selectedWorkbasketMock));
const getWorkbasketActionToolbarExpansionFn = jest.fn().mockReturnValue(of(false));
const workbasketServiceMock = jest.fn().mockImplementation(
  (): Partial<WorkbasketService> => ({
    workbasketSavedTriggered: workbasketSavedTriggeredFn,
    getWorkBasketsSummary: workbasketSummaryFn,
    getWorkBasket: getWorkbasketFn,
    getWorkbasketActionToolbarExpansion: getWorkbasketActionToolbarExpansionFn,
    getWorkBasketAccessItems: jest.fn().mockReturnValue(of({})),
    getWorkBasketsDistributionTargets: jest.fn().mockReturnValue(of({}))
  })
);

const getOrientationFn = jest.fn().mockReturnValue(of('landscape'));
const orientationServiceMock = jest.fn().mockImplementation(
  (): Partial<OrientationService> => ({
    getOrientation: getOrientationFn,
    calculateNumberItemsList: jest.fn().mockReturnValue(1920)
  })
);

const getImportingFinishedFn = jest.fn().mockReturnValue(of(true));
const importExportServiceMock = jest.fn().mockImplementation(
  (): Partial<ImportExportService> => ({
    getImportingFinished: getImportingFinishedFn
  })
);

const domainServiceSpy = jest.fn().mockImplementation(
  (): Partial<DomainService> => ({
    getSelectedDomainValue: jest.fn().mockReturnValue(of()),
    getSelectedDomain: jest.fn().mockReturnValue(of())
  })
);

const requestInProgressServiceSpy = jest.fn().mockImplementation(
  (): Partial<RequestInProgressService> => ({
    setRequestInProgress: jest.fn().mockReturnValue(of()),
    getRequestInProgress: jest.fn().mockReturnValue(of(false))
  })
);

@Component({ selector: 'taskana-administration-workbasket-list-toolbar', template: '' })
class WorkbasketListToolbarStub {
  @Input() workbaskets: Array<WorkbasketSummary>;
  @Input() workbasketDefaultSortBy: string;
  @Output() performSorting = new EventEmitter<Sorting>();
  @Output() performFilter = new EventEmitter<Filter>();
}

@Component({ selector: 'taskana-administration-icon-type', template: '' })
class IconTypeStub {
  @Input() type: ICONTYPES = ICONTYPES.ALL;
  @Input() selected = false;
}

@Component({ selector: 'taskana-shared-pagination', template: '' })
class PaginationStub {
  @Input() page: Page;
  @Input() type: String;
  @Output() changePage = new EventEmitter<number>();
  @Input() numberOfItems: number;
}

@Component({ selector: 'svg-icon', template: '' })
class SvgIconStub {}

describe('WorkbasketListComponent', () => {
  let fixture: ComponentFixture<WorkbasketListComponent>;
  let debugElement: DebugElement;
  let component: WorkbasketListComponent;
  let store: Store;
  let actions$: Observable<any>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgxsModule.forRoot([WorkbasketState]),
        RouterTestingModule,
        MatSnackBarModule,
        MatDialogModule,
        FormsModule,
        MatProgressBarModule,
        MatSelectModule,
        MatListModule
      ],
      declarations: [WorkbasketListComponent, WorkbasketListToolbarStub, IconTypeStub, PaginationStub, SvgIconStub],
      providers: [
        { provide: WorkbasketService, useClass: workbasketServiceMock },
        { provide: OrientationService, useClass: orientationServiceMock },
        { provide: ImportExportService, useClass: importExportServiceMock },
        { provide: DomainService, useClass: domainServiceSpy },
        { provide: RequestInProgressService, useClass: requestInProgressServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(WorkbasketListComponent);
    debugElement = fixture.debugElement;
    component = fixture.componentInstance;
    store = TestBed.inject(Store);
    actions$ = TestBed.inject(Actions);
    fixture.detectChanges();
  }));

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should dispatch SelectWorkbasket when selecting a workbasket', async((done) => {
    component.selectedId = undefined;
    fixture.detectChanges();
    let actionDispatched = false;
    actions$.pipe(ofActionDispatched(SelectWorkbasket)).subscribe(() => (actionDispatched = true));
    component.selectWorkbasket('WBI:000000000000000000000000000000000902');
    expect(actionDispatched).toBe(true);
  }));

  it('should dispatch DeselectWorkbasket when selecting a workbasket again', async((done) => {
    component.selectedId = '123';
    fixture.detectChanges();
    let actionDispatched = false;
    actions$.pipe(ofActionDispatched(DeselectWorkbasket)).subscribe(() => (actionDispatched = true));
    const mockId = '123';
    component.selectWorkbasket(mockId);
    expect(actionDispatched).toBe(true);
    expect(component.selectedId).toEqual(undefined); //because Deselect action sets selectedId to undefined
  }));

  it('should set sort value when performSorting is called', () => {
    const sort = { sortBy: '1', sortDirection: 'asc' };
    component.performSorting(sort);
    expect(component.sort).toMatchObject(sort);
  });

  it('should set filter value when performFilter is called', () => {
    const filter = { filterParams: '123' };
    component.performFilter(filter);
    expect(component.filterBy).toMatchObject(filter);
  });

  it('should change page value when change page function is called ', () => {
    const page = 2;
    component.changePage(page);
    expect(TaskanaQueryParameters.page).toBe(page);
  });
});
