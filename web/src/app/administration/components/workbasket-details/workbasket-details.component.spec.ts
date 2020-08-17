import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketDetailsComponent } from './workbasket-details.component';
import { DebugElement } from '@angular/core';
import { Actions, Store } from '@ngxs/store';
import { Observable } from 'rxjs';

describe('WorkbasketDetailsComponent', () => {
  let fixture: ComponentFixture<WorkbasketDetailsComponent>;
  let debugElement: DebugElement;
  let component: WorkbasketDetailsComponent;
  let store: Store;
  let actions$: Observable<any>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [],
      declarations: [WorkbasketDetailsComponent],
      providers: []
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
});
