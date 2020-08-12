import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketListToolbarComponent } from './workbasket-list-toolbar.component';
import { CUSTOM_ELEMENTS_SCHEMA, DebugElement } from '@angular/core';
import { Actions, NgxsModule, ofActionDispatched, Store } from '@ngxs/store';
import { Observable, zip } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { WorkbasketState } from '../../../shared/store/workbasket-store/workbasket.state';
import { WorkbasketService } from '../../../shared/services/workbasket/workbasket.service';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { CreateWorkbasket, DeselectWorkbasket } from '../../../shared/store/workbasket-store/workbasket.actions';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Filter } from '../../../shared/models/filter';
import { Sorting } from '../../../shared/models/sorting';
import { ACTION } from '../../../shared/models/action';

const getDomainFn = jest.fn().mockReturnValue(true);
const domainServiceMock = jest.fn().mockImplementation(
  (): Partial<DomainService> => ({
    getDomains: getDomainFn
  })
);

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
      declarations: [WorkbasketListToolbarComponent],
      providers: [{ provide: DomainService, useClass: domainServiceMock }, WorkbasketService],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(WorkbasketListToolbarComponent);
    debugElement = fixture.debugElement;
    component = fixture.debugElement.componentInstance;
    store = TestBed.inject(Store);
    actions$ = TestBed.inject(Actions);
    component.action = ACTION.COPY;
  }));

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should dispatch CreateWorkbasket when addWorkbasket is called', async((done) => {
    component.addWorkbasket();
    let actionDispatched = false;
    actions$.pipe(ofActionDispatched(CreateWorkbasket)).subscribe(() => {
      actionDispatched = true;
      expect(actionDispatched).toBe(true);
      done();
    });
  }));

  it('should not do anything in addWorkbasket if action is create ', async((done) => {
    component.action = ACTION.CREATE;
    fixture.detectChanges();
    component.addWorkbasket();
    let actionDispatched = false;
    actions$.pipe(ofActionDispatched(CreateWorkbasket)).subscribe(() => {
      actionDispatched = true;
      expect(actionDispatched).toBe(false);
      done();
    });
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
