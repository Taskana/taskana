import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CategorieslistComponent } from './categorieslist.component';

describe('CategorieslistComponent', () => {
  let component: CategorieslistComponent;
  let fixture: ComponentFixture<CategorieslistComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CategorieslistComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CategorieslistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should be created', () => {
    expect(component).toBeTruthy();
  });
});
