import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { PaginationComponent } from './pagination.component';
import { DebugElement } from '@angular/core';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('PaginationComponent', () => {
  let fixture: ComponentFixture<PaginationComponent>;
  let debugElement: DebugElement;
  let component: PaginationComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        MatPaginatorModule,
        MatAutocompleteModule,
        FormsModule,
        MatFormFieldModule,
        MatInputModule,
        NoopAnimationsModule
      ],
      declarations: [PaginationComponent],
      providers: []
    }).compileComponents();

    fixture = TestBed.createComponent(PaginationComponent);
    debugElement = fixture.debugElement;
    component = fixture.componentInstance;
    fixture.detectChanges();

    component.page = { totalPages: 10 };
    component.pageNumbers = [];
    for (let i = 1; i <= component.page.totalPages; i++) {
      component.pageNumbers.push(i);
    }
  }));

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should suggest page 3 when filter() is called with 3', () => {
    component.filter(3);
    expect(component.filteredPages).toEqual(['3']);
  });

  it('should suggest all pages when input of filter() is out of range', () => {
    component.filter(11);
    expect(component.filteredPages).toEqual(component.pageNumbers.map(String));
    component.filter(-1);
    expect(component.filteredPages).toEqual(component.pageNumbers.map(String));
  });

  it('should suggest all pages when input of filter() is not a number', () => {
    component.filter('abc');
    expect(component.filteredPages).toEqual(component.pageNumbers.map(String));
    component.filter('');
    expect(component.filteredPages).toEqual(component.pageNumbers.map(String));
  });
});
