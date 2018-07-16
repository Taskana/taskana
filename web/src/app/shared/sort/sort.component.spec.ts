import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SortComponent} from './sort.component';
import {configureTests} from 'app/app.test.configuration';
import {Direction} from 'app/models/sorting';

describe('SortComponent', () => {
  let component: SortComponent;
  let fixture: ComponentFixture<SortComponent>;
  let debugElement;

  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        declarations: []
      });
    };
    configureTests(configure).then(testBed => {
      fixture = TestBed.createComponent(SortComponent);
      component = fixture.componentInstance;
      debugElement = fixture.debugElement.nativeElement;
      fixture.detectChanges();
      done();
    });
  });

  afterEach(() => {
    document.body.removeChild(debugElement);
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
    component.sortingFields = new Map<string, string>([['name', 'Name']]);
    fixture.detectChanges();
  	expect(component.sort.sortBy).toBe('key');
  	debugElement.querySelector('#sort-by-name').click();
  	expect(component.sort.sortBy).toBe('name');
  });
});
