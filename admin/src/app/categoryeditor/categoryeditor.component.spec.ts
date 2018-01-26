import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CategoryeditorComponent } from './categoryeditor.component';

describe('CategoryeditorComponent', () => {
  let component: CategoryeditorComponent;
  let fixture: ComponentFixture<CategoryeditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CategoryeditorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CategoryeditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should be created', () => {
    expect(component).toBeTruthy();
  });
});
