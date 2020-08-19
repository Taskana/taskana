import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketListToolbarComponent } from './workbasket-list-toolbar.component';
import { Component, DebugElement, EventEmitter, Input, Output } from '@angular/core';
import { Actions, NgxsModule, ofActionDispatched, Store } from '@ngxs/store';
<<<<<<< HEAD
import { Observable } from 'rxjs';
=======
import { Observable, zip } from 'rxjs';
>>>>>>> 5ea9b0a0fbe229c629dcfb3c24bad4a497658c32
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { WorkbasketState } from '../../../shared/store/workbasket-store/workbasket.state';
import { WorkbasketService } from '../../../shared/services/workbasket/workbasket.service';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { CreateWorkbasket } from '../../../shared/store/workbasket-store/workbasket.actions';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Filter } from '../../../shared/models/filter';
import { Sorting } from '../../../shared/models/sorting';
import { ACTION } from '../../../shared/models/action';
import { TaskanaType } from '../../../shared/models/taskana-type';

const getDomainFn = jest.fn().mockReturnValue(true);
const domainServiceMock = jest.fn().mockImplementation(
  (): Partial<DomainService> => ({
    getDomains: getDomainFn
  })
);

@Component({ selector: 'taskana-administration-import-export', template: '' })
class ImportExportStub {
  @Input() currentSelection: TaskanaType;
}

@Component({ selector: 'taskana-shared-sort', template: '' })
class SortStub {
  @Input() sortingFields: Map<string, string>;
  @Input() defaultSortBy = 'key';
  @Output() performSorting = new EventEmitter<Sorting>();
}

@Component({ selector: 'taskana-shared-filter', template: '' })
class FilterStub {
  @Output() performFilter = new EventEmitter<Filter>();
}

describe('WorkbasketListToolbarComponent', () => {
  let fixture: ComponentFixture<WorkbasketListToolbarComponent>;
  let debugElement: DebugElement;
  let component: WorkbasketListToolbarComponent;
  let store: Store;
  let actions$: Observable<any>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        NgxsModule.forRoot([WorkbasketState]),
        MatSnackBarModule,
        MatDialogModule,
        BrowserAnimationsModule
      ],
      declarations: [WorkbasketListToolbarComponent, ImportExportStub, SortStub, FilterStub],
      providers: [{ provide: DomainService, useClass: domainServiceMock }, WorkbasketService]
    }).compileComponents();

    fixture = TestBed.createComponent(WorkbasketListToolbarComponent);
    debugElement = fixture.debugElement;
    component = fixture.debugElement.componentInstance;
    store = TestBed.inject(Store);
    actions$ = TestBed.inject(Actions);
    component.action = ACTION.COPY;
    fixture.detectChanges();
  }));

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should dispatch CreateWorkbasket when addWorkbasket is called', async((done) => {
    component.action = ACTION.COPY;
    let actionDispatched = false;
<<<<<<< HEAD
    actions$.pipe(ofActionDispatched(CreateWorkbasket)).subscribe(() => (actionDispatched = true));
=======
    zip(actions$.pipe(ofActionDispatched(CreateWorkbasket))).subscribe(() => (actionDispatched = true));
>>>>>>> 5ea9b0a0fbe229c629dcfb3c24bad4a497658c32
    component.addWorkbasket();
    expect(actionDispatched).toBe(true);
  }));

  it('should not dispatch action in addWorkbasket when action is CREATE', async((done) => {
    component.action = ACTION.CREATE;
    let actionDispatched = false;
<<<<<<< HEAD
    actions$.pipe(ofActionDispatched(CreateWorkbasket)).subscribe(() => (actionDispatched = true));
=======
    zip(actions$.pipe(ofActionDispatched(CreateWorkbasket))).subscribe(() => (actionDispatched = true));
>>>>>>> 5ea9b0a0fbe229c629dcfb3c24bad4a497658c32
    component.addWorkbasket();
    expect(actionDispatched).toBe(false);
  }));

  it('should emit value when sorting is called', (done) => {
    const mockSort: Sorting = { sortBy: '123', sortDirection: 'asc' };
    let sort: Sorting = { sortBy: '123', sortDirection: 'asc' };
    component.performSorting.subscribe((sortBy: Sorting) => {
      sort = sortBy;
      done();
    });
    component.sorting(sort);
    expect(sort).toMatchObject(mockSort);
  });

  it('should emit value when filtering is called', (done) => {
    const mockFilter: Filter = { filterParams: 'abc' };
    let filterBy: Filter = { filterParams: 'abc' };
    component.performFilter.subscribe((filter: Filter) => {
      filterBy = filter;
      done();
    });
    component.filtering(filterBy);
    expect(filterBy).toMatchObject(mockFilter);
  });
});
