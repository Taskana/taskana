import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketListComponent } from './workbasket-list.component';
import { CUSTOM_ELEMENTS_SCHEMA, DebugElement } from '@angular/core';
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

const workbasketSavedTriggeredFn = jest.fn().mockReturnValue(of(1));
const workbasketSummaryFn = jest.fn().mockReturnValue(of({}));
const getWorkbasketFn = jest.fn().mockReturnValue(of({ workbasketId: '1' }));
const workbasketServiceMock = jest.fn().mockImplementation(
  (): Partial<WorkbasketService> => ({
    workbasketSavedTriggered: workbasketSavedTriggeredFn,
    getWorkBasketsSummary: workbasketSummaryFn,
    getWorkBasket: getWorkbasketFn
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

describe('WorkbasketListComponent', () => {
  let fixture: ComponentFixture<WorkbasketListComponent>;
  let debugElement: DebugElement;
  let component: WorkbasketListComponent;
  let store: Store;
  let actions$: Observable<any>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [NgxsModule.forRoot([WorkbasketState]), MatSnackBarModule, MatDialogModule],
      declarations: [WorkbasketListComponent],
      providers: [
        { provide: WorkbasketService, useClass: workbasketServiceMock },
        { provide: OrientationService, useClass: orientationServiceMock },
        { provide: ImportExportService, useClass: importExportServiceMock }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
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
    component.selectWorkbasket('1');
    actions$.pipe(ofActionDispatched(SelectWorkbasket)).subscribe(async (action) => {
      expect(action).toBeTruthy();
      done();
    });
  }));

  it('should dispatch DeselectWorkbasket when selecting a workbasket again', async((done) => {
    component.selectedId = '1';
    component.selectWorkbasket('1');
    actions$.pipe(ofActionDispatched(DeselectWorkbasket)).subscribe(async (action) => {
      expect(action).toBeTruthy();
      done();
    });
  }));

  it('performSorting should set sort value', () => {
    const sort = { sortBy: '1', sortDirection: 'asc' };
    component.performSorting(sort);
    expect(component.sort).toMatchObject(sort);
  });

  it('performFilter should set filter value', () => {
    const filter = { filterParams: '123' };
    component.performFilter(filter);
    expect(component.filterBy).toMatchObject(filter);
  });

  it('change page function should change page value', () => {
    const page = 2;
    component.changePage(page);
    expect(TaskanaQueryParameters.page).toBe(page);
  });
});
