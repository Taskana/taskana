import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';

import { FilterComponent } from './filter.component';
import { configureTests } from 'app/app.test.configuration';

describe('FilterComponent', () => {
  let component: FilterComponent,
    fixture: ComponentFixture<FilterComponent>,
    debugElement: any;


  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        declarations: [],
        imports: [AngularSvgIconModule, FormsModule, HttpClientModule]
      })
    };
    configureTests(configure).then(testBed => {
      fixture = TestBed.createComponent(FilterComponent);
      component = fixture.componentInstance;
      component.filterParams = {
        name: 'someName', owner: 'someOwner', description: 'someDescription',
        key: 'someKey', type: 'PERSONAL'
      };
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

  it('should create a component', () => {
    expect(debugElement.querySelector('#some-id')).toBeNull();
    fixture.detectChanges();
    expect(debugElement.querySelector('#some-id')).toBeDefined();
  });

  it('should have filter by: name, description, key, owner and type  defined', () => {
    expect(debugElement.querySelector('#display-name-filter')).toBeDefined();
    expect(debugElement.querySelector('#display-description-filter')).toBeDefined();
    expect(debugElement.querySelector('#display-key-filter')).toBeDefined();
    expect(debugElement.querySelector('#display-owner-filter')).toBeDefined();
    expect(debugElement.querySelector('#display-type-filter')).toBeDefined();
  });

  it('should be able to clear all fields after pressing clear button', () => {
    component.filterParams = {
      name: 'someName', owner: 'someOwner', description: 'someDescription',
      key: 'someKey', type: 'PERSONAL'
    };
    fixture.detectChanges();
    debugElement.querySelector('[title="Clear"]').click();
    expect(component.filter.filterParams.name).toBe('');
    expect(component.filter.filterParams.description).toBe('');
    expect(component.filter.filterParams.owner).toBe('');
    expect(component.filter.filterParams.type).toBe('');
    expect(component.filter.filterParams.key).toBe('');
  });

});
