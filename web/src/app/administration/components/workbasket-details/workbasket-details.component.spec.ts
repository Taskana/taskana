import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketDetailsComponent } from './workbasket-details.component';
import { DebugElement } from '@angular/core';
import { Store } from '@ngxs/store';
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
  }));
});
