import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MapValuesPipe } from '../../pipes/map-values.pipe';

import { SortComponent, Direction } from './sort.component';

describe('SortComponent', () => {
  let component: SortComponent;
  let fixture: ComponentFixture<SortComponent>;
  let debugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SortComponent, MapValuesPipe ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SortComponent);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement.nativeElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should change order when click on order ', () => {
    expect(component.sort.sortDirection).toBe(Direction.ASC);
    debugElement.querySelector('#sort-by-direction-desc').click();
    expect(component.sort.sortDirection).toBe(Direction.DESC);
  });

  it('should change sort by when click on sort by ', () => {
    expect(component.sort.sortBy).toBe('key');
    debugElement.querySelector('#sort-by-name').click();
    expect(component.sort.sortBy).toBe('name');
  });
});
