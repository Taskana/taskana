import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CategoriesadministrationComponent } from './categoriesadministration.component';

describe('CategoriesadministrattionComponent', () => {
  let component: CategoriesadministrationComponent;
  let fixture: ComponentFixture<CategoriesadministrationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CategoriesadministrationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CategoriesadministrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should be created', () => {
    expect(component).toBeTruthy();
  });
});
